package com.example.musica.interfaces

import android.media.MediaPlayer

interface MediaPlayerInterface {
    fun init(musicList: List<String>, startIndex: Int = 0)
    fun play() : MediaPlayer?
    fun pause()
    fun stop()
    fun nextSong() : MediaPlayer?
    fun backSong() : MediaPlayer?
    fun release()
    fun isPlaying(): Boolean // Para saber si hay una canción cargada
    fun getCurrentSongName(): String?
}