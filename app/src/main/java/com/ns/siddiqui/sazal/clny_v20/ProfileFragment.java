/*
 * Copyright (c) 2017. By Noor Nabiul Alam Siddiqui
 */

package com.ns.siddiqui.sazal.clny_v20;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ns.siddiqui.sazal.clny_v20.AppConfig.AppConfig;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.DialogShow;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.DownLoadImageTask;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.PrefUtils;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.SaveImage;
import com.ns.siddiqui.sazal.clny_v20.model.FbUser;
import com.ns.siddiqui.sazal.clny_v20.model.User;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private  View view;
    private CircleImageView proPic;
    private TextView nameTextView,addressTextView,joinTextView;
    private EditText firstNameEditText,lastNameEditText,fullAddressEditText,bioEditText,favSongEditText,favPetEditText;
    private Button saveChangesButton;
    private ImageButton uploadImageButton;

    private ProgressDialog pDialog;

    private String firstName, lastName, fullAddress, bio, favSong, favPet;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "CLYN";
    private Uri fileUri;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        init();
        return view;
    }

    private void init() {
        // Progress dialog
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);

        proPic = (CircleImageView) view.findViewById(R.id.proPic);

        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        addressTextView= (TextView) view.findViewById(R.id.addressTextView);
        joinTextView = (TextView) view.findViewById(R.id.joinTextView);

        firstNameEditText= (EditText) view.findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText) view.findViewById(R.id.lastNameEditText);
        fullAddressEditText = (EditText) view.findViewById(R.id.fullAddressEditText);
        bioEditText = (EditText) view.findViewById(R.id.bioEditText);
        favSongEditText= (EditText) view.findViewById(R.id.favSongEditText);
        favPetEditText = (EditText) view.findViewById(R.id.favPetEditText);

        saveChangesButton = (Button) view.findViewById(R.id.saveChangesButton);
        saveChangesButton.setOnClickListener(onClickListener);

        uploadImageButton = (ImageButton) view.findViewById(R.id.uploadImageButton);
        uploadImageButton.setOnClickListener(onClickListener);


        loadData();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.saveChangesButton:
                firstName = firstNameEditText.getText().toString().trim();
                lastName = lastNameEditText.getText().toString().trim();
                fullAddress = fullAddressEditText.getText().toString().trim();
                bio = bioEditText.getText().toString().trim();
                favSong = favSongEditText.getText().toString().trim();
                favPet = favPetEditText.getText().toString().trim();

                if (firstName.isEmpty()) {
                    firstName = "null";
                }
                if (lastName.isEmpty()) {
                    lastName = "null";
                }
                if (fullAddress.isEmpty()) {
                    fullAddress = "null";
                }
                if (bio.isEmpty()) {
                    bio = "null";
                }
                if (favSong.isEmpty()) {
                    favSong = "null";
                }
                if (favPet.isEmpty()) {
                    favPet = "null";
                }

                pDialog.setMessage("Updating...");
                showDialog();

                StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_INFO, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("OnResponse", "Update Response: " + response);
                        hideDialog();
                        if (response.contains("false")) {
                            new DialogShow(getContext(), "Successful", "Info Update Successfully", getResources().getDrawable(R.drawable.ic_done_all_black_24dp));
                            User.setFirstName(firstName);
                            User.setLastName(lastName);
                            User.setFullAddress(fullAddress);
                            User.setBio(bio);
                            User.setFavMusic(favSong);
                            User.setFavPet(favPet);
                            loadData();
                        } else {
                            new DialogShow(getContext(), "Error?", response, getResources().getDrawable(R.drawable.error_icon));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("OnErrorResponse", "Registration Error: " + error.getMessage());
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        new DialogShow(getContext(), "Error?", error.getMessage() + "Server Problem!!!\n Please Try Again", getResources().getDrawable(R.drawable.error_icon));
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting params to register url
                        Map<String, String> params = new HashMap<>();
                        params.put("FirstName", firstName);
                        params.put("LastName", lastName);
                        params.put("FullAddress", fullAddress);
                        params.put("bio", bio);
                        params.put("FavPet", favPet);
                        params.put("FavMusic", favSong);
                        params.put("id", User.getId());

                        return params;
                    }
                };
                Volley.newRequestQueue(getContext()).add(request);
                    break;
                case R.id.uploadImageButton:
                    if (!isDeviceSupportCamera()) {
                        new DialogShow(getContext(), "Error?", "Sorry! Your device doesn't support camera", getResources().getDrawable(R.drawable.error_icon));
                    }else {
                        captureImage();}
                    break;
            }
        }
    };

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = SaveImage.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private void loadData() {
        if(User.getLoginType()!=0) {

            uploadImageButton.setVisibility(View.GONE);
            nameTextView.setText(FbUser.name);
            firstNameEditText.setText(FbUser.first_name);
            lastNameEditText.setText(FbUser.last_name);
            if (User.getLoginType()==2) {
                Log.d("g+","pic");
                proPic.setImageBitmap(DownLoadImageTask.getLogo());
            }else proPic.setImageBitmap(MainActivity.bitmap);
        }else {
            if (!User.getImageLink().contains("null")) {
                proPic.setImageBitmap(DownLoadImageTask.getLogo());
            }
        }
        if (!User.getFirstName().contains("null")) {
            firstNameEditText.setText(User.getFirstName());
        }
        if (!User.getLastName().contains("null")) {
            lastNameEditText.setText(User.getLastName());
        }
        if (!User.getBio().contains("null")) {
            bioEditText.setText(User.getBio());
        }
        if (!User.getFullAddress().contains("null")) {
            fullAddressEditText.setText(User.getFullAddress());
            addressTextView.setText(User.getFullAddress());
        }
        if (!User.getFavMusic().contains("null")) {
            favSongEditText.setText(User.getFavMusic());
        }
        if (!User.getFavPet().contains("null")) {
            favPetEditText.setText(User.getFavPet());
        }

        joinTextView.setText(User.getCreatedOn());

        if (!User.getFirstName().contains("null") && !User.getLastName().contains("null")) {
            nameTextView.setText(User.getFirstName() + " " + User.getLastName());
        } else if (!User.getFirstName().contains("null")) {
            nameTextView.setText(User.getFirstName());
        } else if (!User.getLastName().contains("null")) {
            nameTextView.setText(User.getLastName());
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            fileUri = savedInstanceState.getParcelable("file_uri");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();

            } else {
                // failed to capture image
                Toast.makeText(getContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(getContext(), UploadActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    private boolean isDeviceSupportCamera() {

        return getContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

}
