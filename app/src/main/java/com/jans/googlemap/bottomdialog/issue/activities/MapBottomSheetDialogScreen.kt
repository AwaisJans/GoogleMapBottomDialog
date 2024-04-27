package com.jans.googlemap.bottomdialog.issue.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.jans.googlemap.bottomdialog.issue.R
import com.jans.googlemap.bottomdialog.issue.databinding.MapBottomsheetdialogScreenBinding
import com.jans.googlemap.bottomdialog.issue.dialog.ModalBottomSheetDialog
import com.jans.googlemap.bottomdialog.issue.model.markerModels.ApiResponse
import com.jans.googlemap.bottomdialog.issue.utils.ConfigApp
import com.jans.googlemap.bottomdialog.issue.utils.ConfigApp.Companion.TYPE_MARKER


class MapBottomSheetDialogScreen : AppCompatActivity(), OnMapReadyCallback {

    lateinit var map: GoogleMap
    private lateinit var binding: MapBottomsheetdialogScreenBinding
    private val markersList: MutableList<LatLng> = mutableListOf()
    private val urlDetailList: MutableList<String> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MapBottomsheetdialogScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

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



        val queue = Volley.newRequestQueue(this)

        val url = ConfigApp.BASE_URL_MARKER

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {

                    Log.d("url123",response.toString())

                    val apiResponse: ApiResponse = Gson().fromJson(response.toString(), ApiResponse::class.java)



                    apiResponse.items.forEach { item ->
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
            },
            { error: VolleyError? ->
                Log.e("list123", "Volley Error: ${error?.message}")
            }
        )

        queue.add(jsonObjectRequest)
    }



}