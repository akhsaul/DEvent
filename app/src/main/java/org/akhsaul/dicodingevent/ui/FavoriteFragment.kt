package org.akhsaul.dicodingevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.akhsaul.dicodingevent.R
import org.akhsaul.dicodingevent.adapter.ListEventAdapter
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.databinding.FragmentFavoriteBinding
import org.akhsaul.dicodingevent.setupTopMenu
import org.akhsaul.dicodingevent.showErrorWithToast
import org.akhsaul.dicodingevent.util.OnItemClickListener
import org.akhsaul.dicodingevent.util.Result

@AndroidEntryPoint
class FavoriteFragment : Fragment(), OnItemClickListener {
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        setupTopMenu(
            R.id.action_navigation_favorite_to_settingsFragment,
            R.id.action_navigation_favorite_to_aboutFragment
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = ListEventAdapter(this)
        binding.rvFavoriteEvent.adapter = adapter

        binding.refreshLayout.setOnRefreshListener {
            loadAll()
            binding.refreshLayout.isRefreshing = false
        }

        favoriteViewModel.getFavoriteEvents().observe(viewLifecycleOwner) { result ->
            with(binding) {
                when (result) {
                    is Result.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        progressBar.visibility = View.GONE
                        if (result.data.isEmpty()) {
                            textNoData(true, getString(R.string.txt_no_data))
                        } else {
                            textNoData(false)
                            adapter.submitList(result.data)
                        }
                    }

                    is Result.Error -> {
                        progressBar.visibility = View.GONE
                        if (favoriteViewModel.hasShownToast.not()) {
                            context.showErrorWithToast(
                                lifecycleScope,
                                onShow = { favoriteViewModel.hasShownToast = true },
                                onHidden = { favoriteViewModel.hasShownToast = false }
                            )
                        }
                        if (adapter.currentList.isEmpty()) {
                            textNoData(true, getString(R.string.txt_error_db))
                        }
                    }
                }
            }
        }
    }

    private fun FragmentFavoriteBinding.textNoData(show: Boolean, message: String? = null) {
        if (show) {
            tvNoData.text = requireNotNull(message)
            rvFavoriteEvent.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        } else {
            tvNoData.visibility = View.GONE
            rvFavoriteEvent.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(event: Event) {
        findNavController().navigate(
            R.id.action_navigation_favorite_to_detailFragment,
            Bundle().apply {
                putParcelable(DetailFragment.KEY_EVENT_DATA, event)
            }
        )
    }

    private fun loadAll() {
        if (!favoriteViewModel.isInitialized()) return

        favoriteViewModel.fetchFavoriteEvents()
    }

    override fun onStart() {
        super.onStart()
        loadAll()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}