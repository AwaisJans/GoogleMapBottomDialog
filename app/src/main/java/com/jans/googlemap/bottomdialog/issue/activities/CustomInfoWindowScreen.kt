package com.jans.googlemap.bottomdialog.issue.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.jans.googlemap.bottomdialog.issue.R
import com.jans.googlemap.bottomdialog.issue.databinding.ActivityInfoWindowScreenBinding
import com.jans.googlemap.bottomdialog.issue.databinding.InfoWindowBinding
import com.jans.googlemap.bottomdialog.issue.databinding.InfoWindowLoadingBinding
import com.jans.googlemap.bottomdialog.issue.model.markerModels.ApiResponse
import com.jans.googlemap.bottomdialog.issue.model.urlDetailsMarker.SingleApiResponse
import com.jans.googlemap.bottomdialog.issue.model.urlDetailsMarker.SingleItem
import com.jans.googlemap.bottomdialog.issue.utils.ConfigApp
import com.jans.googlemap.bottomdialog.issue.utils.ConfigApp.Companion.showToast
import org.json.JSONObject


@SuppressLint("PotentialBehaviorOverride")
class CustomInfoWindowScreen : AppCompatActivity() {

    private lateinit var b: ActivityInfoWindowScreenBinding
    private var mMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private val markersList: MutableList<LatLng> = mutableListOf()
    private val urlDetailList: MutableList<String> = mutableListOf()

    private var infoWindow: ViewGroup? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityInfoWindowScreenBinding.inflate(layoutInflater)
        setContentView(b.root)

        mapFragment = supportFragmentManager.findFragmentById(b.map.id) as SupportMapFragment?
        setMap()

    }

    private fun setMap() {
        val pd = ProgressDialog(this, R.style.CustomAlertDialogTheme)
        pd.setTitle("Getting Markers")
        pd.setMessage("Please Wait")

        mapFragment!!.getMapAsync {
            mMap = it
            pd.show()

            // getting base url
            val queue = Volley.newRequestQueue(this)
            val url = ConfigApp.BASE_URL_MARKER

            // getting response
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                { response ->
                    // getting marker list
                    getMarkerList(response, pd)
                    // load items when click on marker
                    mMap!!.setOnMarkerClickListener { marker ->
                        markerClickHandler(marker)
                        true
                    }

                },
                { error: VolleyError? ->
                    Log.e("list123", "Volley Error: ${error?.message}")
                }
            )
            queue.add(jsonObjectRequest)
        }
    }

    private fun markerClickHandler(marker: Marker) {
        var bool1 = false

        val handler = Handler(Looper.getMainLooper())
        val runnable: Runnable = object : Runnable {
            override fun run() {
                val markerId = Integer.parseInt(marker.id.replace("m", ""))
                val url1 = urlDetailList[markerId]

                val queue1 = Volley.newRequestQueue(this@CustomInfoWindowScreen)

                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.GET, url1, null, { response ->
                        try {
                            Log.d("url123", response.toString())
                            val setupApiResponse: SingleApiResponse =
                                Gson().fromJson(
                                    response.toString(),
                                    SingleApiResponse::class.java
                                )
                            val item = setupApiResponse.singleItem
                            marker.tag = item
                            // now show marker info window
                            if(marker.isInfoWindowShown){
                                marker.hideInfoWindow()
                            }else{
                                marker.showInfoWindow()
                            }
                            bool1 = true
                        } catch (e: Exception) {
                            Log.d("list123", e.message.toString())
                        }
                    },
                    { error: VolleyError? ->
                        Log.e("list123", "Volley Error: ${error?.message}")
                    })
                queue1.add(jsonObjectRequest)
                Log.d("list123", "${urlDetailList[markerId]} $markerId")

                // now show info window when item is loaded
                setUpInfoWindow(bool1)
                handler.postDelayed(this, 1000)
            }
        }

        handler.post(runnable)

        Handler(Looper.getMainLooper()).postDelayed({
            handler.removeCallbacks(runnable)
        },3000)



    }

    private fun getMarkerList(response: JSONObject, pd: ProgressDialog) {
        Log.d("url123", response.toString())
        val apiResponse: ApiResponse =
            Gson().fromJson(response.toString(), ApiResponse::class.java)
        apiResponse.items.forEach { item ->
            val type = item.data.type
            item.data.coords.forEach { coords ->
                // Checking Type is Marker or not
                if (type == ConfigApp.TYPE_MARKER) {
                    // Adding Markers
                    markersList.add(LatLng(coords.lat, coords.lng))
                    // Adding URL Detail for next screen
                    urlDetailList.add(item.urlDetails)
                }
            }
        }
        for (coordinate in markersList) {
            val coordinates = LatLng(coordinate.latitude, coordinate.longitude)
            mMap!!.addMarker(MarkerOptions().position(coordinates))
        }

        Log.d("list123", markersList.size.toString())
        if (markersList.isNotEmpty()) {
            val firstCoordinate = markersList.firstOrNull()!!
            val firstCoordinates =
                LatLng(firstCoordinate.latitude, firstCoordinate.longitude)
            mMap!!.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    firstCoordinates,
                    18f
                )
            )
            pd.dismiss()
        }
    }

    private fun setUpInfoWindow(bool1: Boolean) {

        val bInfoWindow: ViewBinding?

        if(bool1){
            bInfoWindow = InfoWindowBinding.inflate(layoutInflater)
            infoWindow = bInfoWindow.root
        }else{
            bInfoWindow = InfoWindowLoadingBinding.inflate(layoutInflater)
            infoWindow = bInfoWindow.root
        }


        mMap!!.setInfoWindowAdapter(object : InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }
            override fun getInfoContents(marker: Marker): View? {
                val item = marker.tag as? SingleItem

                if(bool1){
                    bInfoWindow as InfoWindowBinding
                    bInfoWindow.titleTV.text = item?.bezeichnung

                    val strExtra = "${item?.icon!!.farbe}\n" +
                            "${item.icon.icon_style}\n" +
                            item.icon.form


                    bInfoWindow.tv1.text = strExtra



                    bInfoWindow.ivMore.setOnClickListener{
                        showToast("more", this@CustomInfoWindowScreen)
                    }


                }
                return infoWindow
            }
        })





    }

}