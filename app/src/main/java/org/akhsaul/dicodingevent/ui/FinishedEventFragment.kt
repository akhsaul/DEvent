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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.akhsaul.dicodingevent.R
import org.akhsaul.dicodingevent.adapter.DisplayEventAdapter
import org.akhsaul.dicodingevent.clear
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.databinding.FragmentFinishedEventBinding
import org.akhsaul.dicodingevent.pxToDp
import org.akhsaul.dicodingevent.roundNumber
import org.akhsaul.dicodingevent.setupTopMenu
import org.akhsaul.dicodingevent.showErrorWithToast
import org.akhsaul.dicodingevent.toggleDisplay
import org.akhsaul.dicodingevent.ui.DetailFragment.Companion.KEY_EVENT_DATA
import org.akhsaul.dicodingevent.util.OnItemClickListener
import org.akhsaul.dicodingevent.util.Result

@AndroidEntryPoint
class FinishedEventFragment : Fragment(), OnItemClickListener {
    private val finishedEventViewModel: FinishedEventViewModel by viewModels()
    private var _binding: FragmentFinishedEventBinding? = null
    private val binding get() = _binding!!
    private val adapter: DisplayEventAdapter by lazy {
        DisplayEventAdapter(this)
    }
    private val listener = ViewTreeObserver.OnGlobalLayoutListener {
        val width = context.pxToDp(binding.refreshLayout.width)
        val height = context.pxToDp(binding.refreshLayout.height)
        if (width != 0 && height != 0) {
            val layoutManager = binding.rvEventActive.layoutManager
            if (layoutManager is StaggeredGridLayoutManager) {
                layoutManager.spanCount = roundNumber(width.toDouble().div(196.0))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedEventBinding.inflate(inflater, container, false)
        setupTopMenu(
            R.id.action_navigation_finished_event_to_settingsFragment,
            R.id.action_navigation_finished_event_to_aboutFragment
        ) {
            val isGridLayout = binding.rvEventActive.layoutManager is StaggeredGridLayoutManager
            if (it != isGridLayout) {
                binding.rvEventActive.toggleDisplay(it, adapter, requireContext())
                finishedEventViewModel.isGrid = it
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvEventActive.viewTreeObserver.addOnGlobalLayoutListener(listener)
        binding.rvEventActive.toggleDisplay(
            finishedEventViewModel.isGrid,
            adapter,
            requireContext()
        )

        binding.refreshLayout.setOnRefreshListener {
            loadData()
            binding.refreshLayout.isRefreshing = false
        }

        binding.searchView.setOnSearchClickListener {
            finishedEventViewModel.openFilter(adapter.currentList)
        }
        binding.searchView.setOnCloseListener {
            adapter.clear()
            val originalList = finishedEventViewModel.closeFilter()
            if (originalList.isEmpty()) {
                loadData()
            } else {
                adapter.submitList(originalList)
                binding.textNoData(false)
            }
            false
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                finishedEventViewModel.searchEvent(query)
                adapter.clear()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        finishedEventViewModel.getSearchEvent().observe(viewLifecycleOwner) { result ->
            if (!finishedEventViewModel.isFilterOpened()) return@observe

            with(binding) {
                when (result) {
                    is Result.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        progressBar.visibility = View.GONE
                        finishedEventViewModel.hasShownToast = false
                        if (result.data.isEmpty()) {
                            textNoData(true, getString(R.string.txt_not_found))
                        } else {
                            textNoData(false)
                            adapter.submitList(result.data)
                        }
                    }

                    is Result.Error -> {
                        progressBar.visibility = View.GONE
                        if (finishedEventViewModel.hasShownToast.not()) {
                            context.showErrorWithToast(
                                lifecycleScope,
                                onShow = { finishedEventViewModel.hasShownToast = true },
                                onHidden = { finishedEventViewModel.hasShownToast = false }
                            )
                        }
                        if (adapter.currentList.isEmpty()) {
                            textNoData(true, getString(R.string.txt_no_internet))
                        }
                    }
                }
            }
        }

        finishedEventViewModel.getFinishedEvent().observe(viewLifecycleOwner) { result ->
            if (finishedEventViewModel.isFilterOpened()) return@observe

            with(binding) {
                when (result) {
                    is Result.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        progressBar.visibility = View.GONE
                        finishedEventViewModel.hasShownToast = false
                        if (result.data.isEmpty()) {
                            textNoData(true, getString(R.string.txt_no_finished_event))
                        } else {
                            textNoData(false)
                            adapter.submitList(result.data)
                        }
                    }

                    is Result.Error -> {
                        progressBar.visibility = View.GONE
                        if (finishedEventViewModel.hasShownToast.not()) {
                            context.showErrorWithToast(
                                lifecycleScope,
                                onShow = { finishedEventViewModel.hasShownToast = true },
                                onHidden = { finishedEventViewModel.hasShownToast = false }
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

    private fun FragmentFinishedEventBinding.textNoData(show: Boolean, message: String? = null) {
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
        if (!finishedEventViewModel.isInitialized()) return

        finishedEventViewModel.fetchFinishedEvent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    override fun onPause() {
        super.onPause()
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