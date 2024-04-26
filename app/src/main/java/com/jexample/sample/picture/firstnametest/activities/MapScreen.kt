package com.jexample.sample.picture.firstnametest.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.jexample.sample.picture.firstnametest.R
import com.jexample.sample.picture.firstnametest.model.jsonModels.MapData
import java.io.InputStream


class MapScreen : AppCompatActivity(), OnMapReadyCallback {

    var mapView: MapView? = null
    var map: GoogleMap? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_screen)

        mapView = findViewById(R.id.mapview)
        mapView!!.onCreate(savedInstanceState)

        mapView!!.getMapAsync(this)


    }


    @SuppressLint("MissingPermission", "PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.uiSettings.isMyLocationButtonEnabled = false
        map!!.isMyLocationEnabled = true

        val jsonString = readJsonFile(R.raw.new_json_file)
        val mapData: MapData = Gson().fromJson(jsonString, MapData::class.java)
        val markersCoordinates = mapData.markers.coordinates
        Log.d("maplist123","Length: ${markersCoordinates.size}\n${markersCoordinates}")
        for (coordinate in markersCoordinates) {
            val coordinates = LatLng(coordinate.lat, coordinate.lon)
            map?.addMarker(MarkerOptions().position(coordinates))
        }
        if (markersCoordinates.isNotEmpty()) {
            val firstCoordinate = markersCoordinates.firstOrNull()!!
            val firstCoordinates = LatLng(firstCoordinate.lat, firstCoordinate.lon)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(firstCoordinates, 15f))
        }


        map?.setOnMarkerClickListener { marker ->

            Toast.makeText(this, "${marker.position}", Toast.LENGTH_SHORT).show()
            true
        }





    }

    private fun readJsonFile(rawResourceId: Int): String {
        val inputStream: InputStream = resources.openRawResource(rawResourceId)
        return inputStream.bufferedReader().use { it.readText() }
    }
    override fun onResume() {
        mapView?.onResume()
        super.onResume()
    }
    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}