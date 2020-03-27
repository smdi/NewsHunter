package newapp.com.newshunter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.LocaleList;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NewsScreen extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private TextView locationtv;
    private Location location;
    private String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_screen);


        locationtv = (TextView) findViewById(R.id.test);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            location = getLocationData();
            country = getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude());
            locationtv.setText(country);

        }
        else {
            Toast.makeText(getApplicationContext(),"Location Permission not granted",Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

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
                    locationtv.setText(getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude()));

                } else {
                    // permission denied, boo! Disable the
                    // load default country India
                }
                return;
            }
        }
    }

    public static String getCountryName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address result;

            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getCountryName();
            }
            return null;
        } catch (IOException ignored) {
            //do something
        }
        return "";
    }

    @Override
    public void onLocationChanged(Location location) {

        if(country == null){
            country = getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude());
        }
        else {

            if(country.equals(getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude()))){
                    //same country dont load new data
            }
            else{
                //load data related to another country
                country = getCountryName(getApplicationContext(),location.getLatitude(),location.getLongitude());
                
            }

        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
}
