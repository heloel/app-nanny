package com.sourcey.materiallogindemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import com.sourcey.materiallogindemo.R;
import com.sourcey.materiallogindemo.Utils.FileHelper;
import com.sourcey.materiallogindemo.Utils.ParseConstants;
import com.sourcey.materiallogindemo.Utils.GeneralConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @Bind(R.id.upload_image_user)
    ImageButton upload_image_user;
    @Bind(R.id.photo_user_profile)
    ImageView user_photo_profile;
    @Bind(R.id.user_name)
    TextView user_name;
    @Bind(R.id.text_about_animal)
    TextView text_about_animal;
    @Bind(R.id.text_about_user)
    TextView text_about_user;

    @Bind(R.id.pet_name)
    TextView pet_name;
    @Bind(R.id.pet_race)
    TextView pet_race;
    @Bind(R.id.pet_color)
    TextView pet_color;

    private ParseRelation<ParseObject> petRelation;
    public static final int PICK_PHOTO_REQUEST = 2;
    private ParseFile file;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);
        getProfileInfo();

        upload_image_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent select_photo = new Intent(Intent.ACTION_GET_CONTENT);
                select_photo.setType(GeneralConstants.KEY_IMG_PATH);
                startActivityForResult(select_photo, PICK_PHOTO_REQUEST);
            }
        });

        return rootView;
    }

    private void getProfileInfo(){

        ParseUser currentUser = ParseUser.getCurrentUser();

        user_name.setText(currentUser.getString(ParseConstants.KEY_USER_NAME)  + " " + currentUser.getString(ParseConstants.KEY_USER_LASTNAME));

        if(currentUser.getParseFile(ParseConstants.KEY_USER_PHOTO) != null){
            String url_photo = currentUser.getParseFile(ParseConstants.KEY_USER_PHOTO).getUrl();
            Picasso.with(getActivity()).load(url_photo).fit().centerCrop().into(user_photo_profile);
        }

        String about_user = currentUser.getString(ParseConstants.KEY_ABOUT_USER);
        text_about_user.setText(about_user);
    }

    @Override
    public void onResume(){
        super.onResume();

        if(!GeneralConstants.checkNetwork(getActivity().getBaseContext()))
            GeneralConstants.showMessageConnection(getActivity().getBaseContext());
        else {
            ParseUser mUser = ParseUser.getCurrentUser();
            petRelation = mUser.getRelation(ParseConstants.KEY_ANIMAL_RELATION);
            getInfoPet();
        }
    }

    private void getInfoPet(){
        ParseQuery<ParseObject> query = petRelation.getQuery();
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject pet, ParseException e) {
                if (e == null) {
                    if (pet != null) {

                        pet_name.setText(pet.getString(ParseConstants.KEY_ANIMAL_NAME));
                        pet_race.setText(pet.getString(ParseConstants.KEY_ANIMAL_RACE));
                        pet_color.setText(pet.getString(ParseConstants.KEY_ANIMAL_COLOR));
                        text_about_animal.setText(pet.getString(ParseConstants.KEY_ANIMAL_DESCRIPTION));
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PICK_PHOTO_REQUEST){
                if(data == null){
                    Toast.makeText(getActivity().getBaseContext(), "Se a presentado un error al obtener la imagen.", Toast.LENGTH_SHORT).show();
                }else{
                    Uri data_uri = data.getData();
                    byte[] fileBytes = FileHelper.getByteArrayFromFile(getActivity().getBaseContext(), data_uri);
                    fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                    String fileName = FileHelper.getFileName(getActivity().getBaseContext(), data_uri, GeneralConstants.KEY_IMG);
                    file = new ParseFile(fileName, fileBytes);
                    file.saveInBackground();
                    Picasso.with(getActivity().getBaseContext()).load(data_uri).fit().centerCrop().tag(this).into(user_photo_profile);

                    ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.put(ParseConstants.KEY_USER_PHOTO, file);
                    currentUser.saveInBackground();
                }
            }
        }
    }
}
