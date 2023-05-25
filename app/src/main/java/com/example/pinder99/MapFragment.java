package com.example.pinder99;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.pinder99.data.HospitalEntity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import net.daum.mf.map.api.CameraUpdate;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private MapView mapView;
    private MapViewModel mapViewModel;
    private static final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private MapPOIItem currentLocationMarker = new MapPOIItem();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);
        fusedLocationProviderClient = new FusedLocationProviderClient(requireActivity());
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        initMapView();
        permissionResult.launch(permissions);
        fetchAnimalHospitalData(mapViewModel);
        loadHospitalMarkers(mapViewModel);
        showProgressBar(mapViewModel);
    }

    private void initMapView() {
        // 맵을 초기화 시켜준다. 원하는 zoom level도 설정할 수 있다.
        mapView = requireView().findViewById(R.id.relativelayout_mapview);
        mapView.setZoomLevel(7,false);
    }

    // 공공데이터 api http 통신시, request를 page별로 요청할 수 있다.
    // currentPage를 설정해놓고, 다음 페이지에 데이터가 존재하지 않을 때까지 요청한다.
    private void fetchAnimalHospitalData(MapViewModel mapViewModel) {
        mapViewModel.getCurrentPage().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mapViewModel.fetchHospitals(requireContext());
            }
        });
    }

    // 서버로부터 response가 정상적으로 수신되고, 동물병원 / 보호소의 장소들이 업데이트 되면 marker를 맵에 찍는다.
    // LiveData를 이용하여 뷰는 data를 관찰한다. (Observer Pattern)
    // data를 관찰중에 data가 변경되면  onChanged() 블록이 실행된다.
    private void loadHospitalMarkers(MapViewModel mapViewModel) {
        mapViewModel.getHospital().observe(getViewLifecycleOwner(), new Observer<HospitalEntity>() {
            @Override
            public void onChanged(HospitalEntity hospitalEntity) {
                drawMarker(hospitalEntity);
            }
        });
    }

    // 마커를 찍는 함수는 게속해서 재활용할 예정이기 때문에 따로 만들어놓았다.
    // 위치값을 파라미터로 넘겨받아 맵에 노출시켜둔다.
    private void drawMarker(HospitalEntity hospitalEntity) {
        if (hospitalEntity.getLatLng() != null) {
            MapPOIItem marker = new MapPOIItem();
            // marker의 위치 설정
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(hospitalEntity.getLatLng().latitude, hospitalEntity.getLatLng().longitude);
            // 마커의 장소 이름 설정
            marker.setItemName(hospitalEntity.getHospitalName());
            marker.setMapPoint(mapPoint);
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 클릭했을 때 마커 모양
            mapView.addPOIItem(marker);
        }
    }

    // 동물 병원, 보호소가 업데이트 되는 중이라는 상황을 유저에게 알려주기 위해 업데이트 과정동안 텍스트를 노출
    private void showProgressBar(MapViewModel mapViewModel) {
        LinearLayout linearLayoutLoading = getView().findViewById(R.id.linearlayout_loading);
        mapViewModel.getIsLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                if(isLoading) {
                    linearLayoutLoading.setVisibility(View.VISIBLE);
                    blinkLoadingView();
                } else if (linearLayoutLoading.getAnimation() != null) {
                    linearLayoutLoading.setAnimation(null);
                    linearLayoutLoading.setVisibility(View.GONE);
                }
            }
        });
    }

    // 업데이트중이라는 텍스트가 깜빡이는 animation을 설정해주었다.
    private void blinkLoadingView() {
        LinearLayout linearLayoutLoading = getView().findViewById(R.id.linearlayout_loading);
        Animation blinkAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_blink);
        blinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayoutLoading.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        linearLayoutLoading.startAnimation(blinkAnimation);
    }

    private ActivityResultLauncher<String[]> permissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    if (result.values().stream().allMatch(r -> r.equals(true))) {
                        updateCurrentLocation();

                    }
                }
            });

    // FusedLocationProviderClient를 이용하여 현재 나의 위치를 업데이트 한다.
    // 정상적으로 위치를 가져오게 되면 addOnListener의 onSuccess함수 안으로 위치값이 떨어진다.
    // 이후 현재 나의 위치를 맵에 찍어준다.
    @SuppressLint("MissingPermission")
    private void updateCurrentLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        drawCurrentLocationMarker(location.getLatitude(), location.getLongitude());
                        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()),4,true);
                    }
                });
    }

    // 현재위치를 좌표에 마커 표시
    // 기본적으로 마커를 찍는 방법은 상단에 있는 동물병원/보호소 찍는 방법과 같다.
    private void drawCurrentLocationMarker(Double latitude, Double longitude) {
        if (currentLocationMarker != null) {
            mapView.removePOIItem(currentLocationMarker);
        }

        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
        currentLocationMarker.setItemName("현재 위치");
        currentLocationMarker.setMapPoint(mapPoint);
        currentLocationMarker.setMarkerType(MapPOIItem.MarkerType.YellowPin);

        currentLocationMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(currentLocationMarker);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
