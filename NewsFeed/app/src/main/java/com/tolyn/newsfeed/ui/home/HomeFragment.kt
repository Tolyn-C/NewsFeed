package com.tolyn.newsfeed.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tolyn.newsfeed.R
import com.tolyn.newsfeed.data.NewsInfo
import com.tolyn.newsfeed.databinding.FragmentHomeBinding

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

        homeViewModel.filteredNewsList.observe(viewLifecycleOwner) { news ->
            binding.recyclerView.adapter = NewsHelperAdapter(requireContext()).also {
                it.items = news
            }
        }
    }
}

class NewsHelperAdapter(val context: Context) : RecyclerView.Adapter<NewsViewHolder>() {
    var items: List<NewsInfo.News> = emptyList()

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
    var news: NewsInfo.News? = null
        set(value) {
            field = value
            titleTextView.text = value?.title
            newsProvider.text = value?.provider
            subTitleTextView.text = value?.subtitle
            categoryTextView.text = value?.category
            authorTextView.text = "by ${value?.author}"
            expandedViewContainer.visibility = View.GONE
            dateTextView.text = value?.time

            itemView.setOnClickListener {
                expandedViewContainer.visibility = when (expandedViewContainer.visibility) {
                    View.GONE -> View.VISIBLE
                    else -> View.GONE
                }
            }

            descriptiveTextView.visibility = View.VISIBLE
            separator2.visibility = descriptiveTextView.visibility
            descriptiveTextView.text = value?.descriptiveStory
        }
}