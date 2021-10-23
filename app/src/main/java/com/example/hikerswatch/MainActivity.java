package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    TextView latitude;
    TextView longitude;
    TextView accuracy;
    TextView altitude;
    TextView address;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //checks for the permission request result. we double check that the returned result is granted and we check the permission it self if it is granted.
        //then we request location updates.

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = findViewById(R.id.latTextView);
        longitude = findViewById(R.id.longTextView);
        accuracy = findViewById(R.id.accuracyTextView);
        altitude = findViewById(R.id.altitudeTextView);
        address = findViewById(R.id.addressTextView);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //changes location on map when user location changes.
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                String lat_val = "Latitude: "+Double.toString(location.getLatitude());
                String lon_val = "Longitude: "+Double.toString(location.getLongitude());
                String altitude_val = "Altitude: "+Double.toString(location.getAltitude());
                String accuracy_val = "Accuracy: "+Float.toString(location.getAccuracy());
                String address_val = "Address: ";
                latitude.setText(lat_val);
                longitude.setText(lon_val);
                altitude.setText(altitude_val);
                accuracy.setText(accuracy_val);
                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    if(addressList.size() > 0 && addressList.get(0) != null){
                        address_val+= addressList.get(0).getThoroughfare();
                    }
                    address.setText(address_val);
                }catch(Exception e){
                    e.printStackTrace();
                }




            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //this will execute on the first run of maps activity , if we changed our location the onLocationChanged() method will get called.
        /*
         * 1-first we check for version to take proper action.
         * 2-check if we took user permission to use GPS on the device.
         * 3-if we don't, we ask for one.
         * 4-if we have, we will set initial location to the last known user location */

        if (Build.VERSION.SDK_INT < 23) {
            //old version? so we will ask for updates immediately.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            //version is new
            //check if already has permission or not
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //we don't have permission, so we ask for it.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            } else {
                // we have permission, se we request to update the location.
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                // initially we will have no location on the map. so we need to use user last known location till the user change his/her location.
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                // clear the already location markers to prevent multiple markers on screen when changing location.

            }
        }



    }
}








