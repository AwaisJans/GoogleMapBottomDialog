package com.jans.googlemap.bottomdialog.issue.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.R.id.design_bottom_sheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jans.googlemap.bottomdialog.issue.R
import com.jans.googlemap.bottomdialog.issue.adapters.ImagesAdapter
import com.jans.googlemap.bottomdialog.issue.databinding.BottomSheetBehaviorBinding
import com.jans.googlemap.bottomdialog.issue.model.urlDetailsMarker.Bild
import com.jans.googlemap.bottomdialog.issue.model.urlDetailsMarker.Kategorie
import com.jans.googlemap.bottomdialog.issue.model.urlDetailsMarker.SingleApiService
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ModalBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetBehaviorBinding

    companion object {
        const val URL_DETAIL = "arg_text"
        var baseUrl = ""
        var endpoint = ""
        var urlString = ""
        var questionMarkIndex = 0


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


        lifecycleScope.launch {
            val singleItem = setupApiResponse().getData(urlString).singleItem
            val kategoriesList = singleItem.kategorien
            val title = singleItem.bezeichnung
            val htmlCode = singleItem.beschreibung
            val imagesList = singleItem.bilder

            binding.tvTitle.text = title
            binding.webView.loadData(htmlCode, "text/html", "UTF-8")

            binding.idLoader.visibility = View.GONE
            binding.nestScroll.visibility = View.VISIBLE

            // setup kategories
            setupKategories(kategoriesList)

            // setup ImagesList
            setupImages(imagesList)




            val textView = binding.tv1

// Set the height of the TextView
//            val desiredHeight = 50
//            textView.layoutParams.height = desiredHeight
//            textView.layoutParams.width = desiredHeight
//
//// Set the top drawable with the desired height
//            val drawable = resources.getDrawable(android.R.drawable.ic_lock_idle_alarm)
//            drawable.setBounds(0, 0, drawable.intrinsicWidth, desiredHeight)
//            textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)



        }

        return binding.root
    }


    private fun setupImages(imagesList:List<Bild>){
        binding.imagesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), HORIZONTAL, false)
        binding.imagesRecyclerView.adapter = ImagesAdapter(imagesList)
    }

    private fun setupKategories(kategoriesList:List<Kategorie>){
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

    private fun setupApiResponse():SingleApiService{
        questionMarkIndex = urlString.indexOf("?")
        if (questionMarkIndex != -1) {
            baseUrl = urlString.substring(0, questionMarkIndex)
            endpoint = urlString.substring(questionMarkIndex + 1)

        }

        val retrofit = Retrofit.Builder()
            .baseUrl("$baseUrl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        return retrofit.create(SingleApiService::class.java)
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
