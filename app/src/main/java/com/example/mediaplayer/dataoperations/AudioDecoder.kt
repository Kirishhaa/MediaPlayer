package com.example.mediaplayer.dataoperations

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import com.example.mediaplayer.models.Audio
import com.example.mediaplayer.models.AudioEntity
import java.io.File

class AudioDecoder {
    companion object {
        fun getAudioEntity(audio: Audio): AudioEntity {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            val curFile = File(audio.path)
            val title = curFile.name
            mediaMetadataRetriever.setDataSource(audio.path)
            val byteArray = mediaMetadataRetriever.embeddedPicture
            val image = if (byteArray != null) {
                BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            } else null
            val duration =
                mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                    .toLong()
            mediaMetadataRetriever.release()
            return AudioEntity(title, duration, image)
        }
    }
}