package com.ashl7developer.cityexplore;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;
import JSONmodel.PhotosModel.PhotosBody;
import JSONmodel.PhotosModel.PhotoItem;
import foursquareREST.FoursquareClient;
import foursquareREST.FoursquareInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by ASHL7 on 2/19/2017.
 * Fragment to dsiplay a grid of 50 pictures for a venue
 */
public class VenuePhotoFragment extends Fragment {

    private static final String TAG = VenuePhotoFragment.class.getName();
    private String venueId;
    private GridView gridView;


    public VenuePhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        if(isNetworkAvailable()) {
            view = inflater.inflate(R.layout.fragment_venue_photo, container, false);
            venueId = getActivity().getIntent().getStringExtra(VenuePhotoActivity.VENUE_ID);
            getPhotosFromFoursquare(venueId, 50, FoursquareClient.API_DATE);
        }
        else {
            view = inflater.inflate(R.layout.no_network_layout, container, false);
        }
        return view;
    }


    /**
     * Call to Foursqaure API get pictures for a venue
     *
     * @param  venueId  The target city
     * @param  limit  Max number of returned result
     * @param  date  Number of photos to return
     */
    private void getPhotosFromFoursquare(String venueId, int limit, String date) {
        Log.d(TAG, "Getting pictures for venue ID: " + venueId);

        Call<PhotosBody> call;
        FoursquareInterface apiService =
                FoursquareClient.getClient().create(FoursquareInterface.class);
        call = apiService.getPictures(venueId, FoursquareClient.CLIENT_ID,
                FoursquareClient.CLIENT_SECRET, Integer.toString(limit), date);

        call.enqueue(new Callback<PhotosBody>() {

            List<PhotoItem> items;
            @Override
            public void onResponse(Call<PhotosBody> call, Response<PhotosBody> response) {
                if(response.isSuccessful()) {
                    Log.d(TAG, "API call for pictures was successful. Got " +
                            response.body().getResponse().getPhotos().getCount() + " pictures");
                    items = response.body().getResponse().getPhotos().getItems();
                }
                else {
                    Log.d(TAG, "API call for pictures was not successful. Error: " + response.errorBody());
                }
                showPicturesOnGrid(items);
            }

            @Override
            public void onFailure(Call<PhotosBody> call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                showPicturesOnGrid(items);
            }
        });
    }


    /**
     * Display the images received from api on grid
     * @param photos List of photos
     * @return  void
     */
    private void showPicturesOnGrid(List<PhotoItem> photos) {
        View view = getView();
        if(view != null && photos != null) {
            gridView = (GridView) view.findViewById(R.id.gridView);

            PhotoGridAdapter photoGridAdapter = new PhotoGridAdapter(getActivity(),
                    R.layout.item_grid_layout, photos);
            gridView.setAdapter(photoGridAdapter);
        }
    }


    /**
     * Check if there is internet connection
     * @return  boolean
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
