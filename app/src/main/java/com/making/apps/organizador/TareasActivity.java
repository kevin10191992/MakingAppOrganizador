package com.making.apps.organizador;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.making.apps.organizador.adapters.TareasRecyclerViewAdapter;
import com.making.apps.organizador.pojos.tareas;

import java.util.List;

public class TareasActivity extends AppCompatActivity {

    private RecyclerView reyclerViewTareas;
    private TareasRecyclerViewAdapter TareasRecyclerViewAdapter;
    private List<tareas> tareasList;
    int id_usuario;
    private BD baseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        reyclerViewTareas = findViewById(R.id.RecyclerViewTareas);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerDialogRegistroTarea();
            }
        });

        SharedPreferences getPrefs = getApplicationContext().getSharedPreferences(getString(R.string.shared_sesion), Context.MODE_PRIVATE);
        if (getPrefs != null) {
            id_usuario = getPrefs.getInt(getString(R.string.shared_id_usuario), -1);
        }

        baseDatos = new BD(this);

        reyclerViewTareas.setHasFixedSize(true);
        reyclerViewTareas.setLayoutManager(new LinearLayoutManager(this));

        try {
            tareasList = baseDatos.leerTareasUsuario(id_usuario);
        } catch (Exception e) {
            Log.e("e", e.toString());
        }
        if (tareasList != null) {
            TareasRecyclerViewAdapter = new TareasRecyclerViewAdapter(tareasList);//se traen las tareas del usuario.
            reyclerViewTareas.setAdapter(TareasRecyclerViewAdapter);

            reyclerViewTareas.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        // Hiding FAB

                        // 3. Property Animation - using animate()
                        //    chain method
                        fab.animate()
                                .scaleX(0)
                                .scaleY(0)
                                .setDuration(200)
                                .setInterpolator(new LinearInterpolator())
                                .start();

                    } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // Showing FAB

                        // 3. Property Animation - using animate()
                        //    chain method
                        fab.animate()
                                .scaleX(1)
                                .scaleY(1)
                                .setDuration(400)
                                .setInterpolator(new OvershootInterpolator())
                                .start();

                    }
                }
            });

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_cerrar_sesion:
                // TODO: 7/07/2018  poner aquii el cierre de sesion 
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);
        return super.onCreateOptionsMenu(menu);

    }

    /**
     * Metodo que genera un cuado de dialogo para el registro del usuario
     */
    private void alerDialogRegistroTarea() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_insertar_tarea, null);
        dialogBuilder.setView(dialogView);

        final TextInputEditText titulo = dialogView.findViewById(R.id.EditTexTituloTarea);
        final TextInputEditText descripcion = dialogView.findViewById(R.id.EditTexTituloDescripcion);

        Button boton_guardar = dialogView.findViewById(R.id.buttonGuardar);
        Button boton_cancelar = dialogView.findViewById(R.id.buttonCancelar);

        final AlertDialog alertDialog = dialogBuilder.create();

        baseDatos = new BD(this);

        boton_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulor = String.valueOf(titulo.getText());
                String descripcionr = String.valueOf(descripcion.getText());
                int id_tarea;
                if (!titulor.isEmpty() && !descripcionr.isEmpty()) {
                    try {
                        //valida si usuario existe en BD

                        //se inserta usuario con clave generada
                        baseDatos.insertarTareas(titulor, descripcionr, getString(R.string.t_estado_en_espera), id_usuario);
                        id_tarea = baseDatos.obtenermaxIdTareas();
                        tareas nuevaTarea = new tareas();
                        nuevaTarea.setNombre(titulor);
                        nuevaTarea.setDescripcion(descripcionr);
                        nuevaTarea.setEstado(getString(R.string.t_estado_en_espera));
                        nuevaTarea.setId_usuario(id_usuario);
                        Log.e("e", "idtareanuevo " + id_tarea);

                        if (id_tarea > -1) {
                            nuevaTarea.setId(id_tarea);
                        }


                        tareasList.add(nuevaTarea);
                        TareasRecyclerViewAdapter.notifyItemInserted((tareasList.size() - 1));
                        TareasRecyclerViewAdapter.notifyDataSetChanged();

                        alertDialog.dismiss();
                        baseDatos = null;
                        Toast.makeText(view.getContext(), R.string.text_tarea_creada, Toast.LENGTH_SHORT).show();


                    } catch (Exception e) {
                        Log.e("error", e.toString());
                    }
                } else {

                    Toast.makeText(view.getContext(), R.string.text_complete_todo, Toast.LENGTH_SHORT).show();
                }
            }
        });


        boton_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                baseDatos = null;
            }
        });


        alertDialog.show();


    }


}
