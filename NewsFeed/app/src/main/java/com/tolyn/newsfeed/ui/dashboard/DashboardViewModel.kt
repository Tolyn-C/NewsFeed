package com.tolyn.newsfeed.ui.dashboard

import androidx.lifecycle.*
import com.tolyn.newsfeed.App
import com.tolyn.newsfeed.data.NewsCategory
import com.tolyn.newsfeed.data.NewsProvider
import com.tolyn.newsfeed.service.NewsFeedService
import com.tolyn.newsfeed.service.TimeDateSort
import org.kodein.di.instance

class DashboardViewModel : ViewModel() {

    private val newsFeedService by App.di.instance<NewsFeedService>()
    private val timeDateSort =
        newsFeedService.newsTimeDateSort.asLiveData(viewModelScope.coroutineContext)
    val newsProviderFilter =
        newsFeedService.newsProviderFilter.asLiveData(viewModelScope.coroutineContext)
    val newsCategoryFilter =
        newsFeedService.newsCategoryFilter.asLiveData(viewModelScope.coroutineContext)
    val newsProviderSubscribe =
        newsFeedService.newsProviderSubscribe.asLiveData(viewModelScope.coroutineContext)
    val timeDateSortText: LiveData<String> = timeDateSort.map { it.name }

    fun clickTimeDateSort() {
        timeDateSort.value.let {
            val sort =
                if (it == TimeDateSort.Descending)
                    TimeDateSort.Ascending
                else
                    TimeDateSort.Descending
            newsFeedService.updateNewsTimeDateSort(sort)
        }
    }

    fun checkNewsProviderFilterCheck(provider: NewsProvider) {
        newsProviderFilter.value?.let {
            val mutableList = it.toMutableList()
            if (it.contains(provider)) {
                mutableList.remove(provider)
            } else {
                mutableList.add(provider)
            }
            newsFeedService.updateNewsProviderFilter(mutableList.toList())
        }
    }

    fun checkNewsCategoryFilterCheck(category: NewsCategory) {
        newsCategoryFilter.value?.let {
            val mutableList = it.toMutableList()
            if (it.contains(category)) {
                mutableList.remove(category)
            } else {
                mutableList.add(category)
            }
            newsFeedService.updateNwsCategoryFilter(mutableList.toList())
        }
    }

    fun checkNewsProviderSubscribeCheck(provider: NewsProvider) {
        newsProviderSubscribe.value?.let {
            val mutableList = it.toMutableList()
            if (it.contains(provider)) {
                mutableList.remove(provider)
            } else {
                mutableList.add(provider)
            }
            newsFeedService.updateNwsProviderSubscribe(mutableList.toList())
        }
    }

}