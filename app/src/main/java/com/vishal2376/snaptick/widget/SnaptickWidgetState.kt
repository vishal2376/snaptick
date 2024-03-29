package com.vishal2376.snaptick.widget

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import com.vishal2376.snaptick.widget.model.WidgetTaskModel
import com.vishal2376.snaptick.widget.util.LocalTimeGsonSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalTime

object SnaptickWidgetState : GlanceStateDefinition<List<WidgetTaskModel>> {

	private const val FILE_NAME = "_WIDGET_DATA_DEFINITION"

	private val Context.widgetData by dataStore(FILE_NAME, TaskSerializers)

	override suspend fun getDataStore(context: Context, fileKey: String)
			: DataStore<List<WidgetTaskModel>> = context.widgetData

	override fun getLocation(context: Context, fileKey: String)
			: File = context.dataStoreFile(FILE_NAME)


	suspend fun updateData(context: Context, newTasks: List<WidgetTaskModel>) =
		withContext(Dispatchers.IO) {
			context.widgetData.updateData { newTasks }
		}


	private object TaskSerializers : Serializer<List<WidgetTaskModel>> {

		private val gson = GsonBuilder()
			.serializeNulls()
			.registerTypeAdapter(LocalTime::class.java, LocalTimeGsonSerializer)
			.create()

		private val kSerializer = object : TypeToken<ArrayList<WidgetTaskModel>>() {}.type

		override val defaultValue: List<WidgetTaskModel>
			get() = emptyList()

		override suspend fun readFrom(input: InputStream): List<WidgetTaskModel> {
			return try {
				input.use { stream ->
					val reader = stream.reader()
					gson.fromJson(reader, kSerializer)
				}
			} catch (e: JsonParseException) {
				e.printStackTrace()
				throw CorruptionException("Could not read JSON: ${e.message}", e)
			}
		}

		override suspend fun writeTo(t: List<WidgetTaskModel>, output: OutputStream) {
			output.use { stream ->
				try {
					val writer = stream.bufferedWriter()
					val jsonData = gson.toJson(t, kSerializer)
					writer.write(jsonData)
				} catch (e: JsonParseException) {
					throw CorruptionException("Could not convert to JSON: ${e.message}", e)
				}
			}
		}
	}
}