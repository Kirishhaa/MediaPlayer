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

class CustomAdapterAudio(listener: Listener, private val type: TypeFragmentList): RecyclerView.Adapter<CustomAdapterAudio.ViewHolder>() {
    private var listener: Listener? = listener
    private var listAudio: List<Audio> = ArrayList()

    interface Listener{
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemRes = when (type) {
            TypeFragmentList.VERTICAL -> R.layout.item_vertical
            TypeFragmentList.HORIZONTAL -> R.layout.item_horizontal
        }
        return ViewHolder(LayoutInflater.from(parent.context).inflate(itemRes, parent, false))
    }

    override fun getItemCount(): Int {
        return listAudio.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (type) {
            TypeFragmentList.HORIZONTAL -> {
                if(listAudio[position].image!=null) holder.imageH!!.setImageBitmap(listAudio[position].image)
                holder.titleH!!.text = listAudio[position].title
                holder.imageH!!.setOnClickListener {
                    listener?.onItemClick(position)
                }
            }
            TypeFragmentList.VERTICAL -> {
                holder.imageV?.setImageBitmap(listAudio[position].image)
                holder.titleV!!.text = listAudio[position].title
                holder.playV!!.setOnClickListener {
                    listener?.onItemClick(position)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(listAudio: List<Audio>){
        this.listAudio = listAudio
        notifyDataSetChanged()
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val imageH: ImageView? = view.findViewById(R.id.image_item_horizontal)
        val titleH: TextView? = view.findViewById(R.id.text_item_horizontal)
        val imageV: ImageView? = view.findViewById(R.id.image_item_vertical)
        val titleV: TextView? = view.findViewById(R.id.text_item_vertical)
        val playV: ImageView? = view.findViewById(R.id.image_item_play)
    }
}