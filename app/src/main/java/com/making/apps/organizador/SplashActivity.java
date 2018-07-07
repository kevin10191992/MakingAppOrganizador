package com.making.apps.organizador;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        SharedPreferences getPrefs = getApplicationContext().getSharedPreferences(getString(R.string.shared_sesion), Context.MODE_PRIVATE);
        if (getPrefs != null) {
            int id_usuario = getPrefs.getInt(getString(R.string.shared_id_usuario), -1);
            if (id_usuario > -1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, TareasActivity.class));
                    }
                }, 1000);

            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                    }
                }, 1000);


            }

        }


    }

    /**
     * Se elimina el share preferences para cerrar sesion local
     *
     * @param activity actividad desde la que se quiere cerrar sesion
     */
    public static void borrarPreferences(final Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Are you sure?").setPositiveButton(activity.getString(R.string.TextoAceptar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SharedPreferences.Editor editor = activity.getSharedPreferences("sesion", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                editor.commit();

                activity.startActivity(new Intent(activity, SplashActivity.class));
                activity.finish();
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();


    }

    /**
     * Permite guardar las preferencias del usuario para el manejo de la sesion
     *
     * @param activity   actividad desde donde se inicia sesion
     * @param id_usuario id del usuario
     * @param usuariobd  nombre del usuario
     */
    public static void guardarPreferences(Activity activity, int id_usuario, String usuariobd) {
        SharedPreferences.Editor editor = activity.getSharedPreferences("sesion", Context.MODE_PRIVATE).edit();
        editor.putInt("id_usuario", id_usuario);
        editor.putString("nombre_usuario", usuariobd);
        editor.apply();
        editor.commit();
    }
}
