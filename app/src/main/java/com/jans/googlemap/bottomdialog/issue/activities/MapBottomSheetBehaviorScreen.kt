package com.jans.googlemap.bottomdialog.issue.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.flexbox.FlexboxLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.jans.googlemap.bottomdialog.issue.R
import com.jans.googlemap.bottomdialog.issue.adapters.ImagesAdapter
import com.jans.googlemap.bottomdialog.issue.model.jsonModels.MapData
import com.jans.googlemap.bottomdialog.issue.model.markerModels.ApiResponse
import com.jans.googlemap.bottomdialog.issue.model.urlDetailsMarker.Bild
import com.jans.googlemap.bottomdialog.issue.model.urlDetailsMarker.Kategorie
import com.jans.googlemap.bottomdialog.issue.model.urlDetailsMarker.SingleApiResponse
import com.jans.googlemap.bottomdialog.issue.utils.ConfigApp
import java.io.InputStream


class MapBottomSheetBehaviorScreen : AppCompatActivity(), OnMapReadyCallback {

    var map: GoogleMap? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private val markersList: MutableList<LatLng> = mutableListOf()
    private val urlDetailList: MutableList<String> = mutableListOf()


    private lateinit var bottomSheet:ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_bottomsheetbehavior_screen)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)


        bottomSheet = findViewById(R.id.bottomSheet)



        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View,     slideOffset:Float) {
                // handle onSlide
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {

                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    else -> {
                    }
                }
            }
        })






    }


    @SuppressLint("MissingPermission", "PotentialBehaviorOverride")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.uiSettings.isMyLocationButtonEnabled = false
        map!!.isMyLocationEnabled = true

        val pd = ProgressDialog(this, R.style.CustomAlertDialogTheme)
        pd.setTitle("Getting Markers")
        pd.setMessage("Please Wait")
        pd.show()



        val queue = Volley.newRequestQueue(this)

        val url = ConfigApp.BASE_URL_MARKER
        val id1 = bottomSheet.findViewById<RelativeLayout>(R.id.id1)

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
                        map!!.addMarker(MarkerOptions().position(coordinates))
                    }

                    Log.d("list123", markersList.size.toString())
                    if (markersList.isNotEmpty()) {
                        val firstCoordinate = markersList.firstOrNull()!!
                        val firstCoordinates =
                            LatLng(firstCoordinate.latitude, firstCoordinate.longitude)
                        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(firstCoordinates, 15f))
                        pd.dismiss()
                    }
                    map!!.setOnMarkerClickListener { marker ->
                        val markerId = Integer.parseInt(marker.id.replace("m", ""))


                        val tv1 = bottomSheet.findViewById<TextView>(R.id.tv1)
                        val webView = bottomSheet.findViewById<WebView>(R.id.webView)
                        val tvTitle = bottomSheet.findViewById<TextView>(R.id.tvTitle)
                        val idLoader = bottomSheet.findViewById<LinearLayout>(R.id.idLoader)
                        val nestScroll = bottomSheet.findViewById<NestedScrollView>(R.id.nestScroll)
                        val imagesRecyclerView = bottomSheet.findViewById<RecyclerView>(R.id.imagesRecyclerView)
                        val container = bottomSheet.findViewById<FlexboxLayout>(R.id.container)

                        idLoader.visibility = VISIBLE
                        nestScroll.visibility = GONE


                        makeVisibleBottomDialog()



                        val queue1 = Volley.newRequestQueue(this)

                        val url1 = urlDetailList[markerId]

                        val jsonObjectRequest = JsonObjectRequest(
                            Request.Method.GET, url1, null,
                            { response ->
                                try {

                                    Log.d("url123", response.toString())

                                    val setupApiResponse: SingleApiResponse =
                                        Gson().fromJson(response.toString(), SingleApiResponse::class.java)


                                    val singleItem = setupApiResponse.singleItem
                                    val kategoriesList = singleItem.kategorien
                                    val title = singleItem.bezeichnung
                                    val htmlCode = singleItem.beschreibung
                                    val imagesList = singleItem.bilder
                                    tv1.text = title

                                    tvTitle.text = title
                                    webView.loadData(htmlCode, "text/html", "UTF-8")

                                    idLoader.visibility = GONE
                                    nestScroll.visibility = VISIBLE

                                    // setup kategories
                                    setupKategories(kategoriesList,container)

                                    // setup ImagesList
                                    setupImages(imagesList,imagesRecyclerView)
                                } catch (e: Exception) {
                                    Log.d("list123", e.message.toString())
                                }
                            },
                            { error: VolleyError? ->
                                Log.e("list123", "Volley Error: ${error?.message}")
                            }
                        )

                        queue1.add(jsonObjectRequest)


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


        map!!.setOnMapClickListener {
            makeHideBottomDialog()
        }




    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if(bottomSheet.visibility == VISIBLE){
            makeHideBottomDialog()
        }else{
            finish()
        }
    }


    private fun makeVisibleBottomDialog(){
        bottomSheet.apply {
            visibility = VISIBLE
            alpha = 0f
            translationY = bottomSheet.height.toFloat()

            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(300)
                .setListener(null)
        }

    }


    private fun makeHideBottomDialog(){

        bottomSheet.animate()
            .translationY(bottomSheet.height.toFloat())
            .alpha(0.0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    bottomSheet.visibility = GONE
                }
            })


    }



    private fun setupImages(imagesList: List<Bild>,imagesRecyclerView:RecyclerView) {
        imagesRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        imagesRecyclerView.adapter = ImagesAdapter(imagesList)
    }

    private fun setupKategories(kategoriesList: List<Kategorie>,container:FlexboxLayout) {
        val containerKategories = container

        val testItems = listOf(
            "Category 1", "Category 2", "Category 3",
            "Category 4", "Category 2", "Category 3", "Category 4", "Category 5",
            "Category 6", "Category 7", "Category 8", "Category 9", "Category 10"
        )

        for (item in kategoriesList) {
            val textView = TextView(this)
            textView.text = item.bezeichnung
            textView.setTextColor(Color.WHITE)
            textView.setBackgroundResource(com.jans.googlemap.bottomdialog.issue.R.drawable.bg_kategory)
            val params = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(10, 10, 0, 15)
            textView.setPadding(10, 10, 10, 10)
            textView.layoutParams = params
            containerKategories.addView(textView)
        }


    }



    private fun sampleMarkersCode()
    {
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
    }
    private fun readJsonFile(rawResourceId: Int): String {
        val inputStream: InputStream = resources.openRawResource(rawResourceId)
        return inputStream.bufferedReader().use { it.readText() }
    }


}