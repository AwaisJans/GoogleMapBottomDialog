package com.jexample.sample.picture.firstnametest.model.urlDetailsMarker

import com.jexample.sample.picture.firstnametest.model.urlDetailsMarker.Bild
import com.jexample.sample.picture.firstnametest.model.urlDetailsMarker.Data
import com.jexample.sample.picture.firstnametest.model.urlDetailsMarker.Icon
import com.jexample.sample.picture.firstnametest.model.urlDetailsMarker.Kategorie


data class SingleItem(
    val beschreibung: String,
    val bezeichnung: String,
    val bild: Bild,
    val bilder: List<Bild>,
    val dateien: List<Any>, // Define the class for this if needed
    val id: Int,
    val kategorien: List<Kategorie>,
    val icon: Icon,
    val data: Data,
    val urlPopUp: String,
    val urlDetails: String,
    val isDetailsWebsite: Boolean
)