package com.vishal2376.snaptick.widget.state

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.vishal2376.snaptick.widget.model.WidgetState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalTime

/**
 * GlanceStateDefinition for the Snaptick widget.
 * Handles persistence of WidgetState via DataStore with JSON serialization.
 */
object WidgetStateDefinition : GlanceStateDefinition<WidgetState> {

	private const val FILE_NAME = "snaptick_widget_state"

	private val Context.widgetDataStore by dataStore(FILE_NAME, WidgetStateSerializer)

	override suspend fun getDataStore(
		context: Context,
		fileKey: String
	): DataStore<WidgetState> = context.widgetDataStore

	override fun getLocation(context: Context, fileKey: String): File =
		context.dataStoreFile(FILE_NAME)

	/**
	 * Updates the widget state with new data.
	 * Called by WidgetUpdateWorker after fetching tasks and settings.
	 */
	suspend fun updateState(context: Context, newState: WidgetState) =
		withContext(Dispatchers.IO) {
			context.widgetDataStore.updateData { newState }
		}

	private object WidgetStateSerializer : Serializer<WidgetState> {

		private val gson = GsonBuilder()
			.serializeNulls()
			.registerTypeAdapter(LocalTime::class.java, LocalTimeAdapter)
			.registerTypeAdapter(LocalDate::class.java, LocalDateAdapter)
			.create()

		override val defaultValue: WidgetState = WidgetState()

		override suspend fun readFrom(input: InputStream): WidgetState {
			return try {
				input.use { stream ->
					val reader = stream.reader()
					gson.fromJson(reader, WidgetState::class.java) ?: defaultValue
				}
			} catch (e: JsonParseException) {
				e.printStackTrace()
				throw CorruptionException("Could not read widget state JSON: ${e.message}", e)
			}
		}

		override suspend fun writeTo(t: WidgetState, output: OutputStream) {
			output.use { stream ->
				try {
					val writer = stream.bufferedWriter()
					val jsonData = gson.toJson(t, WidgetState::class.java)
					writer.write(jsonData)
					writer.flush()
				} catch (e: JsonParseException) {
					throw CorruptionException("Could not write widget state to JSON: ${e.message}", e)
				}
			}
		}
	}

	/**
	 * Gson adapter for LocalTime serialization/deserialization.
	 */
	private object LocalTimeAdapter : JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
		override fun serialize(
			src: LocalTime?,
			typeOfSrc: Type?,
			context: JsonSerializationContext?
		): JsonElement = JsonPrimitive(src?.toString())

		override fun deserialize(
			json: JsonElement?,
			typeOfT: Type?,
			context: JsonDeserializationContext?
		): LocalTime = LocalTime.parse(json?.asString)
	}

	/**
	 * Gson adapter for LocalDate serialization/deserialization.
	 */
	private object LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
		override fun serialize(
			src: LocalDate?,
			typeOfSrc: Type?,
			context: JsonSerializationContext?
		): JsonElement = JsonPrimitive(src?.toString())

		override fun deserialize(
			json: JsonElement?,
			typeOfT: Type?,
			context: JsonDeserializationContext?
		): LocalDate = LocalDate.parse(json?.asString)
	}
}
