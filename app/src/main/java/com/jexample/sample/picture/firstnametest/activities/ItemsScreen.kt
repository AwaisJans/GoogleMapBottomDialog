package com.jexample.sample.picture.firstnametest.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.jexample.sample.picture.firstnametest.R
import com.jexample.sample.picture.firstnametest.databinding.ActivityItemsScreenBinding
import com.jexample.sample.picture.firstnametest.dialog.ModalBottomSheetDialog
import com.jexample.sample.picture.firstnametest.model.markerModels.ApiService
import com.jexample.sample.picture.firstnametest.utils.ConfigApp
import com.jexample.sample.picture.firstnametest.utils.ConfigApp.Companion.TYPE_MARKER
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ItemsScreen : AppCompatActivity(), OnMapReadyCallback {

    lateinit var map: GoogleMap
    private lateinit var binding: ActivityItemsScreenBinding
    private val markersList: MutableList<LatLng> = mutableListOf()
    private val urlDetailList: MutableList<String> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mapview.onCreate(savedInstanceState)
        binding.mapview.getMapAsync(this)


        binding.backBtn.setOnClickListener {
            finish()
        }


    }

    @SuppressLint("MissingPermission", "PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isMyLocationButtonEnabled = false
        map.isMyLocationEnabled = true

        val pd = ProgressDialog(this, R.style.CustomAlertDialogTheme)
        pd.setTitle("Getting Markers")
        pd.setMessage("Please Wait")
        pd.show()

        lifecycleScope.launch {
            try {
                val response = apiResponse().getData()

                response.items.forEach { item ->
                    val type = item.data.type
                    item.data.coords.forEach { coords ->
                        // Checking Type is Marker or not
                        if (type == TYPE_MARKER) {
                            // Adding Markers
                            markersList.add(LatLng(coords.lat, coords.lng))
                            // Adding URL Detail for next screen
                            urlDetailList.add(item.urlDetails)
                        }
                    }
                }


                for (coordinate in markersList) {
                    val coordinates = LatLng(coordinate.latitude, coordinate.longitude)
                    map.addMarker(MarkerOptions().position(coordinates))
                }

                Log.d("list123", markersList.size.toString())
                if (markersList.isNotEmpty()) {
                    val firstCoordinate = markersList.firstOrNull()!!
                    val firstCoordinates =
                        LatLng(firstCoordinate.latitude, firstCoordinate.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstCoordinates, 15f))
                    pd.dismiss()
                }
                map.setOnMarkerClickListener { marker ->
                    val markerId = Integer.parseInt(marker.id.replace("m", ""))
                    val modal = ModalBottomSheetDialog.newInstance(urlDetailList[markerId])
//                    modal.isCancelable = false

                    modal.show(supportFragmentManager,"")

                    Log.d("list123", "${urlDetailList[markerId]} $markerId")
                    true
                }


            } catch (e: Exception) {
                Log.d("list123", e.message.toString())

            }
        }

    }


    private fun apiResponse(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(ConfigApp.BASE_URL_MARKER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }


    override fun onResume() {
        binding.mapview.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapview.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapview.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapview.onLowMemory()
    }


}