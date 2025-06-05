package com.example.musica

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class MainActivity : AppCompatActivity() {

    // Declarar MediaPlayer a nivel de clase para poder controlarlo
    private var mediaPlayer: MediaPlayer? = null
    private val TAG = "MainActivityMusic"

    //definir componentes
    private lateinit var textViewComponent: TextView
    private lateinit var textView2Component: TextView
    private lateinit var playButtonComponent: Button
    private lateinit var pauseButtonComponent: Button
    private lateinit var stopButtonComponent: Button
    private lateinit var nextButtonComponent: Button
    private lateinit var backButtonComponent: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        textViewComponent = findViewById(R.id.textView)
        textView2Component = findViewById(R.id.textView2)
        playButtonComponent = findViewById(R.id.playButton)
        pauseButtonComponent = findViewById(R.id.pauseButton)
        stopButtonComponent = findViewById(R.id.stopButton)
        nextButtonComponent = findViewById(R.id.nextButton)
        backButtonComponent = findViewById(R.id.backButton)

        var text:String=""
        var count:Int=0
        var indexSongs:Int=0

        val musicList:MutableList<String> = mutableListOf()
        val assetManager = this.assets
        val systemAssetExclusions = listOf("webkit", "images", "geoid_map")

        // Obtener los nombres de archivos de la raíz de la carpeta assets
        val filesInRoot = assetManager.list("")

        if (filesInRoot != null) {
            for (fileName in filesInRoot) {
                if (!systemAssetExclusions.contains(fileName)){
                    musicList.add(fileName)
                }
            }
        }

        for(item in musicList){
            count++
            text=text+"$count  "+item+"\n"
        }
        textViewComponent.text=text

        count=0
        text=""

        playButtonComponent.setOnClickListener(){
            if (musicList.isNotEmpty()) {
                if (mediaPlayer == null) {
                    actualSong(musicList[indexSongs])
                    textView2Component.text="Iniciando nueva reproducción: ${musicList[indexSongs]}"
                } else if (!mediaPlayer!!.isPlaying) {
                    mediaPlayer?.start()
                    textView2Component.text="Reanudando reproducción: ${musicList[indexSongs]}"
                }
            } else {
                textView2Component.text="No hay canciones en la lista para reproducir."
            }
        }

        pauseButtonComponent.setOnClickListener(){
            mediaPlayer?.pause()
            textView2Component.text="Canción pausada."
        }

        stopButtonComponent.setOnClickListener(){
            mediaPlayer?.stop()
            mediaPlayer?.reset() // Prepara el MediaPlayer para una nueva fuente de datos
            textView2Component.text="Canción detenida."
        }

        backButtonComponent.setOnClickListener(){
            if (musicList.isNotEmpty()) {
                indexSongs--
                if (indexSongs < 0) {
                    indexSongs = musicList.size - 1 // Vuelve al final si es la primera
                }
                actualSong(musicList[indexSongs])
            }
            textView2Component.text="Iniciando nueva reproducción: ${musicList[indexSongs]}"
        }

        nextButtonComponent.setOnClickListener(){
            if (musicList.isNotEmpty()) {
                indexSongs++
                if (indexSongs >= musicList.size) {
                    indexSongs = 0 // Vuelve al principio si es la última
                }
                actualSong(musicList[indexSongs])
            }
            textView2Component.text="Iniciando nueva reproducción: ${musicList[indexSongs]}"
        }
    }

    fun actualSong(songFileName:String){
        mediaPlayer?.release()
        mediaPlayer=null
        try {
            val fd = assets.openFd(songFileName)

            mediaPlayer = MediaPlayer().apply {
                setDataSource(
                    fd.fileDescriptor,
                    fd.startOffset,
                    fd.length
                )
                prepare()
                start()
                textView2Component.text="Reproduciendo: $songFileName"

                setOnCompletionListener {
                    textView2Component.text="Reproducción de $songFileName completada."
                }
            }
            fd.close()
        } catch (e: IOException) {
            textView2Component.text="Error al reproducir $songFileName: ${e.message}"
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            textView2Component.text="Estado ilegal al reproducir $songFileName: ${e.message}"
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        textView2Component.text="MediaPlayer liberado en onDestroy."
    }
}