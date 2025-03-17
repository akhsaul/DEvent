package org.akhsaul.dicodingevent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.google.android.material.textview.MaterialTextView
import org.akhsaul.dicodingevent.R
import org.akhsaul.dicodingevent.convertTime
import org.akhsaul.dicodingevent.data.Event
import org.akhsaul.dicodingevent.remainingTime
import org.akhsaul.dicodingevent.util.DiffCallback
import org.akhsaul.dicodingevent.util.OnItemClickListener

class DisplayEventAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Event, DisplayViewHolder>(DiffCallback) {
    enum class ViewType(val id: Int) {
        LIST(0),
        GRID(1)
    }

    private var currentViewType = ViewType.GRID

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisplayViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = when (viewType) {
            ViewType.GRID.id -> {
                layoutInflater.inflate(R.layout.grid_item_event, parent, false)
            }

            ViewType.LIST.id -> {
                layoutInflater.inflate(R.layout.item_event, parent, false)
            }

            else -> {
                throw IllegalArgumentException("Unknown viewType: $viewType")
            }
        }
        return DisplayViewHolder(itemView, parent.context)
    }

    override fun onBindViewHolder(holder: DisplayViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    override fun getItemViewType(position: Int): Int {
        return currentViewType.id
    }

    fun setViewType(viewType: ViewType) {
        currentViewType = viewType
    }
}

class DisplayViewHolder(
    itemView: View,
    private val context: Context
) : RecyclerView.ViewHolder(itemView) {
    private val ivCover: ImageView = itemView.findViewById(R.id.ivCover)
    private val tvCategory: MaterialTextView = itemView.findViewById(R.id.tvCategory)
    private val tvLocation: MaterialTextView = itemView.findViewById(R.id.tvLocation)
    private val tvName: MaterialTextView = itemView.findViewById(R.id.tvName)
    private val tvQuota: MaterialTextView = itemView.findViewById(R.id.tvQuota)
    private val tvTime: MaterialTextView = itemView.findViewById(R.id.tvTime)

    fun bind(event: Event, listener: OnItemClickListener) {
        itemView.setOnClickListener {
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