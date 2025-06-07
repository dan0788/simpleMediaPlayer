package com.example.musica.manager

class SongsController {
    fun setSongs(musicList:MutableList<String>) : String{
        var text:String=""
        var count:Int=0
        for(item in musicList){
            count++
            text=text+"$count  "+item+"\n"
        }
        return text
    }
}