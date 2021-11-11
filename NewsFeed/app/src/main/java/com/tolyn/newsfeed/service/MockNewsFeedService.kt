package com.tolyn.newsfeed.service

import com.tolyn.newsfeed.App
import com.tolyn.newsfeed.Database
import com.tolyn.newsfeed.UserPreferences
import com.tolyn.newsfeed.data.NewsCategory
import com.tolyn.newsfeed.data.NewsInfo
import com.tolyn.newsfeed.data.NewsProvider
import com.tolyn.newsfeed.data.UserPreferencesRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.kodein.di.instance
import java.text.SimpleDateFormat
import java.util.*

class MockNewsFeedService : NewsFeedService {

    private val _newsList = MutableStateFlow<List<NewsInfo.NewsItem>>(emptyList())
    override val newsList: StateFlow<List<NewsInfo.NewsItem>> = _newsList

    private val _newsTimeDateSort: MutableStateFlow<TimeDateSort> =
        MutableStateFlow(TimeDateSort.Descending)
    override val newsTimeDateSort: StateFlow<TimeDateSort> = _newsTimeDateSort

    private val _newsProviderFilter: MutableStateFlow<List<NewsProvider>> =
        MutableStateFlow(NewsProvider.values().toList())
    override val newsProviderFilter: StateFlow<List<NewsProvider>> = _newsProviderFilter

    private val _newsCategoryFilter: MutableStateFlow<List<NewsCategory>> =
        MutableStateFlow(NewsCategory.values().toList())
    override val newsCategoryFilter: StateFlow<List<NewsCategory>> = _newsCategoryFilter

    private val _newsProviderSubscribe: MutableStateFlow<List<NewsProvider>> =
        MutableStateFlow(emptyList())
    override val newsProviderSubscribe: StateFlow<List<NewsProvider>> = _newsProviderSubscribe

    private val dateTimeFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)

    init {
        loadNewsProviderSubscribe()
        loadNewsList()
    }

    override fun updateNewsTimeDateSort(sort: TimeDateSort) {
        _newsTimeDateSort.value = sort
        loadNewsList()
    }

    override fun updateNwsCategoryFilter(filterList: List<NewsCategory>) {
        _newsCategoryFilter.value = filterList
        loadNewsList()
    }

    override fun updateNwsProviderSubscribe(filterList: List<NewsProvider>) {
        _newsProviderSubscribe.value = filterList
        loadNewsList()
        GlobalScope.launch {
            NewsProvider.values().forEach {
                getUserData().updateProviderSubscribe(it, false)
            }
            filterList.forEach {
                getUserData().updateProviderSubscribe(it, true)
            }
        }
    }

    override fun updateNewsItemIsRead(id: Long) {
        getDataBase().newsQueries.updateIsRead(1L, id)
    }

    override fun updateNewsProviderFilter(filterList: List<NewsProvider>) {
        _newsProviderFilter.value = filterList
        loadNewsList()
    }

    private fun loadNewsList() {
        GlobalScope.launch {
            getTotalNewsList()
            val originList = _newsList.value.toMutableList()
            if (newsTimeDateSort.value == TimeDateSort.Descending) {
                originList.sortByDescending { news ->
                    news.dbItem.time.time
                }
            } else {
                originList.sortBy { news ->
                    news.dbItem.time.time
                }
            }
            originList.filter { news ->
                var isContains = false
                newsCategoryFilter.value.forEach {
                    if (it.name == news.dbItem.category) isContains = true
                }
                isContains
            }.filter { news ->
                var isContains = false
                newsProviderFilter.value.forEach {
                    if (it.key == news.dbItem.provider) isContains = true
                }
                isContains
            }.let {
                val filterList = it.toMutableList()
                filterList.sortByDescending { news ->
                    var isContains = false
                    newsProviderSubscribe.value.forEach { newsProvider ->
                        if (newsProvider.key == news.dbItem.provider) isContains = true
                    }
                    if (isContains) 1 else 0
                }
                _newsList.value = filterList.toList()
            }
        }
    }

    private fun getTotalNewsList() {
        val newsList by App.di.instance<NewsInfo.NewsList>()
        if (getDataBase().newsQueries.selectInstructions().executeAsList().isEmpty()) {
            newsList.newsList.forEach {
                val date = dateTimeFormat.parse(it.time) ?: Date().toStartOfTheDay()
                getDataBase().newsQueries.addNews(
                    null,
                    it.provider,
                    it.category,
                    it.title,
                    it.subtitle,
                    it.author,
                    date,
                    it.descriptiveStory,
                    it.url,
                    null
                )
            }
        }
        val dbList = getDataBase().newsQueries.selectInstructions().executeAsList()
        val uiList = mutableListOf<NewsInfo.NewsItem>()
        dbList.forEach {
            uiList.add(NewsInfo.NewsItem(it, it.isRead == 1L))
        }
        _newsList.value = uiList
    }

    private fun loadNewsProviderSubscribe() {
        GlobalScope.launch {
            val newsProviderSubscribeList = mutableListOf<NewsProvider>()
            NewsProvider.values().forEach { provider ->
                val isSubscribe = getUserData().getIsProviderSubscribe(provider)
                isSubscribe?.let {
                    if (it) newsProviderSubscribeList.add(provider)
                }
            }
            _newsProviderSubscribe.value = newsProviderSubscribeList
        }
    }

    private fun getDataBase(): Database {
        val database: Database by App.di.instance()
        return database
    }

    private fun getUserData(): UserPreferencesRepository {
        val userPreferencesRepository by App.di.instance<UserPreferencesRepository>()
        return userPreferencesRepository
    }

    private fun Date.toStartOfTheDay(): Date {
        val date = this
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
            set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
            set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
        }.time
    }
}