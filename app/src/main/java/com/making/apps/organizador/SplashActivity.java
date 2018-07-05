package com.making.apps.organizador;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        final SharedPreferences prefs = getSharedPreferences("sesion", Context.MODE_PRIVATE);

        if (prefs != null) {
            int id_usuario = prefs.getInt("id_usuario", -1);
            Log.e("s", "id usua" + id_usuario);
            if (id_usuario > -1) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, TareasActivity.class));
                    }
                }, 1000);

            } else {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
            }

        }


    }


}
