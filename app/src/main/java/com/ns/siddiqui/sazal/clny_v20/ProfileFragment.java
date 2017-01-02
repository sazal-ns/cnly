/*
 * Copyright (c) 2017. By Noor Nabiul Alam Siddiqui
 */

package com.ns.siddiqui.sazal.clny_v20;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.ns.siddiqui.sazal.clny_v20.model.User;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


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

    private ProgressDialog pDialog;

    private String firstName, lastName, fullAddress, bio, favSong, favPet;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        loadData();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             firstName = firstNameEditText.getText().toString().trim();
             lastName = lastNameEditText.getText().toString().trim();
             fullAddress = fullAddressEditText.getText().toString().trim();
             bio = bioEditText.getText().toString().trim();
             favSong = favSongEditText.getText().toString().trim();
             favPet = favPetEditText.getText().toString().trim();

            if (firstName.isEmpty()){ firstName = "null"; }
            if (lastName.isEmpty()){ lastName = "null"; }
            if (fullAddress.isEmpty()){ fullAddress = "null"; }
            if (bio.isEmpty()){ bio = "null"; }
            if (favSong.isEmpty()){ favSong = "null"; }
            if (favPet.isEmpty()){ favPet = "null"; }

            pDialog.setMessage("Updating...");
            showDialog();

            StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_UPDATE_INFO, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("OnResponse", "Update Response: " + response);
                    hideDialog();
                    if (response.contains("false")){
                        new DialogShow(getContext(),"Successful","Info Update Successfully",getResources().getDrawable(R.drawable.ic_done_all_black_24dp));
                        User.setFirstName(firstName);
                        User.setLastName(lastName);
                        User.setFullAddress(fullAddress);
                        User.setBio(bio);
                        User.setFavMusic(favSong);
                        User.setFavPet(favPet);
                    }
                    else{
                        new DialogShow(getContext(),"Error?",response,getResources().getDrawable(R.drawable.error_icon));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("OnErrorResponse", "Registration Error: " + error.getMessage());
                    //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    new DialogShow(getContext(),"Error?",error.getMessage()+"Server Problem!!!\n Please Try Again",getResources().getDrawable(R.drawable.error_icon));
                    hideDialog();
                }
            }){

                @Override
                protected Map<String, String> getParams() {
                    // Posting params to register url
                    Map<String, String> params = new HashMap<>();
                    params.put("FirstName", firstName );
                    params.put("LastName", lastName );
                    params.put("FullAddress", fullAddress );
                    params.put("bio", bio);
                    params.put("FavPet", favPet );
                    params.put("FavMusic", favSong );
                    params.put("id", User.getId());

                    return params;
                }
            };
            Volley.newRequestQueue(getContext()).add(request);


        }
    };

    private void loadData() {

        if (User.getImageLink()!="null"){
            String picUrl = "http://app.clynpro.com/image/";
            new DownLoadImageTask(proPic).execute(picUrl +User.getImageLink());
        }
        if (User.getFirstName()!="null"){
            firstNameEditText.setText(User.getFirstName());
        }
        if (User.getLastName()!="null"){
            lastNameEditText.setText(User.getLastName());
        }
        if (User.getBio()!="null"){
            bioEditText.setText(User.getBio());
        }
        if (User.getFullAddress()!="null"){
            fullAddressEditText.setText(User.getFullAddress());
            addressTextView.setText(User.getFullAddress());
        }
        if(User.getFavMusic()!= "null"){
            favSongEditText.setText(User.getFavMusic());
        }
        if (User.getFavPet()!="null"){
            favPetEditText.setText(User.getFavPet());
        }

        joinTextView.setText(User.getCreatedOn());

        if (User.getFirstName()!="null" && User.getLastName()!="null"){
            nameTextView.setText(User.getFirstName()+" "+User.getLastName());
        }else if (User.getFirstName()!="null"){
            nameTextView.setText(User.getFirstName());
        }else if (User.getLastName()!="null"){
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
