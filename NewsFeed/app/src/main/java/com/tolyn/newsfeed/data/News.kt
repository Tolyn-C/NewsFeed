package com.tolyn.newsfeed.data

import com.google.gson.Gson
import java.util.logging.Level
import java.util.logging.Logger

enum class NewsCategory {
    Crime, Business, Cars, Entertainment, Family, Health, Politics, Religion, Science
}

enum class NewsProvider(val key: String) {
    DailyTimes("Daily Times"), NewsExpress("News Express"), DailyBugle("Daily Bugle"), NewNews("New News"), NewsNow(
        "News Now"
    )
}

class NewsInfo(private val jsonString: String) {

    private val parserNewsList: NewsList by lazy {
        Logger.getGlobal().log(Level.ALL, "jsonString ${jsonString.length}")
        if (jsonString.isBlank()) {
            NewsList(emptyList())
        } else {
            Gson().fromJson(jsonString, NewsList::class.java)
        }
    }

    fun getNewsList(): NewsList {
        return parserNewsList
    }

    data class NewsList(val newsList: List<News>)

    data class News(
        val provider: String,
        val category: String,
        val title: String,
        val subtitle: String,
        val author: String,
        val time: String,
        val descriptiveStory: String,
        val url: String
    )
}