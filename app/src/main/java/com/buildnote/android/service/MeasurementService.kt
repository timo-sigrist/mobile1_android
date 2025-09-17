package com.buildnote.android.service

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.buildnote.android.model.AreaEntry
import com.buildnote.android.model.LengthEntry
import com.buildnote.android.model.Material
import com.buildnote.android.model.MeasurementRecord
import com.buildnote.android.model.LengthUnit
import com.buildnote.android.model.AreaUnit
import com.buildnote.android.model.RoomUnit
import com.buildnote.android.model.MeasurementType
import com.buildnote.android.model.RoomEntry
import com.buildnote.android.service.JsonBuildnoteParser.parseMeasurements
import org.json.JSONObject

import kotlin.apply

class MeasurementService private constructor(
    queue: RequestQueue,
): BaseService(queue){
    companion object {
        private val endpoint = "${defaultBaseUrl()}/measurement_record"

        fun create(context: Context): MeasurementService {
            return MeasurementService(
                queue = createQueue(context)
            )
        }
    }

    fun getMeasurementsForProject(
        projectId: Long,
        onResult: (MutableList<MeasurementRecord>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val requestUrl = "$endpoint/getByProject/${projectId}"
        val request = Utf8StringRequest(
            Request.Method.GET,
            requestUrl,
            { response ->
                Log.d("MeasurementService", "response: $response")
                try {
                    onResult(parseMeasurements(response, projectId).toMutableList())
                } catch (e: Exception) {
                    onError(e)
                }
            },
            { error ->
                Log.e("MeasurementService", "Error loading data: $error")
                onError(error)
            }
        )
        queue.add(request)
    }


    fun postMeasurement(
        measurement: MeasurementRecord,
        onResult: (MeasurementRecord) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val body = Json.obj(
            "name" to measurement.name,
            "notes" to measurement.notes,
            "total" to measurement.total,
            "projectId" to measurement.projectId,
            "userId" to measurement.userId
        )

        when (measurement.measurementType) {
            MeasurementType.LENGTH -> {
                body.put("lengthUnit", measurement.lengthUnit)

                val entriesJson = measurement.lengthEntries.map { entry ->
                    Json.obj(
                        "description" to entry.description,
                        "length" to entry.length,
                        "deductionLength" to entry.deductionLength
                    )
                }.toJSONArray()

                body.put("lengthEntries", entriesJson)
            }
            MeasurementType.AREA -> {
                body.put("areaUnit", measurement.areaUnit)

                val entriesJson = measurement.areaEntries.map { entry ->
                    Json.obj(
                        "description" to entry.description,
                        "length" to entry.length,
                        "width" to entry.width,
                        "deductionLength" to entry.deductionLength,
                        "deductionWidth" to entry.deductionWidth
                    )
                }.toJSONArray()

                body.put("areaEntries", entriesJson)
            }
            MeasurementType.ROOM -> {
                body.put("roomUnit", measurement.roomUnit)

                val entriesJson = measurement.roomEntries.map { entry ->
                    Json.obj(
                        "description" to entry.description,
                        "length" to entry.length,
                        "width" to entry.width,
                        "height" to entry.height,
                        "deductionLength" to entry.deductionLength,
                        "deductionWidth" to entry.deductionWidth,
                        "deductionHeight" to entry.deductionLength
                    )
                }.toJSONArray()

                body.put("roomEntries", entriesJson)
            }
        }

        post(
            endpoint = "measurement_record/create",
            bodyJson = body,
            parse = { json ->
                val lengthEntries = json.optJSONArray("lengthEntries")?.toLengthEntryList() ?: mutableListOf()
                val areaEntries   = json.optJSONArray("areaEntries")?.toAreaEntryList() ?: mutableListOf()
                val roomEntries   = json.optJSONArray("roomEntries")?.toRoomEntryList() ?: mutableListOf()

                val measurementType: MeasurementType
                if (lengthEntries.size > 0)
                    measurementType = MeasurementType.LENGTH
                else if (areaEntries.size > 0)
                    measurementType = MeasurementType.AREA
                else
                    measurementType = MeasurementType.ROOM

                MeasurementRecord(
                    name = json.optString("name"),
                    description = json.optString("description"),
                    notes = json.optString("notes"),
                    total = json.optDouble("total", 0.0),
                    measurementType = measurementType,
                    lengthUnit = LengthUnit.fromString(json.optString("lengthUnit")),
                    areaUnit = AreaUnit.fromString(json.optString("areaUnit")),
                    roomUnit = RoomUnit.fromString(json.optString("roomUnit")),
                    lengthEntries = lengthEntries,
                    areaEntries = areaEntries,
                    roomEntries = roomEntries,
                    projectId = json.optLong("projectId"),
                    userId = json.optInt("userId")
                )
            },
            onResult = onResult,
            onError = onError
        )
    }
}

