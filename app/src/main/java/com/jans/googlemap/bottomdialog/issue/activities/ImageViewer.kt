package com.jans.googlemap.bottomdialog.issue.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bumptech.glide.Glide
import com.jans.googlemap.bottomdialog.issue.R
import com.jans.googlemap.bottomdialog.issue.model.urlDetailsMarker.Bild
import java.util.Objects


class ImageViewer : AppCompatActivity() {
    lateinit var tvTitle: TextView

    var mViewPager: ViewPager? = null
    var mViewPagerAdapter: ViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        tvTitle = findViewById(R.id.tvNumber)

        findViewById<ImageView>(R.id.backBtn).setOnClickListener{
            finish()
        }

        setUpVP()

    }


    private fun setUpVP(){
        val arraylist = intent.getSerializableExtra("imageList") as? ArrayList<*>
        val imageList = arraylist!!.filterIsInstance<Bild>()
        Log.d("list123", "$imageList")

        mViewPager = findViewById<View>(R.id.viewPagerMain) as ViewPager
        mViewPagerAdapter = ViewPagerAdapter(this@ImageViewer, imageList)
        mViewPager!!.adapter = mViewPagerAdapter


        mViewPager!!.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                val textTitle = position + 1
                tvTitle.text = textTitle.toString()
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    class ViewPagerAdapter(
        var context: Context,
        var images: List<Bild>
    ) : PagerAdapter() {
        var mLayoutInflater: LayoutInflater =
            context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return images.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as LinearLayout
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView: View = mLayoutInflater.inflate(R.layout.item_images_vp, container, false)

            val imageView = itemView.findViewById<View>(R.id.imageViewMain) as ImageView

            Glide.with(context)
                .load(images[position].url)
                .placeholder(R.drawable.loading)
                .into((imageView))


            Objects.requireNonNull(container).addView(itemView)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as LinearLayout)
        }
    }


}