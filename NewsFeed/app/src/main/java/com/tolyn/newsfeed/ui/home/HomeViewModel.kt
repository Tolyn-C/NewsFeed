package com.tolyn.newsfeed.ui.home

import androidx.lifecycle.*
import com.tolyn.newsfeed.App
import com.tolyn.newsfeed.data.NewsInfo
import com.tolyn.newsfeed.service.NewsFeedService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.kodein.di.instance

class HomeViewModel : ViewModel() {

    lateinit var filteredNewsList : LiveData<List<NewsInfo.NewsItem>>

    init {
        viewModelScope.launch {
            App.initializedStateFlow.collect {
                if (it){
                    val newsFeedService by App.di.instance<NewsFeedService>()
                    filteredNewsList  = newsFeedService.newsList.asLiveData(viewModelScope.coroutineContext)
                }
            }
        }
    }
}