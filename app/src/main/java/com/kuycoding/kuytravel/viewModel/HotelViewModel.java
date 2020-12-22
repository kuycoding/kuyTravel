package com.kuycoding.kuytravel.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kuycoding.kuytravel.model.Hotel;

import java.util.List;

public class HotelViewModel extends ViewModel {
    private MutableLiveData<List<Hotel>> hotel;
    private HotelRepository hotelRepository ;

    interface HotelRepository {

    }
}
