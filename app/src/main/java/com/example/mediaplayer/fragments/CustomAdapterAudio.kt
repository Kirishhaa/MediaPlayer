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
import com.example.mediaplayer.fragments.superclasses.BaseListFragment

class CustomAdapterAudio(
    private val listener: BaseListFragment,
    private val type: TypeListFragment,
) :
    RecyclerView.Adapter<CustomAdapterAudio.ViewHolder>() {

    private var songMetadata: SongMetadata = SongMetadata()
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

        if (songMetadata.state == PlaybackStatus.PLAYING) {
            holder.playBox.isChecked = songMetadata.currentPosition == position
        } else {
            holder.playBox.isChecked = false
        }

        if (type == TypeListFragment.VERTICAL) {
            holder.title.setOnClickListener {
                listener.navigate(DetailFragment.onInstance(position, listener.isFavorite))
            }
        }

        holder.playBox.setOnClickListener {
            val checkBox = it as CheckBox
            if (!checkBox.isChecked) {
                songMetadata = SongMetadata(position, PlaybackStatus.PAUSED, listener.isFavorite)
                listener.callbackMetadata(songMetadata)
                listener.onPauseClicked(songMetadata)
            } else {
                if (songMetadata.currentPosition == position) {
                    songMetadata = SongMetadata(position, PlaybackStatus.PLAYING, listener.isFavorite)
                    listener.callbackMetadata(songMetadata)
                    listener.onResumeClicked(songMetadata)
                } else {
                    val prevPos = songMetadata.currentPosition
                    songMetadata = SongMetadata(position, PlaybackStatus.PLAYING, listener.isFavorite)
                    if (prevPos != -1) {
                        notifyItemChanged(prevPos)
                    }
                    listener.callbackMetadata(songMetadata)
                    listener.onPlayClicked(songMetadata, audioList, listener.getFavoriteMap())
                }
            }
        }
    }

    fun setSongMetadata(songMetadata: SongMetadata) {
        if (this.songMetadata.currentPosition != -1) {
            notifyItemChanged(this.songMetadata.currentPosition)
        }
        this.songMetadata = songMetadata
        notifyItemChanged(songMetadata.currentPosition)
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