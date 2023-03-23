package com.example.mediaplayer

import android.content.Context
import com.example.mediaplayer.data.Audio
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StorageUtils(private val context: Context) {
    private val STORAGE = "com.example.mediaplayer.STORAGE"

    fun writeAudioList(audioList: ArrayList<Audio>){
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val gson = Gson()
        val jsonList = gson.toJson(audioList)
        editor.putString("audioArrayList", jsonList)
        editor.apply()
    }

    fun readAudioList():ArrayList<Audio>{
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences.getString("audioArrayList", null)
        val type = object : TypeToken<ArrayList<Audio>>(){}.type
        return gson.fromJson(json, type)
    }

    fun writeIndex(index: Int){
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt("index", index)
        editor.apply()
    }

    fun readIndex(): Int{
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        return preferences.getInt("index", -1)
    }

    fun clearData(){
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

}