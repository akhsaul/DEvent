package org.akhsaul.dicodingevent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import org.akhsaul.dicodingevent.R
import org.akhsaul.dicodingevent.convertTime
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.databinding.GridItemEventBinding
import org.akhsaul.dicodingevent.remainingTime
import org.akhsaul.dicodingevent.util.DiffCallback
import org.akhsaul.dicodingevent.util.OnItemClickListener

class GridEventAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Event, GridEventAdapter.MyViewHolder>(DiffCallback) {

    class MyViewHolder(private val binding: GridItemEventBinding, private val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event, listener: OnItemClickListener) {
            with(binding) {
                root.setOnClickListener {
                    listener.onItemClick(event)
                }
                ivCover.load(event.imageLogo)
                tvCategory.text = event.category
                tvLocation.text =
                    if (event.cityName.lowercase() == "online") event.cityName else "Offline"
                tvName.text = event.name
                tvQuota.text =
                    context.getString(R.string.txt_quota, event.quota - event.registrants)
                tvTime.text = context.remainingTime(convertTime(event.beginTime))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            GridItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }
}