package newapp.com.newshunter.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import newapp.com.newshunter.Model.APIInterface;
import newapp.com.newshunter.Model.ApiClient;
import newapp.com.newshunter.Model.CountryFinder;
import newapp.com.newshunter.Model.NewsAdapter;
import newapp.com.newshunter.Model.NewsList;
import newapp.com.newshunter.Model.RetrofitResponseListener;
import newapp.com.newshunter.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsScreen extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private Location location;
    private String country;
    private APIInterface apiInterface;
    private List<NewsList.Datum> listView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_screen);

        initialise();

    }

    private void initialise() {


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        apiInterface = ApiClient.getClient().create(APIInterface.class);
        recyclerView = (RecyclerView) findViewById(R.id.newslist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplication());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        listView = new ArrayList();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            location = getLocationData();
            country = CountryFinder.getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude());


        }
        else {
            Toast.makeText(getApplicationContext(),"Location Permission not granted",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


        getNewsList(new RetrofitResponseListener() {
            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(),"failure method",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(NewsList body) {

                listView = body.articles;
                adapter = new NewsAdapter(getApplicationContext(),listView , NewsScreen.this);
                recyclerView.setAdapter(adapter);

            }
        });

    }

    private void getNewsList(final RetrofitResponseListener retrofitResponseListener) {

        Call<NewsList> call = apiInterface.getNewsList("us","67c6296f19c2443d861fd6a878dd3548");
        call.enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {

                Toast.makeText(getApplicationContext(),""+response.body(),Toast.LENGTH_SHORT).show();
                if(response.isSuccessful()){
                    retrofitResponseListener.onSuccess(response.body());
                }
                else {

                    retrofitResponseListener.onFailure();
                }
            }

            @Override
            public void onFailure(Call<NewsList> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(),""+t,Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("MissingPermission")
    private Location getLocationData() {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return location;
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(),"Location Permission granted",Toast.LENGTH_SHORT).show();

                    location = getLocationData();
                    country = CountryFinder.getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude());

                } else {
                    // permission denied, boo! Disable the
                    // load default country India
                }
                return;
            }
        }
    }



    @Override
    public void onLocationChanged(Location location) {

        if(country == null){
            country = CountryFinder.getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude());
        }
        else {

            if(country.equals(CountryFinder.getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude()))){
                    //same country don't load new data
            }
            else{
                //load data related to another country
                country = CountryFinder.getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude());

            }

        }
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
