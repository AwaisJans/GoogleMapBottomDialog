package com.jans.googlemap.bottomdialog.issue.model.urlDetailsMarker

import retrofit2.http.GET
import retrofit2.http.Url

interface SingleApiService {
    @GET
    suspend fun getData(@Url url: String): SingleApiResponse
}