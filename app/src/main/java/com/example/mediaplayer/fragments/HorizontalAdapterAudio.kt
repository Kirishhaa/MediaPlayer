package com.example.mediaplayer.fragments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.PlaybackStatus
import com.example.mediaplayer.interfaces.AdapterListener

class HorizontalAdapterAudio(private val listener: AdapterListener, private var currentPosition: Int) :
    RecyclerView.Adapter<HorizontalAdapterAudio.ViewHolder>() {
    private var listAudio: List<Audio> = ArrayList()
    private var state: PlaybackStatus = PlaybackStatus.PLAYING


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_horizontal, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return listAudio.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (listAudio[position].imageArt != null)
            holder.imageH!!.setImageBitmap(listAudio[position].imageArt)
        holder.titleH!!.text = listAudio[position].title
        holder.imageH!!.setOnClickListener {
            if (currentPosition == position && state == PlaybackStatus.PLAYING) {
                listener.onPauseClicked()
                state = PlaybackStatus.PAUSED
                listAudio[position].imagePlayRes = R.drawable.ic_play_arrow
            } else if (currentPosition == position && state == PlaybackStatus.PAUSED) {
                listener.onResumeClicked()
                state = PlaybackStatus.PLAYING
                listAudio[position].imagePlayRes = R.drawable.ic_pause
            } else {
                listener.onPlayClicked(position, listAudio)
                if(currentPosition!=-1){
                    listAudio[currentPosition].imagePlayRes = R.drawable.ic_play_arrow
                    notifyItemChanged(currentPosition)
                } else currentPosition = holder.adapterPosition
                listAudio[position].imagePlayRes = R.drawable.ic_pause
                state = PlaybackStatus.PLAYING
            }
            notifyItemChanged(position)
            currentPosition = holder.adapterPosition
            listener.callbackItem(position,state)
        }
    }

    fun setCurrentData(position: Int, state: PlaybackStatus) {
        this.currentPosition = position
        this.state = state
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(listAudio: List<Audio>) {
        this.listAudio = listAudio
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageH: ImageView? = view.findViewById(R.id.image_item_horizontal)
        val titleH: TextView? = view.findViewById(R.id.text_item_horizontal)
    }
}