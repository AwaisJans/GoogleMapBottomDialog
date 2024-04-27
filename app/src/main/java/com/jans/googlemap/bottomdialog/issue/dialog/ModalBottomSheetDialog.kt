package com.jans.googlemap.bottomdialog.issue.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.flexbox.FlexboxLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.R.id.design_bottom_sheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.jans.googlemap.bottomdialog.issue.adapters.ImagesAdapter
import com.jans.googlemap.bottomdialog.issue.databinding.BottomSheetBehaviorBinding
import com.jans.googlemap.bottomdialog.issue.model.markerModels.ApiResponse
import com.jans.googlemap.bottomdialog.issue.model.urlDetailsMarker.Bild
import com.jans.googlemap.bottomdialog.issue.model.urlDetailsMarker.Kategorie
import com.jans.googlemap.bottomdialog.issue.model.urlDetailsMarker.SingleApiResponse
import com.jans.googlemap.bottomdialog.issue.utils.ConfigApp


class ModalBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetBehaviorBinding

    companion object {
        const val URL_DETAIL = "arg_text"
        var urlString = ""

        fun newInstance(text: String): ModalBottomSheetDialog {
            val args = Bundle().apply {
                putString(URL_DETAIL, text)
            }
            val fragment = ModalBottomSheetDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetBehaviorBinding.inflate(inflater, container, false)
        urlString = arguments?.getString(URL_DETAIL).toString()

        binding.idLoader.visibility = View.VISIBLE
        binding.nestScroll.visibility = View.GONE

        binding.expandButton.setOnClickListener {
            dialog?.dismiss()
        }


        val queue = Volley.newRequestQueue(requireContext())

        val url = urlString

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
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
                    binding.tv1.text = title

                    binding.tvTitle.text = title
                    binding.webView.loadData(htmlCode, "text/html", "UTF-8")

                    binding.idLoader.visibility = View.GONE
                    binding.nestScroll.visibility = View.VISIBLE

                    // setup kategories
                    setupKategories(kategoriesList)

                    // setup ImagesList
                    setupImages(imagesList)
                } catch (e: Exception) {
                    Log.d("list123", e.message.toString())
                }
            },
            { error: VolleyError? ->
                Log.e("list123", "Volley Error: ${error?.message}")
            }
        )

        queue.add(jsonObjectRequest)

        return binding.root
    }


    private fun setupImages(imagesList: List<Bild>) {
        binding.imagesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), HORIZONTAL, false)
        binding.imagesRecyclerView.adapter = ImagesAdapter(imagesList)
    }

    private fun setupKategories(kategoriesList: List<Kategorie>) {
        val containerKategories = binding.container

        val testItems = listOf(
            "Category 1", "Category 2", "Category 3",
            "Category 4", "Category 2", "Category 3", "Category 4", "Category 5",
            "Category 6", "Category 7", "Category 8", "Category 9", "Category 10"
        )

        for (item in kategoriesList) {
            val textView = TextView(requireContext())
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


    @SuppressLint("RestrictedApi")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        dialog?.setOnShowListener { it ->
            val d = it as BottomSheetDialog
            val bottomSheet: View = d.findViewById(design_bottom_sheet)!!
            bottomSheet.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return super.onCreateDialog(savedInstanceState)
    }
}
