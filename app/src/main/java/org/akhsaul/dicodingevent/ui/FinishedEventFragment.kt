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
import org.akhsaul.dicodingevent.adapter.GridEventAdapter
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
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedEventBinding.inflate(inflater, container, false)
        setupTopMenu(
            R.id.action_navigation_finished_event_to_settingsFragment,
            R.id.action_navigation_finished_event_to_aboutFragment
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = GridEventAdapter(this)
        binding.rvEventActive.adapter = adapter

        binding.refreshLayout.setOnRefreshListener {
            loadData()
            binding.refreshLayout.isRefreshing = false
        }

        binding.searchView.setOnSearchClickListener {
            finishedEventViewModel.openFilter(adapter.currentList)
        }
        binding.searchView.setOnCloseListener {
            val originalList = finishedEventViewModel.closeFilter()
            adapter.submitList(originalList)
            binding.textNoData(false)
            false
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                finishedEventViewModel.searchEvent(query)
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

    override fun onItemClick(event: Event) {
        findNavController().navigate(
            R.id.action_navigation_finished_event_to_detailFragment,
            Bundle().apply {
                putParcelable(KEY_EVENT_DATA, event)
            }
        )
    }
}