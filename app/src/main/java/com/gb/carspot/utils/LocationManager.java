//
//  Advanced Android - MADS4006
//  CarSpot
//
//  Group 7
//  Brian Domingo - 101330689
//  Daryl Dyck - 101338429
//

package com.gb.carspot.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

public class LocationManager
{
    private static final String TAG = "LocationManager";
    private static final LocationManager instance = new LocationManager();
    public final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private final String[] permissionArray = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    public boolean locationPermissionGranted = false;
    private FusedLocationProviderClient fusedLocationProviderClient = null;
    private LocationRequest locationRequest;
    private MutableLiveData<Location> location = new MutableLiveData<Location>();

    private LocationManager()
    {
        createLocationRequest();
    }

    // singleton initializer
    public static LocationManager getInstance()
    {
        return instance;
    }

    // create request to get current location
    private void createLocationRequest()
    {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
    }

    // check for required location permissions
    public void checkPermissions(Context context, Fragment fragment)
    {
        locationPermissionGranted = (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        Log.d(TAG, "checkPermissions: " + locationPermissionGranted);

        if (!locationPermissionGranted)
        {
            requestPermission(fragment);
        }
    }

    // request required permissions for location
    private void requestPermission(Fragment fragment)
    {
        //ActivityCompat.requestPermissions((Activity) context, permissionArray, LOCATION_PERMISSION_REQUEST_CODE);
        fragment.requestPermissions(
                permissionArray,
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    // fuse location manager to current context
    public FusedLocationProviderClient getFusedLocationProviderClient(Context context)
    {
        if (fusedLocationProviderClient == null)
        {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        }
        return fusedLocationProviderClient;
    }

    // get location information for lat and lon
    public com.gb.carspot.models.Location getLocation(Context context, Location location)
    {
        Log.d(TAG, "getLocation: Lat: " + location.getLatitude());
        Log.d(TAG, "getLocation: Lon: " + location.getLongitude());

        if (context != null)
        {
            try
            {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(context, Locale.getDefault());
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                String address = addresses.get(0).getSubThoroughfare() + " " + addresses.get(0).getThoroughfare();
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                com.gb.carspot.models.Location myLocation =
                        new com.gb.carspot.models.Location(location.getLatitude(),
                                location.getLongitude(),
                                address,
                                city,
                                country,
                                false);

                return myLocation;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        com.gb.carspot.models.Location myLocation =
                new com.gb.carspot.models.Location(location.getLatitude(),
                        location.getLongitude(),
                        "",
                        "",
                        "",
                        false);
        return myLocation;
    }

    // get location information from address string
    public com.gb.carspot.models.Location getLocationFromAddress(Context context, com.gb.carspot.models.Location location)
    {
        Geocoder coder = new Geocoder(context);
        List<Address> addressList;
        String addressString = location.getStreetAddress() + ", " + location.getCity() + ", " + location.getCountry();

        try
        {
            addressList = coder.getFromLocationName(addressString, 5);
            if (addressList == null)
            {
                return null;
            }
            Address address = addressList.get(0);
            address.getLatitude();
            address.getLongitude();

            location.setLat((double) (address.getLatitude()));
            location.setLon((double) (address.getLongitude()));

            return location;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("MissingPermission") // get users last location
    public MutableLiveData<Location> getLastLocation(Context context)
    {
        if (locationPermissionGranted)
        {
            try
            {
                getFusedLocationProviderClient(context)
                        .getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>()
                        {
                            @Override
                            public void onSuccess(Location loc)
                            {
                                if (loc != null)
                                {
                                    location.setValue(loc);
                                    Log.d(TAG, "onSuccess: Location Lat: " + location.getValue().getLatitude() +
                                            " Lon: " + location.getValue().getLongitude());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Log.e(TAG, e.toString());
                                Log.e(TAG, e.getLocalizedMessage());
                            }
                        });
            }
            catch (Exception e)
            {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
                return null;
            }
            return location;
        }
        else
        {
            // request permission again with explanation
        }
        return null;
    }

    @SuppressLint("MissingPermission") // request reoccurring location updates
    public void requestLocationUpdates(Context context, LocationCallback locationCallback)
    {
        if (locationPermissionGranted)
        {
            try
            {
                getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
            catch (Exception e)
            {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("MissingPermission") // stop location updates
    public void stopLocationUpdates(Context context, LocationCallback locationCallback)
    {
        if (locationPermissionGranted)
        {
            try
            {
                getFusedLocationProviderClient(context).removeLocationUpdates(locationCallback);
            }
            catch (Exception e)
            {
                Log.e(TAG, e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }
}
