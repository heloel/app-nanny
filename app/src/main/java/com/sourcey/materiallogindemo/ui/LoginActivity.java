package com.sourcey.materiallogindemo.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.sourcey.materiallogindemo.DiggyDogApp;
import com.sourcey.materiallogindemo.R;

import com.facebook.CallbackManager;
import com.sourcey.materiallogindemo.Utils.GeneralConstants;
import com.sourcey.materiallogindemo.Utils.ParseConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    LoginButton btn_facebook_login;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _emailText = (EditText)findViewById(R.id.input_email);
        _passwordText = (EditText)findViewById(R.id.input_password);
        _loginButton = (Button)findViewById(R.id.btn_login);
        _signupLink = (TextView)findViewById(R.id.link_signup);
        btn_facebook_login = (LoginButton)findViewById(R.id.login_button);

        ParseUser user = ParseUser.getCurrentUser();
        if(user != null){
            Intent i = new Intent(LoginActivity.this, Home.class);

        }

        final List<String> permissions = Arrays.asList("public_profile", "email");

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });


        btn_facebook_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user == null) {
                            String message = getString(R.string.facebook_cancel);
                            errorSignUp(message);
                        } else if (user.isNew()) {
                            makeMeRequest();
                        } else {
                            acceptUser();
                        }
                    }
                });
            }
        });
    }


    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {

                if (user != null) {
                    DiggyDogApp.updateParseInstallation(user);

                    Intent i = new Intent(LoginActivity.this, Home.class);
                    startActivity(i);
                    onLoginSuccess();
                } else {
                    if (!isFinishing()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(e.getMessage())
                                .setTitle(R.string.sign_up_error_title)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        acceptUser();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Error al iniciar sesión.", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Ingrese una dirección de correo valida.");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Debe contener entre 4 y 10 caracteres alfanuméricos");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void makeMeRequest() {
        Toast.makeText(this, "Obteniendo informacion de facebook", Toast.LENGTH_SHORT).show();
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        boolean error = false;
                        String message = "";
                        String email = "";
                        if (jsonObject != null) {

                            ParseUser currentUser = ParseUser.getCurrentUser();

                            try {
                                String name[] = new String[2];
                                name = parseString(jsonObject.getString(ParseConstants.KEY_USER_NAME));
                                currentUser.put(ParseConstants.KEY_USER_NAME, name[0]);
                                currentUser.put(ParseConstants.KEY_USER_LASTNAME, name[1]);

                                if (jsonObject.getString(ParseConstants.KEY_USER_EMAIL) != null) {
                                    currentUser.put(ParseConstants.KEY_USER_EMAIL, jsonObject.getString(ParseConstants.KEY_USER_EMAIL));
                                    email = jsonObject.getString(ParseConstants.KEY_USER_EMAIL);
                                }
                                else {
                                    currentUser.put(ParseConstants.KEY_USER_EMAIL, name[0] + "@diggydog.com" );
                                    email = name[0] + "@diggydog.com";
                                }

                                JSONObject photo = jsonObject.getJSONObject(GeneralConstants.KEY_PHOTO_FACEBOOK).getJSONObject(GeneralConstants.KEY_PHOTO_DATA_FACEBOOK);

                                currentUser.put(ParseConstants.KEY_USER_PHOTO, photo.getString(GeneralConstants.KEY_PHOTO_URL_FACEBOOK));
                                currentUser.saveInBackground();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                acceptUser();
                                            }
                                        });
                                    }
                                }).start();

                            } catch (JSONException e) {
                                message = getString(R.string.error_parsing_facebook) + e;
                                error = true;
                            }
                        } else if (graphResponse.getError() != null) {
                            error = true;
                            switch (graphResponse.getError().getCategory()) {
                                case LOGIN_RECOVERABLE:
                                    message = getString(R.string.authentication_error) + graphResponse.getError();
                                    break;

                                case TRANSIENT:
                                    message = getString(R.string.try_again_facebook) + graphResponse.getError();
                                    break;

                                case OTHER:
                                    message = getString(R.string.other_facebook_error) + graphResponse.getError();
                                    break;
                            }
                        }
                        if (error) {
                            errorSignUp(message);
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,gender,name, picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private String[] parseString(String name) {
        String completeName[]= new String[2];
        String[] parts = name.split(" ");
        String lastname= "";
        if(parts.length > 2) {
            for(int i=1; i<parts.length; i++) {
                lastname += parts[i] + " ";
                completeName[1] = lastname;
            }
        }
        else {
            completeName[1] = parts[1];
        }
        completeName[0] = parts[0];

        return completeName;
    }

    private void acceptUser() {
        DiggyDogApp.updateParseInstallation(
                ParseUser.getCurrentUser()
        );
        Intent i = new Intent(LoginActivity.this, Home.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void errorSignUp(String e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(e)
                .setTitle(R.string.sign_up_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
