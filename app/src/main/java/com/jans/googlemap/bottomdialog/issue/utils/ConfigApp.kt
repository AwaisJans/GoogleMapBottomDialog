package com.jans.googlemap.bottomdialog.issue.utils

import android.content.Context
import android.widget.Toast

class ConfigApp {
    companion object{
        const val BASE_URL_MARKER = "https://www.schlier.de/index.php?id=279&baseColor=272727&baseFontSize=14&action=getGeomap2Items"
        const val TYPE_MARKER = "marker"


        fun showToast(msg:String,context:Context){
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

    }
}