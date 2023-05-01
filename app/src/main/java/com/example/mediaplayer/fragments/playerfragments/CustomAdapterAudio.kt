package com.example.mediaplayer.fragments.playerfragments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.R
import com.example.mediaplayer.dataoperations.*
import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.AudioEntity
import com.example.mediaplayer.models.MetaData
import com.example.mediaplayer.fragments.playerfragments.listfragments.DetailFragment
import com.example.mediaplayer.interfaces.markers.AudioAdapterListener
import com.example.mediaplayer.dataoperations.xml.XMLAudioDecorator
import com.example.mediaplayer.dataoperations.xml.XMLListenerSetter

class CustomAdapterAudio(
    private val listener: AudioAdapterListener,
    private val type: TypeListFragment,
) : RecyclerView.Adapter<CustomAdapterAudio.ViewHolder>() {

    private var metadata: MetaData = MetaData()
    private var audioList: List<Audio> = emptyList()
    private val xmlAudioDecorator = XMLAudioDecorator()
    private val xmlListenerSetter = XMLListenerSetter(listener)

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
        xmlAudioDecorator.setImageArt(holder.artImage, position)
        xmlAudioDecorator.setTitle(holder.title, position)
        xmlAudioDecorator.setPlayBox(holder.playBox, metadata, position)

        if (type == TypeListFragment.VERTICAL) {
            holder.title.setOnClickListener {
                listener.navigate(
                    DetailFragment.onInstance(
                        position,
                        listener.getIsFavoriteState()
                    )
                )
            }
        }
        holder.playBox.setOnClickListener {
            val prevPos = xmlListenerSetter.setPlayListener(
                playBox = it as CheckBox,
                metaData = metadata,
                curPos = position,
                audioList = audioList
            )

            if (prevPos != -1) notifyItemChanged(prevPos)
        }
    }

    fun setMetaData(metadata: MetaData) {
        if (this.metadata.currentPosition != -1) {
            notifyItemChanged(this.metadata.currentPosition)
        }
        this.metadata = metadata
        notifyItemChanged(metadata.currentPosition)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAudioList(audioList: List<Audio>, decoratorList: List<AudioEntity>) {
        xmlAudioDecorator.setDecoratorList(decoratorList)
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