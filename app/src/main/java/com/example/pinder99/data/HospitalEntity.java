package com.example.pinder99.data;

import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

public class HospitalEntity {
    private String hospitalName;
    private LatLng latLng;

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public LatLng getLatLng() { return latLng; }
    public void setLatLng(LatLng latLng) { this.latLng = latLng; }
}
