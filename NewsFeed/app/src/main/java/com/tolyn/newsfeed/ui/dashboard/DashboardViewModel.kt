package com.tolyn.newsfeed.ui.dashboard

import androidx.lifecycle.*
import com.tolyn.newsfeed.data.NewsProvider
import com.tolyn.newsfeed.service.TimeDateSort

class DashboardViewModel : ViewModel() {

    private val timeDateSort = MutableLiveData(TimeDateSort.Descending)
    private val _newsProviderFilter = MutableLiveData<List<NewsProvider>>(NewsProvider.values().toList())
    val newsProviderFilter = _newsProviderFilter

    val timeDateSortText: LiveData<String> = timeDateSort.map { it.name }

    fun clickTimeDateSort() {
        timeDateSort.value = timeDateSort.value.let {
            if (it == TimeDateSort.Descending)
                TimeDateSort.Ascending
            else TimeDateSort.Descending
        }
    }

    fun checkNewsProviderFilterCheck(provider: NewsProvider){

    }

}