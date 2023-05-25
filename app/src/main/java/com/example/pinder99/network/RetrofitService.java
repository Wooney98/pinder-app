package com.example.pinder99.network;

import com.example.pinder99.data.ResponseAnimalHospital;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {
    @GET("15087531/v1/uddi:e134a963-cc0c-471e-b65a-b5eef046e1e4")
    Single<ResponseAnimalHospital> fetchHospitalLocations(
            @Query("serviceKey") String serviceKey,
            @Query("page") int page,
            @Query("perPage") int perPage
    );
}
