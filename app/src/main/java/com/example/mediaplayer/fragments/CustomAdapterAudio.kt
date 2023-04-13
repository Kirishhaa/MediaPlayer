package com.example.mediaplayer.fragments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.*
import com.example.mediaplayer.interfaces.myinnf.markers.AudioAdapterListener

class CustomAdapterAudio(
    private val listener: AudioAdapterListener,
    private val type: TypeListFragment,
) :
    RecyclerView.Adapter<CustomAdapterAudio.ViewHolder>() {

    private var metadata: MetaData = MetaData()
    private var audioList: List<Audio> = emptyList()
    private var decoratorList: List<AudioEntity> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutRes = when (type) {
            TypeListFragment.HORIZONTAL -> R.layout.item_horizontal
            TypeListFragment.VERTICAL -> R.layout.item_vertical
        }
        return ViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false))
    }

    override fun getItemCount(): Int {
        return audioList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(decoratorList[position].bitmap!=null) {
            holder.artImage.setImageBitmap(decoratorList[position].bitmap)
        } else {
            holder.artImage.setImageResource(R.drawable.ic_empty_music_card)
        }
        holder.title.text = decoratorList[position].title

        if (metadata.state == PlaybackStatus.PLAYING) {
            holder.playBox.isChecked = metadata.currentPosition == position
        } else {
            holder.playBox.isChecked = false
        }

        if (type == TypeListFragment.VERTICAL) {
            holder.title.setOnClickListener {
                listener.navigate(DetailFragment.onInstance(position, listener.getIsFavoriteState()))
            }
        }

        holder.playBox.setOnClickListener {
            val checkBox = it as CheckBox
            if (!checkBox.isChecked) {
                metadata =
                    MetaData(position, PlaybackStatus.PAUSED, listener.getIsFavoriteState())
                listener.callbackMetaData(metadata)
                listener.sendPauseAudio(metadata)
            } else {
                if (metadata.currentPosition == position) {
                    metadata = MetaData(position, PlaybackStatus.PLAYING, listener.getIsFavoriteState())
                    listener.callbackMetaData(metadata)
                    listener.sendResumeAudio(metadata)
                } else {
                    val prevPos = metadata.currentPosition
                    metadata = MetaData(position, PlaybackStatus.PLAYING, listener.getIsFavoriteState())
                    if (prevPos != -1) {
                        notifyItemChanged(prevPos)
                    }
                    listener.callbackMetaData(metadata)
                    listener.sendPlayAudio(metadata, audioList)
                }
            }
        }
    }

    fun setSongMetadata(metadata: MetaData) {
        if (this.metadata.currentPosition != -1) {
            notifyItemChanged(this.metadata.currentPosition)
        }
        this.metadata = metadata
        notifyItemChanged(metadata.currentPosition)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAudioList(audioList: List<Audio>, decoratorList: List<AudioEntity>) {
        this.decoratorList = decoratorList
        this.audioList = audioList
        notifyDataSetChanged()
    }

    enum class TypeListFragment {
        HORIZONTAL,
        VERTICAL
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val artImage: ImageView = view.findViewById(R.id.art_image_item)
        val title: TextView = view.findViewById(R.id.title_item)
        val playBox: CheckBox = view.findViewById(R.id.play_image_item)
    }
}