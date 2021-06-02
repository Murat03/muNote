package com.muratipek.munote;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastLocation;
    Integer providerStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if(providerStatus == 1){
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17));
                    providerStatus = 0;
                }
            }
            @Override
            public void onProviderEnabled(@NonNull String provider) {
                providerStatus = 1;
            }
            @Override
            public void onProviderDisabled(@NonNull String provider) {
                providerStatus = 0;
                Toast.makeText(MapsActivity.this, "Please Turn On GPS", Toast.LENGTH_LONG).show();
            }
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
        }else{
            //location
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastLocation != null){
                LatLng userLastLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLastLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation, 17));
            }
        }
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.clear();

        String address = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addressList != null && addressList.size() > 0){/*
                if(addressList.get(0).getCountryName() != null){
                    address += addressList.get(0).getCountryName()+" ";
                }
                if(addressList.get(0).getAdminArea() != null){
                    address += addressList.get(0).getAdminArea()+" ";
                }
                if(addressList.get(0).getSubAdminArea() != null){
                    address += addressList.get(0).getSubAdminArea();
                }
                if (addressList.get(0).getSubThoroughfare() != null){
                    address += addressList.get(0).getSubThoroughfare();
                }*/
                if(addressList.get(0).getAddressLine(0) != null){
                    address += addressList.get(0).getAddressLine(0);
                }
/*
                address += addressList.get(0).getSubThoroughfare();
                address += addressList.get(0).getAddressLine(0);
                address += addressList.get(0).getFeatureName();
                */

            }
            Intent intentToNote = new Intent(this, NoteActivity.class);
            intentToNote.putExtra("address", address);
            intentToNote.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentToNote);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length > 0){
            if(requestCode == 3){
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(lastLocation != null){
                        LatLng userLastLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation, 17));
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}