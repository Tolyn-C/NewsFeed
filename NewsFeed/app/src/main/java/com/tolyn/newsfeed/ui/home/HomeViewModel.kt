package com.tolyn.newsfeed.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tolyn.newsfeed.App
import com.tolyn.newsfeed.data.NewsInfo
import com.tolyn.newsfeed.service.NewsFeedService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.kodein.di.instance
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    val filteredNewsList = MutableLiveData<List<NewsInfo.News>>(emptyList())

    init {
        viewModelScope.launch {
            App.initializedStateFlow.collect {
                if (it){
                    val newsFeedService by App.di.instance<NewsFeedService>()
                    val originList = newsFeedService.getTotalNewsList()
                    filteredNewsList.value = originList.sortedByDescending { news ->
                        val dateTime = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH).parse(news.time)
                        dateTime.time
                    }
                }
            }
        }
    }
}