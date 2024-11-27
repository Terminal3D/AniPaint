package com.example.coursework.common.database.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter


class Converters {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(IntArray::class.java, IntArrayTypeAdapter())
        .create()

    @TypeConverter
    fun fromListIntArray(list: List<IntArray>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toListIntArray(data: String): List<IntArray> {
        val listType = object : TypeToken<List<IntArray>>() {}.type
        return gson.fromJson(data, listType)
    }
}

class IntArrayTypeAdapter : TypeAdapter<IntArray>() {
    override fun write(out: JsonWriter, value: IntArray) {
        out.beginArray()
        for (i in value) {
            out.value(i)
        }
        out.endArray()
    }

    override fun read(reader: JsonReader): IntArray {
        val list = mutableListOf<Int>()
        reader.beginArray()
        while (reader.hasNext()) {
            list.add(reader.nextInt())
        }
        reader.endArray()
        return list.toIntArray()
    }
}