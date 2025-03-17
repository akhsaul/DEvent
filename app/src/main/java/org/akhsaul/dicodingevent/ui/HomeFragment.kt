package org.akhsaul.dicodingevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.MultiBrowseCarouselStrategy
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
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val carouselAdapter = CarouselAdapter(this)
        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvUpcomingEvent)
        binding.rvUpcomingEvent.setLayoutManager(CarouselLayoutManager(MultiBrowseCarouselStrategy()))
        binding.rvUpcomingEvent.adapter = carouselAdapter

        val listAdapter = ListEventAdapter(this)
        binding.rvFinishedEvent.adapter = listAdapter

        binding.refreshLayout.setOnRefreshListener {
            loadAll()
            binding.refreshLayout.isRefreshing = false
        }

        homeViewModel.getUpcomingEventList().observe(viewLifecycleOwner) { result ->
            with(binding) {
                when (result) {
                    is Result.Loading -> {
                        pbUpcomingEvent.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        pbUpcomingEvent.visibility = View.GONE
                        homeViewModel.hasShownToast = false
                        if (result.data.isEmpty()) {
                            tvNoUpcomingEvent.text = getString(R.string.txt_no_upcoming_event)
                            rvUpcomingEvent.visibility = View.INVISIBLE
                            tvNoUpcomingEvent.visibility = View.VISIBLE
                        } else {
                            tvNoUpcomingEvent.visibility = View.GONE
                            rvUpcomingEvent.visibility = View.VISIBLE
                            carouselAdapter.submitList(result.data)
                        }
                    }

                    is Result.Error -> {
                        pbUpcomingEvent.visibility = View.GONE
                        if (homeViewModel.hasShownToast.not()) {
                            context.showErrorWithToast(
                                lifecycleScope,
                                onShow = { homeViewModel.hasShownToast = true },
                                onHidden = { homeViewModel.hasShownToast = false }
                            )
                        }
                        if (carouselAdapter.currentList.isEmpty()) {
                            tvNoUpcomingEvent.text = getString(R.string.txt_no_internet)
                            tvNoUpcomingEvent.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        homeViewModel.getFinishedEventList().observe(viewLifecycleOwner) { result ->
            with(binding) {
                when (result) {
                    is Result.Loading -> {
                        pbFinishedEvent.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        pbFinishedEvent.visibility = View.GONE
                        homeViewModel.hasShownToast = false
                        if (result.data.isEmpty()) {
                            tvNoFinishedEvent.text = getString(R.string.txt_no_finished_event)
                            rvFinishedEvent.visibility = View.INVISIBLE
                            tvNoFinishedEvent.visibility = View.VISIBLE
                        } else {
                            tvNoFinishedEvent.visibility = View.GONE
                            rvFinishedEvent.visibility = View.VISIBLE
                            listAdapter.submitList(result.data)
                        }
                    }

                    is Result.Error -> {
                        pbFinishedEvent.visibility = View.GONE
                        if (homeViewModel.hasShownToast.not()) {
                            context.showErrorWithToast(
                                lifecycleScope,
                                onShow = { homeViewModel.hasShownToast = true },
                                onHidden = { homeViewModel.hasShownToast = false }
                            )
                        }
                        if (listAdapter.currentList.isEmpty()) {
                            tvNoFinishedEvent.text = getString(R.string.txt_no_internet)
                            tvNoFinishedEvent.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }

        setupTopMenu(
            R.id.action_navigation_home_to_settingsFragment,
            R.id.action_navigation_home_to_aboutFragment
        )
        return root
    }

    private fun loadAll() {
        if (!homeViewModel.isInitialized()) return

        homeViewModel.fetchUpcomingEventList()
        homeViewModel.fetchFinishedEventList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        loadAll()
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