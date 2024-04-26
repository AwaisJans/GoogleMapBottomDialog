package com.jexample.sample.picture.firstnametest.model.urlDetailsMarker

import com.jexample.sample.picture.firstnametest.model.urlDetailsMarker.SingleApiResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface SingleApiService {
    @GET
    suspend fun getData(@Url url: String): SingleApiResponse
}