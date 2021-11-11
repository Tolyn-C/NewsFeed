package com.tolyn.newsfeed.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tolyn.newsfeed.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        binding.fragment = this
        binding.vm = dashboardViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun clickNewsProviderLayout() {
        val originState = binding.newsProviderFilterLayout.visibility
        binding.newsProviderFilterLayout.visibility =
            if (originState == View.VISIBLE) View.GONE else View.VISIBLE
        binding.newsProviderSubscribeLayout.visibility = binding.newsProviderFilterLayout.visibility
    }

    fun clickCategoryLayout() {
        val originState = binding.categoryFilterFLayout.visibility
        binding.categoryFilterFLayout.visibility =
            if (originState == View.VISIBLE) View.GONE else View.VISIBLE
    }
}