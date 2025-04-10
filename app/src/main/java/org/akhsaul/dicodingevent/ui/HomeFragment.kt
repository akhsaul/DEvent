package org.akhsaul.dicodingevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselSnapHelper
import dagger.hilt.android.AndroidEntryPoint
import org.akhsaul.dicodingevent.R
import org.akhsaul.dicodingevent.adapter.CarouselAdapter
import org.akhsaul.dicodingevent.adapter.ListEventAdapter
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.databinding.FragmentHomeBinding
import org.akhsaul.dicodingevent.setupTopMenu
import org.akhsaul.dicodingevent.showErrorWithToast
import org.akhsaul.dicodingevent.ui.DetailFragment.Companion.KEY_EVENT_DATA
import org.akhsaul.dicodingevent.util.OnItemClickListener
import org.akhsaul.dicodingevent.util.Result

@AndroidEntryPoint
class HomeFragment : Fragment(), OnItemClickListener {
    private val homeViewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)
    private lateinit var carouselAdapter: CarouselAdapter
    private lateinit var listAdapter: ListEventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTopMenu(
            R.id.action_navigation_home_to_settingsFragment,
            R.id.action_navigation_home_to_aboutFragment
        )
        carouselAdapter = CarouselAdapter(this)
        listAdapter = ListEventAdapter(this)
        binding.apply {
            CarouselSnapHelper().attachToRecyclerView(rvUpcomingEvent)
            rvUpcomingEvent.adapter = carouselAdapter
            rvFinishedEvent.adapter = listAdapter

            refreshLayout.setOnRefreshListener {
                loadData()
            }
            setupObserver()
        }
        loadData()
    }

    private fun FragmentHomeBinding.setupObserver() {
        homeViewModel.apply {
            getUpcomingEventList().observe(viewLifecycleOwner) {
                carouselAdapter.submitList(it)
            }
            getUpcomingEventState().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        pbUpcomingEvent.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        refreshLayout.isRefreshing = false
                        pbUpcomingEvent.visibility = View.GONE
                        hasShownToast = false
                        if (result.data.isEmpty()) {
                            showNoDataText(
                                tvNoUpcomingEvent,
                                getString(R.string.txt_no_upcoming_event),
                                rvUpcomingEvent
                            )
                        } else {
                            hideNoDataText(tvNoUpcomingEvent, rvUpcomingEvent)
                            setUpcomingEventList(result.data)
                            carouselAdapter.submitList(result.data)
                        }
                    }

                    is Result.Error -> {
                        refreshLayout.isRefreshing = false
                        pbUpcomingEvent.visibility = View.GONE
                        if (hasShownToast.not()) {
                            context.showErrorWithToast(
                                lifecycleScope,
                                onShow = { hasShownToast = true },
                                onHidden = { hasShownToast = false }
                            )
                        }
                        if (carouselAdapter.currentList.isEmpty()) {
                            showNoDataText(tvNoUpcomingEvent, getString(R.string.txt_no_internet))
                        }
                    }
                }
            }

            getFinishedEventList().observe(viewLifecycleOwner) {
                listAdapter.submitList(it)
            }
            getFinishedEventState().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        pbFinishedEvent.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        refreshLayout.isRefreshing = false
                        pbFinishedEvent.visibility = View.GONE
                        hasShownToast = false
                        if (result.data.isEmpty()) {
                            showNoDataText(
                                tvNoFinishedEvent,
                                getString(R.string.txt_no_finished_event),
                                rvFinishedEvent
                            )
                        } else {
                            hideNoDataText(tvNoFinishedEvent, rvFinishedEvent)
                            setFinishedEventList(result.data)
                            listAdapter.submitList(result.data)
                        }
                    }

                    is Result.Error -> {
                        refreshLayout.isRefreshing = false
                        pbFinishedEvent.visibility = View.GONE
                        if (hasShownToast.not()) {
                            context.showErrorWithToast(
                                lifecycleScope,
                                onShow = { hasShownToast = true },
                                onHidden = { hasShownToast = false }
                            )
                        }
                        if (listAdapter.currentList.isEmpty()) {
                            showNoDataText(tvNoFinishedEvent, getString(R.string.txt_no_internet))
                        }
                    }
                }
            }
        }
    }

    private fun showNoDataText(
        noDataView: TextView,
        message: String,
        recyclerView: RecyclerView? = null
    ) {
        noDataView.text = message
        recyclerView?.visibility = View.INVISIBLE
        noDataView.visibility = View.VISIBLE
    }

    private fun hideNoDataText(noDataView: TextView, recyclerView: RecyclerView) {
        noDataView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun loadData() {
        homeViewModel.apply {
            if (isInitialized().not()) return

            fetchUpcomingEventList()
            fetchFinishedEventList()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(event: Event) {
        findNavController().navigate(
            R.id.action_navigation_home_to_detailFragment,
            Bundle().apply {
                putParcelable(KEY_EVENT_DATA, event)
            }
        )
    }
}