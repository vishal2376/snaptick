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
import com.vishal2376.snaptick.widget.model.WidgetTaskModel
import com.vishal2376.snaptick.widget.util.LocalTimeGsonSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.time.LocalTime


object SnaptickWidgetStateDefinition
	: GlanceStateDefinition<SnaptickWidgetState> {

	private const val FILE_NAME = "_WIDGET_DATA_DEFINITION"

	private val Context.widgetData by dataStore(FILE_NAME, TaskSerializers)

	override suspend fun getDataStore(context: Context, fileKey: String)
			: DataStore<SnaptickWidgetState> = context.widgetData

	override fun getLocation(context: Context, fileKey: String)
			: File = context.dataStoreFile(FILE_NAME)


	suspend fun updateData(context: Context, newTasks: List<WidgetTaskModel>) =
		withContext(Dispatchers.IO) {
			context.widgetData.updateData { state -> state.copy(tasks = newTasks) }
		}


	private object TaskSerializers : Serializer<SnaptickWidgetState> {

		private val gson = GsonBuilder()
			.serializeNulls()
			.registerTypeAdapter(LocalTime::class.java, LocalTimeGsonSerializer)
			.create()


		override val defaultValue: SnaptickWidgetState
			get() = SnaptickWidgetState()

		override suspend fun readFrom(input: InputStream): SnaptickWidgetState {
			return try {
				input.use { stream ->
					val reader = stream.reader()
					gson.fromJson(reader, SnaptickWidgetState::class.java)
				}
			} catch (e: JsonParseException) {
				e.printStackTrace()
				throw CorruptionException("Could not read JSON: ${e.message}", e)
			}
		}

		override suspend fun writeTo(t: SnaptickWidgetState, output: OutputStream) {
			output.use { stream ->
				try {
					val writer = stream.bufferedWriter()
					val jsonData = gson.toJson(t, SnaptickWidgetState::class.java)
					writer.write(jsonData)
				} catch (e: JsonParseException) {
					throw CorruptionException("Could not convert to JSON: ${e.message}", e)
				}
			}
		}
	}
}