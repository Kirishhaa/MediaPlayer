package com.example.mediaplayer.fragments

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.data.Audio
import com.example.mediaplayer.data.PlaybackStatus
import com.example.mediaplayer.data.SongMetadata
import com.example.mediaplayer.fragments.superclasses.BaseListFragment

class CustomAdapterAudio(
    private val listener: BaseListFragment,
    private val type: TypeListFragment,
) :
    RecyclerView.Adapter<CustomAdapterAudio.ViewHolder>() {

    private var songMetadata: SongMetadata = SongMetadata()
    private var audioList: List<Audio> = emptyList()

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
        if(audioList[position].imageArt!=null) {
            holder.artImage.setImageBitmap(audioList[position].imageArt)
        } else {
            holder.artImage.setImageResource(R.drawable.ic_empty_music_card)
        }
        holder.title.text = audioList[position].title
        holder.playImage.setOnClickListener {
            if (songMetadata.currentPosition == -1) {
                playAudio(position)
            } else {
                if (songMetadata.currentPosition == position) {
                    if (songMetadata.state == PlaybackStatus.PLAYING) {
                        pauseAudio()
                    } else {
                        resumeAudio()
                    }
                } else {
                    playAudio(position)
                }
            }
        }
        if (songMetadata.state == PlaybackStatus.PAUSED) {
            holder.playImage.setImageResource(R.drawable.ic_play_arrow)
        } else {
            holder.playImage.setImageResource(audioList[position].imagePlayRes)
        }
    }

    private fun playAudio(position: Int) {
        if (songMetadata.currentPosition != -1) {
            audioList[songMetadata.currentPosition].imagePlayRes = R.drawable.ic_play_arrow
            notifyItemChanged(songMetadata.currentPosition)
        }
        songMetadata = SongMetadata(position, PlaybackStatus.PLAYING)
        audioList[songMetadata.currentPosition].imagePlayRes = R.drawable.ic_pause
        notifyItemChanged(songMetadata.currentPosition)
        listener.callbackMetadata(songMetadata)
        listener.onPlayClicked(songMetadata)
    }

    private fun pauseAudio() {
        songMetadata = SongMetadata(songMetadata.currentPosition, PlaybackStatus.PAUSED)
        audioList[songMetadata.currentPosition].imagePlayRes = R.drawable.ic_play_arrow
        notifyItemChanged(songMetadata.currentPosition)
        listener.callbackMetadata(songMetadata)
        listener.onPauseClicked(songMetadata)
    }

    private fun resumeAudio() {
        songMetadata = SongMetadata(songMetadata.currentPosition, PlaybackStatus.PLAYING)
        audioList[songMetadata.currentPosition].imagePlayRes = R.drawable.ic_pause
        notifyItemChanged(songMetadata.currentPosition)
        listener.callbackMetadata(songMetadata)
        listener.onResumeClicked(songMetadata)
    }

    fun setSongMetadata(songMetadata: SongMetadata) {
        if (songMetadata.currentPosition != -1) {
            if (songMetadata.currentPosition == this.songMetadata.currentPosition) {
                if (songMetadata.state == PlaybackStatus.PLAYING) {
                    audioList[songMetadata.currentPosition].imagePlayRes = R.drawable.ic_pause
                    notifyItemChanged(songMetadata.currentPosition)
                } else {
                    audioList[songMetadata.currentPosition].imagePlayRes = R.drawable.ic_play_arrow
                    notifyItemChanged(songMetadata.currentPosition)
                }
            } else {
                if (this.songMetadata.currentPosition != -1) {
                    audioList[this.songMetadata.currentPosition].imagePlayRes =
                        R.drawable.ic_play_arrow
                    notifyItemChanged(this.songMetadata.currentPosition)
                }
                audioList[songMetadata.currentPosition].imagePlayRes = R.drawable.ic_pause
                notifyItemChanged(songMetadata.currentPosition)
            }
        }
        this.songMetadata = songMetadata
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAudioList(audioList: List<Audio>) {
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
        val playImage: ImageView = view.findViewById(R.id.play_image_item)
    }
}