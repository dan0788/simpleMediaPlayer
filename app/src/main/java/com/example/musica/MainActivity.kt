package com.example.musica

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.musica.classes.GetSongsAssets
import com.example.musica.manager.MediaPlayerController
import com.example.musica.manager.SongsController
import java.io.IOException
import android.util.Log

class MainActivity : AppCompatActivity() {

    private var currentMediaPlayer: MediaPlayer? = null
    private val TAG = "MainActivityMusic"

    private lateinit var textViewSongsComponent: TextView
    private lateinit var textViewPlayerComponent: TextView
    private lateinit var playButtonComponent: Button
    private lateinit var pauseButtonComponent: Button
    private lateinit var stopButtonComponent: Button
    private lateinit var nextButtonComponent: Button
    private lateinit var backButtonComponent: Button
    private val musicList: MutableList<String> = mutableListOf()

    private lateinit var getSongsAssets:GetSongsAssets
    private lateinit var songsManager:SongsController
    private lateinit var mediaPlayerController: MediaPlayerController

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        getSongsAssets=GetSongsAssets()
        songsManager=SongsController()
        mediaPlayerController=MediaPlayerController(this)

        textViewSongsComponent = findViewById(R.id.textViewSongs)
        textViewPlayerComponent = findViewById(R.id.textViewPlayer)
        playButtonComponent = findViewById(R.id.playButton)
        pauseButtonComponent = findViewById(R.id.pauseButton)
        stopButtonComponent = findViewById(R.id.stopButton)
        nextButtonComponent = findViewById(R.id.nextButton)
        backButtonComponent = findViewById(R.id.backButton)

        var text:String=""
        val assetManager = this.assets
        musicList.addAll(getSongsAssets.getSongs(assetManager))

        mediaPlayerController.init(musicList, 0)

        text=songsManager.setSongs(musicList)
        textViewSongsComponent.text=text

        text=""

        playButtonComponent.setOnClickListener(){
            if (musicList.isNotEmpty()) {
                currentMediaPlayer = mediaPlayerController.play()
                textViewPlayerComponent.text = "Reproduciendo: ${mediaPlayerController.getCurrentSongName()}"

                currentMediaPlayer?.setOnCompletionListener {
                    textViewPlayerComponent.text = "Reproducción de ${mediaPlayerController.getCurrentSongName()} completada."
                    Log.d(TAG, "Reproducción de ${mediaPlayerController.getCurrentSongName()} completada desde MainActivity.")
                }
            } else {
                textViewPlayerComponent.text = "No hay canciones disponibles."
                Log.w(TAG, "No hay canciones en la lista para reproducir.")
            }
        }

        pauseButtonComponent.setOnClickListener(){
            mediaPlayerController.pause()
            textViewPlayerComponent.text = "Canción pausada: ${mediaPlayerController.getCurrentSongName()}"
        }

        stopButtonComponent.setOnClickListener(){
            mediaPlayerController.stop()
            mediaPlayerController.release()
            currentMediaPlayer = null
            textViewPlayerComponent.text = "Canción detenida."
        }

        backButtonComponent.setOnClickListener(){
            if (musicList.isNotEmpty()) {
                currentMediaPlayer = mediaPlayerController.backSong()
                textViewPlayerComponent.text = "Reproduciendo: ${mediaPlayerController.getCurrentSongName()}"
                currentMediaPlayer?.setOnCompletionListener {
                    textViewPlayerComponent.text = "Reproducción de ${mediaPlayerController.getCurrentSongName()} completada."
                    Log.d(TAG, "Reproducción de ${mediaPlayerController.getCurrentSongName()} completada desde MainActivity.")
                }
            }
        }

        nextButtonComponent.setOnClickListener(){
            if (musicList.isNotEmpty()) {
                currentMediaPlayer = mediaPlayerController.nextSong()
                textViewPlayerComponent.text = "Reproduciendo: ${mediaPlayerController.getCurrentSongName()}"
                currentMediaPlayer?.setOnCompletionListener {
                    textViewPlayerComponent.text = "Reproducción de ${mediaPlayerController.getCurrentSongName()} completada."
                    Log.d(TAG, "Reproducción de ${mediaPlayerController.getCurrentSongName()} completada desde MainActivity.")
                }
            }
        }
    }

    fun actualSong(songFileName:String){
        currentMediaPlayer?.release()
        currentMediaPlayer=null
        try {
            val fd = assets.openFd(songFileName)

            currentMediaPlayer = MediaPlayer().apply {
                setDataSource(
                    fd.fileDescriptor,
                    fd.startOffset,
                    fd.length
                )
                prepare()
                start()
                textViewPlayerComponent.text="Reproduciendo: $songFileName"

                setOnCompletionListener {
                    textViewPlayerComponent.text="Reproducción de $songFileName completada."
                }
            }
            fd.close()
        } catch (e: IOException) {
            textViewPlayerComponent.text="Error al reproducir $songFileName: ${e.message}"
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            textViewPlayerComponent.text="Estado ilegal al reproducir $songFileName: ${e.message}"
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentMediaPlayer?.release()
        currentMediaPlayer = null
        textViewPlayerComponent.text="MediaPlayer liberado en onDestroy."
    }
}