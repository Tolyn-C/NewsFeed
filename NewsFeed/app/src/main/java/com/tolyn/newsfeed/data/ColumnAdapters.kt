package com.tolyn.newsfeed.data


import com.google.gson.Gson
import com.squareup.sqldelight.ColumnAdapter
import java.util.*
import kotlin.collections.ArrayList

val stringMapAdapter = object : ColumnAdapter<Map<String, String>, String> {
    override fun decode(databaseValue: String): Map<String, String> {
        return (Gson()).fromJson(databaseValue, Map::class.java) as Map<String, String>
    }

    override fun encode(value: Map<String, String>): String {
        return (Gson()).toJson(value)
    }
}

val dateCalendarAdapter = object : ColumnAdapter<Date, Long> {
    override fun decode(databaseValue: Long): Date {
        return Date().also {
            it.time = databaseValue
        }
    }

    override fun encode(value: Date): Long {
        return value.time
    }
}

val listOfStringsAdapter = object : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String): List<String> {
        val arrayList : ArrayList<String> = ArrayList()
        databaseValue.split(",").forEachIndexed { index, s ->
            if(s.isNotBlank()){
                arrayList.add(s)
            }
        }
        return arrayList
    }
    override fun encode(value: List<String>) = value.joinToString(separator = ",")
}
