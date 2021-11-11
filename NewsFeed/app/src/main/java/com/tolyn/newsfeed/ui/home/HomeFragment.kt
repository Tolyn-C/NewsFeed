package com.tolyn.newsfeed.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tolyn.newsfeed.App
import com.tolyn.newsfeed.R
import com.tolyn.newsfeed.data.NewsInfo
import com.tolyn.newsfeed.databinding.FragmentHomeBinding
import com.tolyn.newsfeed.service.NewsFeedService
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.kodein.di.instance

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.vm = homeViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        homeViewModel.viewModelScope.launch {
            App.initializedStateFlow.collect {
                if (it) {
                    homeViewModel.filteredNewsList.observe(viewLifecycleOwner) { news ->
                        binding.recyclerView.adapter = NewsHelperAdapter(requireContext()).also {
                            it.items = news
                        }
                    }
                }
            }
        }
    }
}

class NewsHelperAdapter(val context: Context) : RecyclerView.Adapter<NewsViewHolder>() {
    var items: List<NewsInfo.NewsItem> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val root =
            LayoutInflater.from(context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(root)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.news = items[position]
    }

    override fun getItemCount(): Int {
        return items.size
    }

}

class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val titleTextView = itemView.findViewById<TextView>(R.id.titleTextView)
    private val dateTextView = itemView.findViewById<TextView>(R.id.dateTextView)
    private val newsProvider = itemView.findViewById<TextView>(R.id.newsProviderTextView)
    private val categoryTextView = itemView.findViewById<TextView>(R.id.cateTextView)
    private val authorTextView = itemView.findViewById<TextView>(R.id.authorTextView)
    private val subTitleTextView = itemView.findViewById<TextView>(R.id.subTitleTextView)
    private val expandedViewContainer =
        itemView.findViewById<LinearLayout>(R.id.expandedViewContainer)
    private val separator2 = itemView.findViewById<View>(R.id.separator2)
    private val descriptiveTextView = itemView.findViewById<TextView>(R.id.descriptiveTextView)
    private val haveBeenReadView = itemView.findViewById<RelativeLayout>(R.id.haveBeenReadView)
    private val sourceButton = itemView.findViewById<Button>(R.id.sourceButton)
    var news: NewsInfo.NewsItem? = null
        set(value) {
            field = value
            value?.let {
                with(it.dbItem) {
                    titleTextView.text = title
                    newsProvider.text = provider
                    subTitleTextView.text = subtitle
                    categoryTextView.text = category
                    authorTextView.text = "by $author"
                    dateTextView.text = time.toString()
                    descriptiveTextView.text = descriptiveStory
                }
            }

            expandedViewContainer.visibility = View.GONE
            haveBeenReadView.visibility =
                value?.isRead?.let {
                    if (it) View.VISIBLE
                    else View.GONE
                } ?: View.GONE

            itemView.setOnClickListener {
                expandedViewContainer.visibility = when (expandedViewContainer.visibility) {
                    View.GONE -> View.VISIBLE
                    else -> View.GONE
                }
                val newsFeedService by App.di.instance<NewsFeedService>()
                value?.dbItem?.id?.let {
                    newsFeedService.updateNewsItemIsRead(it)
                }
                value?.isRead = true
                haveBeenReadView.visibility = View.VISIBLE

            }

            sourceButton.setOnClickListener {
                value?.dbItem?.url?.let{
                    openNewTabWindow(it,itemView.context)
                }
            }

            descriptiveTextView.visibility = View.VISIBLE
            separator2.visibility = descriptiveTextView.visibility
        }

    private fun openNewTabWindow(urls: String, context: Context) {
        val uris = Uri.parse(urls)
        val intents = Intent(Intent.ACTION_VIEW, uris)
        val b = Bundle()
        b.putBoolean("new_window", true)
        intents.putExtras(b)
        context.startActivity(intents)
    }
}