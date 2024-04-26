package com.jexample.sample.picture.firstnametest.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jexample.sample.picture.firstnametest.model.urlDetailsMarker.Bild
import com.jexample.sample.picture.firstnametest.R

class ImagesAdapter(private val imagesList: List<Bild>) :
            RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val holder: RecyclerView.ViewHolder

                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.images_item, parent, false)
                holder = ImageViewHolder(view)

                return holder
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder as ImageViewHolder

                val context = holder.itemView.context
                val item = imagesList[position]
                val url = item.url

                holder.imageName.text = item.bezeichnung
                Log.d("picture123",url)

                if(item.bezeichnung.equals("")){
                    holder.imageName.visibility = View.GONE
                }

                Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.loading)
                    .into((holder.imageHolder))


            }

            class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val imageHolder: ImageView = itemView.findViewById(R.id.imageViewer)
                val imageName: TextView = itemView.findViewById(R.id.imgName)
            }
            override fun getItemCount(): Int {
                return imagesList.size
            }
        }
