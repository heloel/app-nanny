package com.sourcey.materiallogindemo.ui;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sourcey.materiallogindemo.R;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.sourcey.materiallogindemo.Utils.CircleTransform;
import com.sourcey.materiallogindemo.Utils.ParseConstants;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.Bind;


public class Account extends AppCompatActivity {

    @Bind(R.id.et_user_name)
    EditText name;
    @Bind(R.id.et_last_name)
    EditText apellido;
    @Bind(R.id.et_user_email)
    EditText email;
    @Bind(R.id.btn_save_info)
    Button btn_save;
    @Bind(R.id.user_photo_profile)
    ImageView user_photo;
    @Bind(R.id.about_me)
    EditText about_me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ParseUser user = ParseUser.getCurrentUser();

        name.setHint(user.getString(ParseConstants.KEY_USER_NAME));
        apellido.setHint(user.getString(ParseConstants.KEY_USER_LASTNAME));
        email.setHint(user.getEmail());
        about_me.setHint(user.getString(ParseConstants.KEY_ABOUT_USER));

        ParseFile photo = user.getParseFile(ParseConstants.KEY_USER_PHOTO);

        if(photo != null){
            String photo_url = photo.getUrl();
            Picasso.with(this).load(photo_url).transform(new CircleTransform()).fit().centerCrop().tag(this).into(user_photo);
        }

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = name.getText().toString();
                String user_last_name = apellido.getText().toString();
                String user_email = email.getText().toString();
                String about_user = about_me.getText().toString();

                user.put(ParseConstants.KEY_USER_NAME, user_name);
                user.put(ParseConstants.KEY_USER_LASTNAME, user_last_name);
                user.put(ParseConstants.KEY_USER_EMAIL, user_email);
                user.put(ParseConstants.KEY_ABOUT_USER, about_user);

                user.saveInBackground();

                Toast.makeText(getBaseContext(), "Información guardada con éxito", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
