package org.akhsaul.dicodingevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.akhsaul.dicodingevent.R
import org.akhsaul.dicodingevent.adapter.GridEventAdapter
import org.akhsaul.dicodingevent.adjustStaggeredGridSpanCount
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.databinding.FragmentFinishedEventBinding
import org.akhsaul.dicodingevent.setupTopMenu
import org.akhsaul.dicodingevent.showErrorWithToast
import org.akhsaul.dicodingevent.ui.DetailFragment.Companion.KEY_EVENT_DATA
import org.akhsaul.dicodingevent.util.OnItemClickListener
import org.akhsaul.dicodingevent.util.Result

@AndroidEntryPoint
class FinishedEventFragment : Fragment(), OnItemClickListener {
    private val finishedEventViewModel: FinishedEventViewModel by viewModels()
    private var _binding: FragmentFinishedEventBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val listener = ViewTreeObserver.OnGlobalLayoutListener {
        binding.apply {
            rvEventActive.layoutManager.adjustStaggeredGridSpanCount(
                refreshLayout.width,
                refreshLayout.height,
                196.0,
                context?.resources?.displayMetrics
            )
        }
    }
    private lateinit var adapter: GridEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTopMenu(
            R.id.action_navigation_finished_event_to_settingsFragment,
            R.id.action_navigation_finished_event_to_aboutFragment
        )
        adapter = GridEventAdapter(this)
        binding.apply {
            rvEventActive.viewTreeObserver.addOnGlobalLayoutListener(listener)
            rvEventActive.adapter = adapter

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
            finishedEventViewModel.apply {
                setOnSearchClickListener {
                    openFilter()
                    adapter.submitList(emptyList())
                }
                setOnCloseListener {
                    closeFilter()
                    adapter.submitList(getFinishedEventList().value)
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
        finishedEventViewModel.apply {
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

            getFinishedEventList().observe(viewLifecycleOwner) {
                if (isFilterOpened().not()) {
                    adapter.submitList(it)
                }
            }
            getFinishedEventState().observe(viewLifecycleOwner) {
                if (isFilterOpened().not()) {
                    handleResult(it, getString(R.string.txt_no_finished_event)) { list ->
                        setFinishedEventList(list)
                    }
                }
            }
        }
    }

    private fun FinishedEventViewModel.handleResult(
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

    private fun FragmentFinishedEventBinding.showNoDataText(
        show: Boolean,
        message: String? = null
    ) {
        if (show) {
            tvNoData.text = requireNotNull(message)
            rvEventActive.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        } else {
            tvNoData.visibility = View.GONE
            rvEventActive.visibility = View.VISIBLE
        }
    }

    private fun loadData() {
        finishedEventViewModel.apply {
            if (isInitialized().not()) return

            if (isFilterOpened()) {
                fetchSearchEvent()
            } else {
                fetchFinishedEvent()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        binding.rvEventActive.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    override fun onItemClick(event: Event) {
        findNavController().navigate(
            R.id.action_navigation_finished_event_to_detailFragment,
            Bundle().apply {
                putParcelable(KEY_EVENT_DATA, event)
            }
        )
    }
}