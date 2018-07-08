package com.making.apps.organizador;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.making.apps.organizador.pojos.usuario;


/**
 * Clase principal que contiene la vista login y la de registro
 */
public class MainActivity extends AppCompatActivity {

    private TextInputEditText usuario;
    private TextInputEditText clave;
    private BD baseDatos;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuario = findViewById(R.id.input_usuario);
        clave = findViewById(R.id.input_clave);

        Button login = findViewById(R.id.boton_login);
        Button registrarse = findViewById(R.id.boton_registro);
        

        baseDatos = new BD(this);

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerDialogRegistro();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginLocal();
            }
        });


        callbackManager = CallbackManager.Factory.create();


        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");


        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                final int id_usuario = Integer.parseInt(accessToken.getUserId().substring(0, 10));
                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        if (currentProfile != null) {
                            String usuariobd = currentProfile.getName();
                            SplashActivity.guardarPreferences(MainActivity.this, id_usuario, usuariobd);
                            startActivity(new Intent(MainActivity.this, TareasActivity.class));
                            profileTracker.stopTracking();
                            MainActivity.this.finish();

                        }


                    }
                };
                profileTracker.startTracking();


            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e("d", exception.toString());
            }
        });


    }

    /**
     * Metodo que genera un cuado de dialogo para el registro del usuario
     */
    private void alerDialogRegistro() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_registro, null);
        dialogBuilder.setView(dialogView);

        final TextInputEditText usuarioreg = dialogView.findViewById(R.id.reg_usuario);
        final TextInputEditText clavereg = dialogView.findViewById(R.id.reg_clave);

        Button boton_registrarse = dialogView.findViewById(R.id.boton_registro);

        final AlertDialog alertDialog = dialogBuilder.create();

        boton_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuarior = usuarioreg.getText().toString();
                String claver = clavereg.getText().toString();
                if (!usuarior.isEmpty() && !claver.isEmpty()) {
                    if (claver.length() >= 8) {
                        try {
                            //valida si usuario existe en BD
                            if (!baseDatos.existeEnBd(usuarior)) {
                                //se inserta usuario con clave generada
                                baseDatos.insertarUsuario(usuarior, claver);
                                Toast.makeText(view.getContext(), R.string.text_usuario_creado, Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            } else {
                                Toast.makeText(view.getContext(), R.string.text_usuario_existe, Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            Log.e("error", e.toString());
                        }


                    } else {
                        Toast.makeText(view.getContext(), R.string.text_longitud_minima, Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(view.getContext(), R.string.text_complete_todo, Toast.LENGTH_SHORT).show();
                }
            }
        });


        alertDialog.show();


    }

    /**
     * Metodo que hace login con BD
     */
    private void loginLocal() {
        String usuariobd = String.valueOf(usuario.getText());
        String clavebd = String.valueOf(clave.getText());
        int id_usuario;

        if (!usuariobd.isEmpty() && !clavebd.isEmpty()) {
            try {
                //valida si usuario existe en BD
                if (baseDatos.existeEnBd(usuariobd)) {
                    id_usuario = baseDatos.loginBD(usuariobd, clavebd);
                    if (id_usuario > -1) {
                        usuario nuevo_user = new usuario();
                        nuevo_user.setId(id_usuario);
                        nuevo_user.setNombre(usuariobd);
                        nuevo_user.setClave(clavebd);

                        ///se guardan datos del usuario
                        SplashActivity.guardarPreferences(MainActivity.this, id_usuario, usuariobd);

                        startActivity(new Intent(this, TareasActivity.class));
                        this.finish();

                    } else {
                        Toast.makeText(getApplicationContext(), R.string.text_error_sesion, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.text_usuario_no_existe, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("error", e.toString());
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.text_complete_todo, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


}



