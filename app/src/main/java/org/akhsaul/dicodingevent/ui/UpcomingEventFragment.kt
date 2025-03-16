package org.akhsaul.dicodingevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.akhsaul.dicodingevent.R
import org.akhsaul.dicodingevent.adapter.ListEventAdapter
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.databinding.FragmentUpcomingEventBinding
import org.akhsaul.dicodingevent.setupTopMenu
import org.akhsaul.dicodingevent.showErrorWithToast
import org.akhsaul.dicodingevent.ui.DetailFragment.Companion.KEY_EVENT_DATA
import org.akhsaul.dicodingevent.util.OnItemClickListener
import org.akhsaul.dicodingevent.util.Result

@AndroidEntryPoint
class UpcomingEventFragment : Fragment(), OnItemClickListener {
    private val upcomingEventViewModel: UpcomingEventViewModel by viewModels()
    private var _binding: FragmentUpcomingEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)
        setupTopMenu(
            R.id.action_navigation_upcoming_event_to_settingsFragment,
            R.id.action_navigation_upcoming_event_to_aboutFragment
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ListEventAdapter(this)
        binding.rvUpcomingEvent.adapter = adapter

        binding.refreshLayout.setOnRefreshListener {
            loadData()
            binding.refreshLayout.isRefreshing = false
        }

        binding.searchView.setOnSearchClickListener {
            upcomingEventViewModel.openFilter(adapter.currentList)
        }
        binding.searchView.setOnCloseListener {
            val originalList = upcomingEventViewModel.closeFilter()
            adapter.submitList(originalList)
            binding.textNoData(false)
            false
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                upcomingEventViewModel.searchEvent(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        upcomingEventViewModel.getSearchEvent().observe(viewLifecycleOwner) { result ->
            if (!upcomingEventViewModel.isFilterOpened()) return@observe

            with(binding) {
                when (result) {
                    is Result.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        progressBar.visibility = View.GONE
                        upcomingEventViewModel.hasShownToast = false
                        if (result.data.isEmpty()) {
                            textNoData(true, getString(R.string.txt_not_found))
                        } else {
                            textNoData(false)
                            adapter.submitList(result.data)
                        }
                    }

                    is Result.Error -> {
                        progressBar.visibility = View.GONE
                        if (upcomingEventViewModel.hasShownToast.not()) {
                            context.showErrorWithToast(
                                lifecycleScope,
                                onShow = { upcomingEventViewModel.hasShownToast = true },
                                onHidden = { upcomingEventViewModel.hasShownToast = false }
                            )
                        }
                        if (adapter.currentList.isEmpty()) {
                            textNoData(true, getString(R.string.txt_no_internet))
                        }
                    }
                }
            }
        }

        upcomingEventViewModel.getUpcomingEventList().observe(viewLifecycleOwner) { result ->
            if (upcomingEventViewModel.isFilterOpened()) return@observe

            with(binding) {
                when (result) {
                    is Result.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        progressBar.visibility = View.GONE
                        upcomingEventViewModel.hasShownToast = false
                        if (result.data.isEmpty()) {
                            textNoData(true, getString(R.string.txt_no_upcoming_event))
                        } else {
                            textNoData(false)
                            adapter.submitList(result.data)
                        }
                    }

                    is Result.Error -> {
                        progressBar.visibility = View.GONE
                        if (upcomingEventViewModel.hasShownToast.not()) {
                            context.showErrorWithToast(
                                lifecycleScope,
                                onShow = { upcomingEventViewModel.hasShownToast = true },
                                onHidden = { upcomingEventViewModel.hasShownToast = false }
                            )
                        }
                        if (adapter.currentList.isEmpty()) {
                            textNoData(true, getString(R.string.txt_no_internet))
                        }
                    }
                }
            }
        }
    }

    private fun FragmentUpcomingEventBinding.textNoData(show: Boolean, message: String? = null) {
        if (show) {
            tvNoData.text = requireNotNull(message)
            rvUpcomingEvent.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        } else {
            tvNoData.visibility = View.GONE
            rvUpcomingEvent.visibility = View.VISIBLE
        }
    }

    private fun loadData() {
        if (!upcomingEventViewModel.isInitialized()) return

        upcomingEventViewModel.fetchUpcomingEventList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    override fun onItemClick(event: Event) {
        findNavController().navigate(
            R.id.action_navigation_upcoming_event_to_detailFragment,
            Bundle()
                .apply {
                    putParcelable(KEY_EVENT_DATA, event)
                }
        )
    }
}