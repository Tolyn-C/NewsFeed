package com.tolyn.newsfeed.service

import com.tolyn.newsfeed.App
import com.tolyn.newsfeed.data.NewsInfo
import org.kodein.di.instance

class MockNewsFeedService: NewsFeedService {
    override fun getTotalNewsList(): List<NewsInfo.News> {
        val newsList by App.di.instance<NewsInfo.NewsList>()
        return newsList.newsList
    }

    override fun updateNewsProvider() {
        TODO("Not yet implemented")
    }
}