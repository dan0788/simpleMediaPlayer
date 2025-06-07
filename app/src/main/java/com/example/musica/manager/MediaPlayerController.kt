package com.example.musica.manager

import com.example.musica.interfaces.MediaPlayerInterface
import android.media.MediaPlayer
import java.io.IOException
import android.util.Log
import android.content.Context

class MediaPlayerController(
    private val context: Context
) : MediaPlayerInterface {

    private var mediaPlayer: MediaPlayer? = null
    private val TAG = "MediaPlayerController" // MediaPlayerController Tag
    private var currentSongIndex: Int = 0
    private var musicList: List<String> = emptyList()

    override fun init(musicList: List<String>, startIndex: Int) {
        this.musicList = musicList
        this.currentSongIndex = startIndex
        Log.d(TAG, "Controlador inicializado con ${musicList.size} canciones. Índice inicial: $startIndex")
    }

    override fun play() : MediaPlayer? {
        if (musicList.isEmpty()) {
            Log.w(TAG, "No hay canciones cargadas para reproducir.")
            return null
        }

        val songFileName = musicList[currentSongIndex]

        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
            try {
                mediaPlayer?.start()
                Log.d(TAG, "Reanudando reproducción: $songFileName")
                return mediaPlayer
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Error al reanudar MediaPlayer: ${e.message}")
                release()
            }
        }

        if (mediaPlayer?.isPlaying == true) {
            Log.d(TAG, "Ya reproduciendo: $songFileName. No se reinicia.")
            return mediaPlayer
        }

        try {
            val fd = context.assets.openFd(songFileName)

            mediaPlayer = MediaPlayer().apply {
                setDataSource(
                    fd.fileDescriptor,
                    fd.startOffset,
                    fd.length
                )
                prepare()
                start()
                Log.d(TAG, "Iniciando nueva reproducción: $songFileName")

                setOnCompletionListener {
                    Log.d(TAG, "Reproducción de $songFileName completada.")
                    // reproducción automática
                    mediaPlayer = nextSong()
                }
            }
            fd.close()
            return mediaPlayer
        } catch (e: IOException) {
            val errorMessage = "Error de I/O al reproducir $songFileName: ${e.message}"
            Log.e(TAG, errorMessage)
            e.printStackTrace()
            release()
            return null
        } catch (e: IllegalStateException) {
            val errorMessage = "Estado ilegal al reproducir $songFileName: ${e.message}"
            Log.e(TAG, errorMessage)
            e.printStackTrace()
            release()
            return null
        }
    }

    override fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                Log.d(TAG, "Canción pausada: ${getCurrentSongName()}.")
            }
        }
    }

    override fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying || it.currentPosition > 0) { // Si está reproduciendo o pausado
                it.stop()
                it.reset() // Prepara para una nueva fuente de datos
                Log.d(TAG, "Canción detenida: ${getCurrentSongName()}.")
            }
        }
    }

    override fun nextSong() : MediaPlayer?{
        if (musicList.isEmpty()) {
            Log.w(TAG, "No hay canciones en la lista para avanzar.")
            return null
        }

        release() // Liberar el MediaPlayer actual antes de cambiar de canción

        currentSongIndex++
        if (currentSongIndex >= musicList.size) {
            currentSongIndex = 0
        }
        Log.d(TAG, "Siguiente canción. Nuevo índice: $currentSongIndex")
        return play()
    }

    override fun backSong() : MediaPlayer?{
        if (musicList.isEmpty()) {
            Log.w(TAG, "No hay canciones en la lista para retroceder.")
            return null
        }

        release() // Liberar el MediaPlayer actual antes de cambiar de canción

        currentSongIndex--
        if (currentSongIndex < 0) {
            currentSongIndex = musicList.size - 1 // Vuelve al final si es la primera
        }
        Log.d(TAG, "Canción anterior. Nuevo índice: $currentSongIndex")
        return play()
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d(TAG, "MediaPlayer liberado en MediaPlayerController.")
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    override fun getCurrentSongName(): String? {
        return if (musicList.isNotEmpty() && currentSongIndex >= 0 && currentSongIndex < musicList.size) {
            musicList[currentSongIndex]
        } else {
            null
        }
    }
}