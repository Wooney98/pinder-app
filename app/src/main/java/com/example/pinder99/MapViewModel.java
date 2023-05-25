package com.example.pinder99;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pinder99.data.HospitalDtoMapper;
import com.example.pinder99.data.HospitalEntity;
import com.example.pinder99.data.HospitalInfoDto;
import com.example.pinder99.data.ResponseAnimalHospital;
import com.example.pinder99.network.RetrofitProvider;
import com.example.pinder99.network.RetrofitService;

import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapViewModel extends ViewModel {
    private static final int itemCountPerPage = 50;
    private MutableLiveData<Integer> currentPage = new MutableLiveData<Integer>(1);
    public MutableLiveData<Integer> getCurrentPage() { return currentPage; }
    public void increaseCurrentPage() {
        int pageIdx = currentPage.getValue();
        currentPage.setValue(pageIdx + 1);
    }

    private MutableLiveData<HospitalEntity> hospital = new MutableLiveData<HospitalEntity>();
    public LiveData<HospitalEntity> getHospital() { return hospital; }
    public void setHospital(HospitalEntity hospitalInfo) {
        hospital.setValue(hospitalInfo);
    }

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<Boolean>(false);
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public void setIsLoading(Boolean loading) {
        isLoading.setValue(loading);
    }

    public void fetchHospitals(Context context) {
        RetrofitProvider retrofitService = new RetrofitProvider();
        RetrofitService retrofit = retrofitService.createRetrofit().create(RetrofitService.class);
        retrofit.fetchHospitalLocations(
                BuildConfig.SERVICE_KEY,
                currentPage.getValue(),
                itemCountPerPage
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ResponseAnimalHospital>() {
                    @Override
                    public void onSuccess(@NonNull ResponseAnimalHospital responseAnimalHospital) {
                        if (responseAnimalHospital.getData().stream().count() > 0) increaseCurrentPage();
                        convertToHospitalEntity(context, responseAnimalHospital);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("error", e.getMessage());
                    }
                });
    }

    private void convertToHospitalEntity(Context context, ResponseAnimalHospital responseAnimalHospital) {
        HospitalDtoMapper hospitalDtoMapper = new HospitalDtoMapper(context);

        Flowable.just(responseAnimalHospital.getData())
                .flatMap(hospitals -> Flowable.fromIterable(hospitals))
                .map(hospital -> hospitalDtoMapper.convertToHospitalEntity(hospital))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        hospitalEntity -> {
                            if (!isLoading.getValue()) setIsLoading(true);
                            if (hospitalEntity.getLatLng() != null) setHospital(hospitalEntity);
                        },
                        error -> setIsLoading(false),
                        () -> setIsLoading(false)
                );
    }
}
