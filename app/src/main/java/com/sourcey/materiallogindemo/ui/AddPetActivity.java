package com.sourcey.materiallogindemo.ui;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.Toast;
import android.net.Uri;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sourcey.materiallogindemo.R;
import com.sourcey.materiallogindemo.Utils.GeneralConstants;
import com.sourcey.materiallogindemo.Utils.FileHelper;
import com.sourcey.materiallogindemo.Utils.ParseConstants;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.Bind;

public class AddPetActivity extends AppCompatActivity {

    @Bind(R.id.photo_pet_profile)
    ImageView pet_image;
    @Bind(R.id.get_image_pet)
    ImageButton get_picture;
    @Bind(R.id.input_nombre)
    EditText pet_name;
    @Bind(R.id.input_raza)
    EditText pet_race;
    @Bind(R.id.input_color)
    EditText pet_color;
    @Bind(R.id.input_actividades)
    EditText pet_activities;
    @Bind(R.id.btn_guardar)
    Button btn_save_info;

    public static final int PICK_PHOTO_REQUEST = 2;
    private ParseFile file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);
        ButterKnife.bind(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        get_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent select_photo = new Intent(Intent.ACTION_GET_CONTENT);
                select_photo.setType(GeneralConstants.KEY_IMG_PATH);
                startActivityForResult(select_photo, PICK_PHOTO_REQUEST);
            }
        });

        btn_save_info.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String name = pet_name.getText().toString();
                                String race = pet_race.getText().toString();
                                String color = pet_color.getText().toString();
                                String activities = pet_activities.getText().toString();

                                final ParseObject newPet = new ParseObject(ParseConstants.KEY_PET);

                                newPet.put(ParseConstants.KEY_ANIMAL_NAME, name);
                                newPet.put(ParseConstants.KEY_ANIMAL_RACE, race);
                                newPet.put(ParseConstants.KEY_ANIMAL_COLOR, color);
                                newPet.put(ParseConstants.KEY_ANIMAL_DESCRIPTION, activities);

                                if(file != null){
                                    newPet.put(ParseConstants.KEY_ANIMAL_IMAGE, file);
                                }

                                newPet.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {

                                        if(e == null){
                                            Toast.makeText(getApplicationContext(), "Tu mascota ha sido registrada", Toast.LENGTH_SHORT).show();

                                            ParseUser current = ParseUser.getCurrentUser();
                                            ParseRelation<ParseObject> petRelation = current.getRelation(ParseConstants.KEY_ANIMAL_RELATION);
                                            petRelation.add(newPet);
                                            current.saveInBackground();

                                            finish();
                                        }else {
                                            Log.d("MASCOTA", e.getMessage());
                                        }
                                    }
                                });
                            }
                        });
                    }
                }).start();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PICK_PHOTO_REQUEST){
                if(data == null){
                    Toast.makeText(this, "Se a presentado un error al obtener la imagen.", Toast.LENGTH_SHORT).show();
                }else{
                    Uri data_uri = data.getData();
                    byte[] fileBytes = FileHelper.getByteArrayFromFile(this, data_uri);
                    fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                    String fileName = FileHelper.getFileName(this, data_uri, GeneralConstants.KEY_IMG);
                    file = new ParseFile(fileName, fileBytes);
                    file.saveInBackground();
                    Picasso.with(this).load(data_uri).fit().centerCrop().tag(this).into(pet_image);
                }
            }
        }
    }
}
