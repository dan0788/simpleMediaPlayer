package com.example.musica.classes

import android.content.res.AssetManager
import com.example.musica.interfaces.GetSongsInterface

class GetSongsAssets : GetSongsInterface {
    override fun getSongs(assetManager: AssetManager) : MutableList<String>{
        val musicList:MutableList<String> = mutableListOf()
        val systemAssetExclusions = listOf("webkit", "images", "geoid_map, activityDim_config.xml, attributes_config.xml, decline_config.xml, freeform_resolutions.json, operators.dat, tel_uniqid_len8.dat, telocation.idf, xiaomi_mobile.dat")

        val filesInRoot = assetManager.list("")

        if (filesInRoot != null) {
            for (fileName in filesInRoot) {
                if (!systemAssetExclusions.contains(fileName) && fileName.endsWith(".mp3", ignoreCase = true)){
                    musicList.add(fileName)
                }
            }
        }

        return musicList
    }
}