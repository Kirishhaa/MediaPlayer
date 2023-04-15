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
import com.example.mediaplayer.interfaces.markers.AudioAdapterListener

class CustomAdapterAudio(
    private val listener: AudioAdapterListener,
    private val type: TypeListFragment,
) :
    RecyclerView.Adapter<CustomAdapterAudio.ViewHolder>() {

    private var metadata: MetaData = MetaData()
    private var audioList: List<Audio> = emptyList()
    private var decoratorList: List<AudioEntity> = emptyList()
    private val checkBoxSetListener = CheckBoxSetListener(listener)
    private val checkBoxSetData = CheckBoxSetData()

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

        checkBoxSetData.setData(
            imageArt = holder.artImage,
            title = holder.title,
            playBox = holder.playBox,
            curPos = position,
            metaData = metadata)

        if (type == TypeListFragment.VERTICAL) {
            holder.title.setOnClickListener {
                listener.navigate(DetailFragment.onInstance(position, listener.getIsFavoriteState()))
            }
        }

        holder.playBox.setOnClickListener {

            val prevPos = checkBoxSetListener.setPlayListener(
                playBox = it as CheckBox,
                metaData = metadata,
                curPos = position,
                audioList = audioList)

            if(prevPos!=-1) notifyItemChanged(prevPos)
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
        checkBoxSetData.setDecoratorList(decoratorList)
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