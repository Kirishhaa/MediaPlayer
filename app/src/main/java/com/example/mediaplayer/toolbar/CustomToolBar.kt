package com.example.mediaplayer.toolbar

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.mediaplayer.R

class CustomToolBar @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0,
    desStyleAttr: Int = 0
) : FrameLayout(context, attr, defStyleAttr, desStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_toolbar, this, true)
        initializeProperties()
    }

    private lateinit var title: TextView

    private lateinit var shuffleBox: CheckBox

    private fun initializeProperties() {
        title = findViewById(R.id.title_toolbar)
        shuffleBox = findViewById(R.id.shuffle_toolbar)

        shuffleBox.setOnClickListener {
            Log.d("Custom toolbar", "shuffleBox Clicked")
        }
    }

    fun setTitle(text: String) {
        title.text = text
    }

    fun showShuffleBox(show: Boolean) {
        shuffleBox.isVisible = show
    }
}