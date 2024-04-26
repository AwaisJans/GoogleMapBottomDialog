package com.jexample.sample.picture.firstnametest.model.markerModels

import com.jexample.sample.picture.firstnametest.utils.ConfigApp
import retrofit2.http.GET

interface ApiService {
    @GET(ConfigApp.ENDPOINTS_MARKER)
    suspend fun getData(): ApiResponse
}