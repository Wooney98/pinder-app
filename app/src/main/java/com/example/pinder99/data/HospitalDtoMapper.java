package com.example.pinder99.data;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.example.pinder99.data.HospitalEntity;
import com.example.pinder99.data.HospitalInfoDto;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

public class HospitalDtoMapper {
    private Context context;
    private Geocoder geocoder;

    public HospitalDtoMapper(Context context) {
        this.context = context;
        geocoder = new Geocoder(context, Locale.KOREA);
    }

/** API에서 받아온 주소 데이터를 위도와 경도로 변환 **/
    public HospitalEntity convertToHospitalEntity(HospitalInfoDto hospital) {
        HospitalEntity hospitalEntity = new HospitalEntity();
        LatLng location = null;
        try {
            List<Address> address = geocoder.getFromLocationName(hospital.get주소(),1);
            if (address == null) {
                return null;
            }
            location = new LatLng(address.get(0).getLatitude(), address.get(0).getLongitude());
            hospitalEntity.setHospitalName(hospital.get치료소명());
            hospitalEntity.setLatLng(location);
            return hospitalEntity;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
