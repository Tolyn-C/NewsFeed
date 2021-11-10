package com.hyenatest.newsfeed.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.hyenatest.newsfeed.data.NewsInfo

interface NewsFeedService {
    interface Provider {
        fun provide(): NewsFeedService
    }

    fun getTotalNewsList(): List<NewsInfo.News>

    fun updateNewsProvider()
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

}