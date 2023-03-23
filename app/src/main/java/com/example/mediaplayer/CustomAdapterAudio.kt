package com.example.mediaplayer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mediaplayer.data.Audio

class CustomAdapterAudio(listener: Listener, private val type: Type): RecyclerView.Adapter<CustomAdapterAudio.ViewHolder>() {
    private var listener: Listener? = listener
    private var listAudio = ArrayList<Audio>()

    interface Listener{
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemRes = when (type) {
            Type.VERTICAL -> R.layout.item_vertical
            Type.HORIZONTAL -> R.layout.item_horizontal
        }
        return ViewHolder(LayoutInflater.from(parent.context).inflate(itemRes, parent, false))
    }

    override fun getItemCount(): Int {
        return listAudio.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (type) {
            Type.HORIZONTAL -> {
                if(listAudio[position].image!=null) holder.imageH!!.setImageBitmap(listAudio[position].image)
                holder.titleH!!.text = listAudio[position].title
                holder.imageH!!.setOnClickListener {
                    listener?.onItemClick(position)
                }
            }
            Type.VERTICAL -> {
                holder.imageV?.setImageBitmap(listAudio[position].image)
                holder.titleV!!.text = listAudio[position].title
                holder.playV!!.setOnClickListener {
                    listener?.onItemClick(position)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(listAudio: ArrayList<Audio>){
        this.listAudio = listAudio
        notifyDataSetChanged()
    }

    enum class Type{
        HORIZONTAL,
        VERTICAL
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val imageH: ImageView? = view.findViewById(R.id.image_item_horizontal)
        val titleH: TextView? = view.findViewById(R.id.text_item_horizontal)
        val imageV: ImageView? = view.findViewById(R.id.image_item_vertical)
        val titleV: TextView? = view.findViewById(R.id.text_item_vertical)
        val playV: ImageView? = view.findViewById(R.id.image_item_play)
    }
}