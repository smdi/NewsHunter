package newapp.com.newshunter.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import newapp.com.newshunter.Model.APIInterface;
import newapp.com.newshunter.Model.ApiClient;
import newapp.com.newshunter.Model.CountryFinder;
import newapp.com.newshunter.Model.NewsAdapter;
import newapp.com.newshunter.Model.NewsList;
import newapp.com.newshunter.Model.RetrofitResponseListener;
import newapp.com.newshunter.R;
import newapp.com.newshunter.ViewModel.NewsScreenViewModel;


public class NewsScreen extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private Location location;
    private String country;
    private APIInterface apiInterface;
    private List<NewsList.Datum> listView;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private NewsScreenViewModel newsScreenViewModel;
    private ProgressDialog progressDialog;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_screen);

        initialise();



    }

    private void initialise() {


        adinitialiser();

        onesignalInitialiser();

        progressDialog = new ProgressDialog(NewsScreen.this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

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



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            progressDialog.show();

            location = getLocationData();
            country = CountryFinder.getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude());


            newsScreenViewModel = new ViewModelProvider(this ,new NewsFactory(getApplicationContext(), country, "business" )).get(NewsScreenViewModel.class);

            newsScreenViewModel.getNewsList(new RetrofitResponseListener() {
                @Override
                public void onFailure() {
                    Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

                @Override
                public void onSuccess(NewsList body) {

                    listView = body.articles;
                    adapter = new NewsAdapter(getApplicationContext(),listView , NewsScreen.this);
                    recyclerView.setAdapter(adapter);
                    progressDialog.dismiss();

                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(),"Location Permission not granted",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


    }

    private void onesignalInitialiser() {
        // OneSignal Initialization
        OneSignal.startInit(this)

                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new NotificationOpenHandler())
                .init();
    }

    private class NotificationOpenHandler implements OneSignal.NotificationOpenedHandler {
        // This fires when a notification is opened by tapping on it.
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {


            Log.d("APP  " +result.notification.payload.launchURL, "Notification clicked");
//            Toast.makeText(getApplicationContext(),""+result,Toast.LENGTH_SHORT).show();

            String url = result.notification.payload.launchURL;


            Intent mIntent = new Intent(NewsScreen.this, NotificationWebview.class);
            mIntent.putExtra("url", url);
            startActivity(mIntent);

        }
    }

    private void adinitialiser() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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

//                    progressDialog.show();

                    location = getLocationData();
                    country = CountryFinder.getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude());


                    newsScreenViewModel = new ViewModelProvider(this , new NewsFactory(getApplicationContext(),country,"business" )).get(NewsScreenViewModel.class);


                    newsScreenViewModel.getNewsList(new RetrofitResponseListener() {
                        @Override
                        public void onFailure() {
                            Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onSuccess(NewsList body) {

                            listView = body.articles;
                            adapter = new NewsAdapter(getApplicationContext(),listView , NewsScreen.this);
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();

                        }
                    });

                } else {
                    // permission denied
                    // load default country us
                    Toast.makeText(getApplicationContext(),"loading default",Toast.LENGTH_SHORT).show();
                    newsScreenViewModel = new ViewModelProvider(this , new NewsFactory(getApplicationContext(),"us","business" )).get(NewsScreenViewModel.class);
                    newsScreenViewModel.getNewsList(new RetrofitResponseListener() {
                        @Override
                        public void onFailure() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(NewsList body) {

                            listView = body.articles;
                            adapter = new NewsAdapter(getApplicationContext(),listView , NewsScreen.this);
                            recyclerView.setAdapter(adapter);
                            progressDialog.dismiss();
                        }
                    });

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


                newsScreenViewModel = new ViewModelProvider(this , new NewsFactory(getApplicationContext(),country,"business" )).get(NewsScreenViewModel.class);
                newsScreenViewModel.getNewsList(new RetrofitResponseListener() {
                    @Override
                    public void onFailure() {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"No data found",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(NewsList body) {

                        listView = body.articles;
                        adapter = new NewsAdapter(getApplicationContext(),listView , NewsScreen.this);
                        recyclerView.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                });


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
