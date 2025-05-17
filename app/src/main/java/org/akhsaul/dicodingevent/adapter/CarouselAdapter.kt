package org.akhsaul.dicodingevent.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import org.akhsaul.core.domain.model.Event
import org.akhsaul.dicodingevent.databinding.CarouselEventBinding
import org.akhsaul.dicodingevent.util.DiffCallback
import org.akhsaul.dicodingevent.util.OnItemClickListener

class CarouselAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Event, CarouselAdapter.MyViewHolder>(DiffCallback) {

    class MyViewHolder(private val binding: CarouselEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event, listener: OnItemClickListener) {
            binding.root.setOnClickListener {
                listener.onItemClick(event)
            }
            binding.carouselImageView.load(event.imageLogo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            CarouselEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }
}