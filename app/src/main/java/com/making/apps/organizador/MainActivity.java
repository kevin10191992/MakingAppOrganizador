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

import com.making.apps.organizador.pojos.usuario;


/**
 * Clase principal que contiene la vista login y la de registro
 */
public class MainActivity extends AppCompatActivity {

    private TextInputEditText usuario;
    private TextInputEditText clave;
    private Button login;
    private Button login_facebook;
    private Button registrarse;
    private BD baseDatos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuario = findViewById(R.id.input_usuario);
        clave = findViewById(R.id.input_clave);
        login = findViewById(R.id.boton_login);
        login_facebook = findViewById(R.id.boton_login_facebook);
        registrarse = findViewById(R.id.boton_registro);

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
                if (claver.length() >= 8) {
                    if (!usuarior.isEmpty() && !claver.isEmpty()) {
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
                        Toast.makeText(view.getContext(), R.string.text_complete_todo, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(view.getContext(), R.string.text_longitud_minima, Toast.LENGTH_SHORT).show();
                }
            }
        });


        alertDialog.show();


    }

    /**
     * Metodo que hace login con BD
     */
    private void loginLocal() {
        String usuariobd = usuario.getText().toString();
        String clavebd = clave.getText().toString();
        int id_usuario;

        if (!usuariobd.isEmpty() && !clavebd.isEmpty()) {
            try {
                //valida si usuario existe en BD
                if (baseDatos.existeEnBd(usuariobd)) {
                    id_usuario = baseDatos.loginBD(usuariobd, clavebd);
                    Log.e("en", "si existe " + id_usuario);
                    if (id_usuario > -1) {
                        com.making.apps.organizador.pojos.usuario nuevo_user = new usuario();
                        nuevo_user.setId(id_usuario);
                        nuevo_user.setNombre(usuariobd);
                        nuevo_user.setClave(clavebd);

                        startActivity(new Intent(this, TareasActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.text_error_sesion, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.text_usuario_no_existe, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("error", e.toString());
            }
        }

    }
}
