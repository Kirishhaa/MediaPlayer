package com.example.mediaplayer.storageutils

import android.content.Context
import com.example.mediaplayer.models.Audio
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Storage(private val context: Context) {
    private val STORAGE = "com.example.mediaplayer.STORAGE"
    private val ALL_AUDIO_LIST = "all_audio_list"
    private val FAVORITE_MAP = "favorite_map"

    fun writeFavoriteMap(map: Map<Int, Audio>) {
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val gson = Gson()
        val jsonList = gson.toJson(map)
        editor.putString(FAVORITE_MAP, jsonList)
        editor.apply()
    }

    fun readFavoriteMap(): LinkedHashMap<Int, Audio> {
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences.getString(FAVORITE_MAP, null)
        val type = object : TypeToken<LinkedHashMap<Int, Audio>>() {}.type
        return if (json != null) gson.fromJson(json, type) else LinkedHashMap()
    }

    fun writeAllAudioList(audioList: List<Audio>) {
        writeAudioList(audioList, ALL_AUDIO_LIST)
    }

    private fun writeAudioList(audioList: List<Audio>, namePref: String) {
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val gson = Gson()
        val jsonList = gson.toJson(audioList)
        editor.putString(namePref, jsonList)
        editor.apply()
    }

    fun readAllAudioList(): List<Audio> {
        return readAudioList(ALL_AUDIO_LIST)
    }

    private fun readAudioList(namePref: String): List<Audio> {
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences.getString(namePref, null)
        val type = object : TypeToken<ArrayList<Audio>>() {}.type
        return if (json != null) gson.fromJson(json, type) else emptyList()
    }

    fun writeIndex(index: Int) {
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        preferences.edit()
            .putInt("index", index)
            .apply()
    }

    fun readIndex(): Int {
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        return preferences.getInt("index", -1)
    }

    fun writeFavorite(isFavorite: Boolean) {
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        preferences.edit()
            .putBoolean("isFavorite", isFavorite)
            .apply()
    }

    fun readFavorite(): Boolean {
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        return preferences.getBoolean("isFavorite", false)
    }

    fun clearData() {
        val preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        preferences.edit()
            .clear()
            .apply()
    }
}