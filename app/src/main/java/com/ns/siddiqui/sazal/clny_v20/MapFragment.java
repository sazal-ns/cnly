/*
 * Copyright (c) 2017. By Noor Nabiul Alam Siddiqui
 */

package com.ns.siddiqui.sazal.clny_v20;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.ns.siddiqui.sazal.clny_v20.AppConfig.AppConfig;
import com.ns.siddiqui.sazal.clny_v20.AppConfig.Constants;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.DialogShow;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener, ResultCallback<LocationSettingsResult> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button fabButton;

    private OnFragmentInteractionListener mListener;

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Location location;
    private DecimalFormat format;
    private LocationRequest mLocationRequest;
    protected LocationSettingsRequest mLocationSettingsRequest;
    protected Boolean mRequestingLocationUpdates;
    protected String mLastUpdateTime;
    protected boolean mAddressRequested;
    protected String mAddressOutput;
    // private AddressResultReceiver mResultReceiver;

    private Location tempLocation;
    private IconGenerator iconGenerator;

    protected static final String TAG = "MapFragment";
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";
    protected final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    protected final static String KEY_LOCATION = "location";
    protected final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 2
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // 3
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        format = new DecimalFormat("####.000000");

        tempLocation = new Location("");
        tempLocation.setLatitude(0.00);
        tempLocation.setLongitude(0.00);


        // mResultReceiver = new AddressResultReceiver(new Handler());
        iconGenerator = new IconGenerator(getContext());

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Set defaults, then update using values stored in the Bundle.
        /*mAddressRequested = true;
        mAddressOutput = "";*/

        updateValuesFromBundle(savedInstanceState);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        // checkLocationSettings();


        fabButton = (Button) view.findViewById(R.id.fabButton);
        fabButton.setEnabled(false);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                /*double dis = calculateDistance(tempLocation.getLatitude(), tempLocation.getLongitude(), location.getLatitude(),location.getLongitude());
                if (dis > 25.0){
                    Log.wtf(TAG, String.valueOf(dis));
                    tempLocation = location;
                    ClynfromDB(location.getLatitude(), location.getLongitude());
                }*/
                ClynfromDB(location.getLatitude(), location.getLongitude());


            }
        });

        return view;
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        // 4
        if (savedInstanceState != null) {
            Log.i(TAG, "Updating values from inside bundle");
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                location = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            //updateUI();
            moveMap();

            /*if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                //displayAddressOutput();
                Log.e(TAG,mAddressOutput +"inside bundale update");
                new DialogShow(getContext(),"Address",mAddressOutput,null);
                //Toast.makeText(getContext(),mAddressOutput,Toast.LENGTH_LONG).show();

            }*/

        }
    }

   /* protected void startIntentService() {
        Log.e("Find", "startIntentService");
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getContext(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        getActivity().startService(intent);
    }*/

    /*public void fetchAddressButtonHandler() {
        Log.e("Find", "fetchAddressButtonHandler");
        Log.i(TAG, "fetchAddressButtonHandler");
        // We only start the service to fetch the address if GoogleApiClient is connected.
        if (googleApiClient.isConnected() && location != null) {
            Log.e(TAG, "fetchAddressButtonHandler");
            startIntentService();
        }
        // If GoogleApiClient isn't connected, we process the user's request by setting
        // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to
        // fetch the address. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        //updateUIWidgets();
    }*/

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        Log.e("Find", "onButtonPressed");
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        // 1
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.e("Find", "onDetach");

        Log.d("Call", "onDetach");
        googleApiClient.disconnect();
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //12

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));

        //iconGenerator.setColor(R.color.appCOlor);
        // iconGenerator.setStyle(IconGenerator.STYLE_GREEN);
        checkLocationSettings();
    }

    private void addIcon(IconGenerator iconFactory, CharSequence text, LatLng position) {
        Log.e("Find", "addIcon");

        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        mMap.addMarker(markerOptions);
    }


    private void ClynfromDB(final double lat, final double lng) {
        Log.e("Find", "ClynfromDB");

        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.MARKER_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    //Log.e("Map", jsonObject.toString());
                    Iterator keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String dynamicKey = (String) keys.next();
                        // Log.e("key", dynamicKey);
                        if (!dynamicKey.contains("error")) {
                            JSONObject object = jsonObject.getJSONObject(dynamicKey);
                            Log.e("Map object", object.toString());
                            MarkerOptions marker = new MarkerOptions().position(new LatLng(object.getDouble("lat"), object.getDouble("lng")))
                                    .title("$" + object.getString("prize"));

                            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                            mMap.addMarker(marker);
                            // addIcon(iconGenerator,"$"+object.getString("prize"), new LatLng(object.getDouble("lat"), object.getDouble("lng")) );

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("chekc login", "Login Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();

                params.put("lat", format.format(lat));
                params.put("lng", format.format(lng));
                params.put("radius", "25");
                // params.put("locality", mAddressOutput.toLowerCase());
                Log.d("Sent to DB latitude", String.valueOf(lat));
                Log.d("Sent to DB longitude", String.valueOf(lng));
                // Log.d("Sent to DB locality", mAddressOutput.toLowerCase());

                return params;
            }
        };

        // Adding request to request queue
       /* AppController.getInstance().addToRequestQueue(strReq, tag_string_req);*/
        Volley.newRequestQueue(getContext()).add(request);
    }

    private void moveMap() {
        Log.e("Find", "moveMap");

        /**
         * Creating the latlng object to store lat, long coordinates
         * adding marker to map
         * move the camera with animation
         */
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(17).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //new DialogShow(getContext(), "Lacation", latLng.toString(), null);
        fabButton.setEnabled(true);
    }

    public static double calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        Log.e("Find", "calculateDistance");

        float[] results = new float[3];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return results[0];
    }

    protected synchronized void buildGoogleApiClient() {
        // 5

        Log.i(TAG, "Building GoogleApiClient");
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        // 6 9
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        // 7
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void checkLocationSettings() {
        //8
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                mLocationSettingsRequest);
        result.setResultCallback(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //13

        Log.i(TAG, "Connected to GoogleApiClient");
        if (location == null) {
            //fetchAddressButtonHandler();
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Log.i(TAG, "GoogleApiClientgjhgjhftytyyt");


              /*  if (!Geocoder.isPresent()) {
                    Log.e(TAG, "no_geocoder_available");
                    Toast.makeText(getContext(), "no_geocoder_available", Toast.LENGTH_LONG).show();
                }else  startIntentService();*/
            }
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            moveMap();
           /* //updateUI();
            if (location != null) {
                Log.e(TAG, "Connected to GoogleApiClient mAddressRequested");
                // Determine whether a Geocoder is available.

                // It is possible that the user presses the button to get the address before the
                // GoogleApiClient object successfully connects. In such a case, mAddressRequested
                // is set to true, but no attempt is made to fetch the address (see
                // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
                // user has requested an address, since we now have a connection to GoogleApiClient.
                if (mAddressRequested) {

                }
                //moveMap();
            }*/

        }


        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        /*if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Find", "onConnectionSuspended");

        Log.i(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(getContext(), "onMarkerClick", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("Find", "onLocationChanged");

        //getCurrentLocation();
        this.location = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        //updateUI();
        /*if (!mAddressOutput.equals(""))*/
        moveMap();
        Toast.makeText(getContext(), "Location updated", Toast.LENGTH_SHORT).show();
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.e("Find", "onSaveInstanceState");

        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, location);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(KEY_LOCATION, location);
        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        // 15

        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("Find", "onActivityResult");

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    protected void stopLocationUpdates() {
        Log.e("Find", "stopLocationUpdates");

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        // LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = false;
                // setButtonsEnabledState();
            }
        });
    }

    protected void startLocationUpdates() {
        Log.e("Find", "startLocationUpdates");

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest,
                this
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;
                //setButtonsEnabledState();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //10
        Log.d("Call", "onViewCreated");
        googleApiClient.connect();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        // 11
        super.onResume();
        if (googleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        Log.e("Find", "onPause");

        super.onPause();

        if (googleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onStop() {
        Log.e("Find", "onStop");

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    /*class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler) {

            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.e("Find", "onReceiveResult");

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            moveMap();
           // new DialogShow(getContext(),"Address",mAddressOutput,null);
            //Toast.makeText(getContext(),mAddressOutput,Toast.LENGTH_LONG).show();
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Log.e(TAG,"address_found");

                //Toast.makeText(getContext(),"address_found",Toast.LENGTH_LONG).show();
            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
           // updateUIWidgets();
        }
    }*/
}

