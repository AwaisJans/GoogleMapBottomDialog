package com.jans.googlemap.bottomdialog.issue.model.markerModels

import com.jans.googlemap.bottomdialog.issue.utils.ConfigApp
import retrofit2.http.GET

interface ApiService {
    @GET(ConfigApp.ENDPOINTS_MARKER)
    suspend fun getData(): ApiResponse
}