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
    private val binding get() = requireNotNull(_binding)
    private lateinit var adapter: ListEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTopMenu(
            R.id.action_navigation_upcoming_event_to_settingsFragment,
            R.id.action_navigation_upcoming_event_to_aboutFragment
        )
        adapter = ListEventAdapter(this)
        binding.apply {
            rvUpcomingEvent.adapter = adapter

            refreshLayout.setOnRefreshListener {
                loadData()
            }
        }
        setupSearch()
        setupObserver()
        loadData()
    }

    private fun setupSearch() {
        binding.searchView.apply {
            upcomingEventViewModel.apply {
                setOnSearchClickListener {
                    openFilter()
                    adapter.submitList(emptyList())
                }
                setOnCloseListener {
                    closeFilter()
                    adapter.submitList(getUpcomingEventList().value)
                    binding.showNoDataText(false)
                    false
                }
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        searchEvent(query)
                        return true
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        return false
                    }
                })
            }
        }
    }

    private fun setupObserver() {
        upcomingEventViewModel.apply {
            getSearchEventList().observe(viewLifecycleOwner) {
                if (isFilterOpened()) {
                    adapter.submitList(it)
                }
            }
            getSearchEventState().observe(viewLifecycleOwner) {
                if (isFilterOpened()) {
                    handleResult(it, getString(R.string.txt_not_found)) { list ->
                        setSearchEventList(list)
                    }
                }
            }

            getUpcomingEventList().observe(viewLifecycleOwner) {
                if (isFilterOpened().not()) {
                    adapter.submitList(it)
                }
            }
            getUpcomingEventState().observe(viewLifecycleOwner) {
                if (isFilterOpened().not()) {
                    handleResult(it, getString(R.string.txt_no_upcoming_event)) { list ->
                        setUpcomingEventList(list)
                    }
                }
            }
        }
    }

    private fun UpcomingEventViewModel.handleResult(
        result: Result<List<Event>>,
        dataEmptyMessage: String,
        onSuccess: (List<Event>) -> Unit
    ) {
        binding.apply {
            when (result) {
                is Result.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    refreshLayout.isRefreshing = false
                    progressBar.visibility = View.GONE
                    hasShownToast = false
                    if (result.data.isEmpty()) {
                        showNoDataText(true, dataEmptyMessage)
                    } else {
                        showNoDataText(false)
                        onSuccess(result.data)
                        adapter.submitList(result.data)
                    }
                }

                is Result.Error -> {
                    refreshLayout.isRefreshing = false
                    progressBar.visibility = View.GONE
                    if (hasShownToast.not()) {
                        context.showErrorWithToast(
                            lifecycleScope,
                            onShow = { hasShownToast = true },
                            onHidden = { hasShownToast = false }
                        )
                    }
                    if (adapter.currentList.isEmpty()) {
                        showNoDataText(true, getString(R.string.txt_no_internet))
                    }
                }
            }
        }
    }

    private fun FragmentUpcomingEventBinding.showNoDataText(
        show: Boolean,
        message: String? = null
    ) {
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
        upcomingEventViewModel.apply {
            if (isInitialized().not()) return

            if (isFilterOpened()) {
                fetchSearchEvent()
            } else {
                fetchUpcomingEventList()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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