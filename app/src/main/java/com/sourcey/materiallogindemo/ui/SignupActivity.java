package com.sourcey.materiallogindemo.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.sourcey.materiallogindemo.DiggyDogApp;
import com.sourcey.materiallogindemo.R;
import com.sourcey.materiallogindemo.Utils.ParseConstants;

import butterknife.ButterKnife;
import butterknife.Bind;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name) EditText _nameText;
    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;
    @Bind(R.id.input_last_name) TextView _lastName;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        ParseUser user = ParseUser.getCurrentUser();
        if(user != null){
            Intent i = new Intent(SignupActivity.this, Home.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final String name = _nameText.getText().toString();
        final String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();
        final String lastName = _lastName.getText().toString();

        // TODO: Implement your own signup logic here.
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ParseUser newUser = new ParseUser();
                        newUser.put(ParseConstants.KEY_USER_NAME, name);
                        newUser.put(ParseConstants.KEY_USER_LASTNAME, lastName);
                        newUser.put(ParseConstants.KEY_USER_EMAIL, email);
                        newUser.setUsername(email);
                        newUser.setPassword(password);

                        newUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if(e == null){
                                    DiggyDogApp.updateParseInstallation(ParseUser.getCurrentUser());

                                    Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    onSignupSuccess();
                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                    builder.setMessage(e.getMessage())
                                            .setTitle(R.string.sign_up_error_title)
                                            .setPositiveButton(android.R.string.ok, null);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            }
                        });
                    }
                });
            }
        }).start();
    }

    public void showAlert(int titleId, String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(titleId);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(true);

        alertDialog.setPositiveButton(
                "Si",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialog.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = alertDialog.create();
        alert11.show();
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Error al crear cuenta.", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String lastName = _lastName.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("El campo nombre está vacio o es muy corto.");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (lastName.isEmpty() || lastName.length() < 3) {
            _lastName.setError("El campo apellido está vacio o es muy corto.");
            valid = false;
        } else {
            _lastName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Ingrese una dirección de correo valida.");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Debe tener entre 4 y 10 caracteres alfanuméricos.");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}