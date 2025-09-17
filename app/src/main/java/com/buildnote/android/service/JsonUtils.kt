package com.buildnote.android.service

import org.json.JSONArray
import org.json.JSONObject
import com.buildnote.android.model.AreaEntry
import com.buildnote.android.model.LengthEntry
import com.buildnote.android.model.RoomEntry

// Map -> JSONObject (rekursiv)
fun Map<String, *>.toJSONObject(): JSONObject {
    val obj = JSONObject()
    for ((k, v) in this) obj.put(k, v.toJsonValue())
    return obj
}

// List/Iterable/Array -> JSONArray (rekursiv)
fun Iterable<*>.toJSONArray(): JSONArray {
    val arr = JSONArray()
    for (v in this) arr.put(v.toJsonValue())
    return arr
}

private fun Any?.toJsonValue(): Any? = when (this) {
    null -> JSONObject.NULL
    is JSONObject, is JSONArray -> this
    is String, is Number, is Boolean -> this
    is Map<*, *> -> (this as Map<String, *>).toJSONObject() // Keys müssen String sein
    is Iterable<*> -> this.toJSONArray()
    is Array<*> -> this.asList().toJSONArray()
    else -> this.toString() // Fallback: als String (ohne Reflection)
}
fun JSONObject.toLengthEntry(): LengthEntry =
    LengthEntry(
        description = this.optString("description"),
        length = if (this.has("length") && !this.isNull("length")) this.optDouble("length") else null,
        includeDeduction = has("deductionLength") && !isNull("deductionLength"),
        deductionLength = if (this.has("deductionLength") && !this.isNull("deductionLength")) this.optDouble("deductionLength") else null
    )

// Liste parsen
fun JSONArray.toLengthEntryList(): MutableList<LengthEntry> {
    val list = mutableListOf<LengthEntry>()
    for (i in 0 until this.length()) {
        val obj = this.optJSONObject(i) ?: continue
        list.add(obj.toLengthEntry())
    }
    return list
}
fun JSONObject.toAreaEntry(): AreaEntry =
    AreaEntry(
        description = optString("description"),
        length = if (has("length") && !isNull("length")) optDouble("length") else null,
        width = if (has("width") && !isNull("width")) optDouble("width") else null,
        includeDeduction = has("deductionLength") && !isNull("deductionLength") || has("deductionWidth") && !isNull("deductionWidth"),
        deductionLength = if (has("deductionLength") && !isNull("deductionLength")) optDouble("deductionLength") else null,
        deductionWidth = if (has("deductionWidth") && !isNull("deductionWidth")) optDouble("deductionWidth") else null
    )

fun JSONArray.toAreaEntryList(): MutableList<AreaEntry> {
    val list = mutableListOf<AreaEntry>()
    for (i in 0 until length()) {
        val obj = optJSONObject(i) ?: continue
        list.add(obj.toAreaEntry())
    }
    return list
}

// ---------- RoomEntry ----------
fun JSONObject.toRoomEntry(): RoomEntry =
    RoomEntry(
        description = optString("description"),
        length = if (has("length") && !isNull("length")) optDouble("length") else null,
        width = if (has("width") && !isNull("width")) optDouble("width") else null,
        height = if (has("height") && !isNull("height")) optDouble("height") else null,
        includeDeduction = has("deductionLength") && !isNull("deductionLength") || has("deductionWidth") && !isNull("deductionWidth") || has("deductionHeight") && !isNull("deductionHeight") ,
        deductionLength = if (has("deductionLength") && !isNull("deductionLength")) optDouble("deductionLength") else null,
        deductionWidth = if (has("deductionWidth") && !isNull("deductionWidth")) optDouble("deductionWidth") else null,
        deductionHeight = if (has("deductionHeight") && !isNull("deductionHeight")) optDouble("deductionHeight") else null
    )

fun JSONArray.toRoomEntryList(): MutableList<RoomEntry> {
    val list = mutableListOf<RoomEntry>()
    for (i in 0 until length()) {
        val obj = optJSONObject(i) ?: continue
        list.add(obj.toRoomEntry())
    }
    return list
}

// Mini-Builder-DLS
object Json {
    fun obj(vararg pairs: Pair<String, Any?>): JSONObject =
        pairs.toMap().toJSONObject()
    fun arr(vararg items: Any?): JSONArray =
        items.asList().toJSONArray()
}


