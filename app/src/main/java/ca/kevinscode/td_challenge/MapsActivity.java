package ca.kevinscode.td_challenge;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ca.kevinscode.td_challenge.ui.database.DBAdapter;
import ca.kevinscode.td_challenge.ui.database.Location;

public class MapsActivity extends com.google.android.gms.maps.SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.InfoWindowAdapter {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationClient;
    private DBAdapter db;

    private final String TAG ="TD_Challenge_Kevin_Cox";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.mapView);

        mapFragment.getMapAsync(this);
        // Checks if ACCESS_FINE_LOCATION permission is granted
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        googleApiClient = new GoogleApiClient.Builder
                (this.getContext(), this, this)
                .addApi(LocationServices.API).build();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.getContext());

        db = new DBAdapter(this.getContext());
    }


    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();}
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(this);

        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this.getContext(), R.raw.map_style));

        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to Google Play Services!");

        //For my demo I will be using a predetermined location Toronto, Ontario
        //In regular use we would get and place a location at the user
//        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location lastLocation) {
//
//                        if (lastLocation != null) {
//                            double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
//                            LatLng loc = new LatLng(lat, lon);
//                            Log.i(TAG, loc.toString());
//
//
//                            mMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                                    .title("Your Current Location").snippet(getCurrAddress(loc)));
//
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 14));
//                        }
//                    }
//                });

        //Place Marker In Toronto Location
        final double TORONTO_LAT = 43.647380, TORONTO_LONG = -79.380980;
        LatLng loc = new LatLng(TORONTO_LAT, TORONTO_LONG);

        mMap.addMarker(new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Your Current Location").snippet(getCurrAddressFromLatLong(loc)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));

        generateNearByLocationMarkers();
    }

    public void generateNearByLocationMarkers(){

        //connect to db
        db.open();

        final double TORONTO_LAT = 43.647380, TORONTO_LONG = -79.380980;
        LatLng loc = new LatLng(TORONTO_LAT, TORONTO_LONG);

        //we are on explore so lets see everything!
        List<Location> foundLocations =  db.getLocationsInRange(10000, loc, "");

        db.close();

        //if in range, place as markers
        for (int i =0 ; i < foundLocations.size(); i++){
            BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            if (foundLocations.get(i).getCategoryTags().contains("Shopping")){
                icon = BitmapDescriptorFactory.fromResource(R.drawable.shop);
            }else if (foundLocations.get(i).getCategoryTags().contains("Food and Dining")){
                icon = BitmapDescriptorFactory.fromResource(R.drawable.food);
            }else if (foundLocations.get(i).getCategoryTags().contains("Auto and Transport")){
                icon =BitmapDescriptorFactory.fromResource(R.drawable.car);
            }else if (foundLocations.get(i).getCategoryTags().contains("Health and Fitness")){
                icon =BitmapDescriptorFactory.fromResource(R.drawable.fitness);
            }else if (foundLocations.get(i).getCategoryTags().contains("Home")){
                icon =BitmapDescriptorFactory.fromResource(R.drawable.home_store);
            }else if (foundLocations.get(i).getCategoryTags().contains("Entertainment")){
                icon =BitmapDescriptorFactory.fromResource(R.drawable.entertainment);
            }
            DecimalFormat df = new DecimalFormat("0.00");
            mMap.addMarker(new MarkerOptions().position(new LatLng(foundLocations.get(i).getLocationLatitude(), foundLocations.get(i).getLocationLongitude())).icon(icon).title(foundLocations.get(i).getMerchantName()).snippet("Average spending of $" + df.format(foundLocations.get(i).getCurrencyAmount()) + "\n" + foundLocations.get(i).getLocationStreet() +", " + foundLocations.get(i).getLocationCity()));


        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG,
                "Connection suspended to Google Play Services!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG,
                "Can't connect to Google Play Services!");
    }

    public String getCurrAddressFromLatLong(LatLng location)
    {
        Geocoder geocoder = new Geocoder(this.getContext(), Locale.getDefault());
        String errorMessage = "";
        String currAddress = "";  // string to return with address

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " + "Latitude = " + location.latitude + ", Longitude = " + location.longitude, illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found);
                Log.e(TAG, errorMessage);
            }
            return currAddress;
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();

            // Fetch the address lines using getAddressLine,
            // join them, and return as String.
            for(int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.address_found));
            return TextUtils.join(System.getProperty("line.separator"),
                    addressFragments);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        LinearLayout info = new LinearLayout(getContext());
        info.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(getContext());
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());

        TextView snippet = new TextView(getContext());
        snippet.setTextColor(Color.GRAY);
        snippet.setGravity(Gravity.CENTER);
        snippet.setText(marker.getSnippet());

        info.addView(title);
        info.addView(snippet);
        return info;
    }
}
