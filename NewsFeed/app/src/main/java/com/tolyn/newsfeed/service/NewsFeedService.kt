package com.tolyn.newsfeed.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.tolyn.newsfeed.data.NewsCategory
import com.tolyn.newsfeed.data.NewsInfo
import com.tolyn.newsfeed.data.NewsProvider
import kotlinx.coroutines.flow.StateFlow

enum class TimeDateSort { Descending, Ascending }

interface NewsFeedService {
    interface Provider {
        fun provide(): NewsFeedService
    }

    val newsList: StateFlow<List<NewsInfo.NewsItem>>
    val newsTimeDateSort: StateFlow<TimeDateSort>
    val newsProviderFilter: StateFlow<List<NewsProvider>>
    val newsCategoryFilter: StateFlow<List<NewsCategory>>
    val newsProviderSubscribe: StateFlow<List<NewsProvider>>

    fun updateNewsTimeDateSort(sort: TimeDateSort)

    fun updateNewsProviderFilter(filterList: List<NewsProvider>)

    fun updateNwsCategoryFilter(filterList: List<NewsCategory>)

    fun updateNwsProviderSubscribe(filterList: List<NewsProvider>)

    fun updateNewsItemIsRead(id: Long)
}

class NewsFeedProviderService : Service(), NewsFeedService.Provider {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getNewsFeedService(): NewsFeedProviderService {
            return this@NewsFeedProviderService
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun provide(): NewsFeedService {
        return MockNewsFeedService()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

}