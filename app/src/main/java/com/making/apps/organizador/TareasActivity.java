package com.making.apps.organizador;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.making.apps.organizador.adapters.TareasRecyclerViewAdapter;
import com.making.apps.organizador.pojos.tareas;

import java.util.ArrayList;
import java.util.List;

public class TareasActivity extends AppCompatActivity {

    private RecyclerView reyclerViewTareas;
    private TareasRecyclerViewAdapter TareasRecyclerViewAdapter;
    private List<tareas> tareasList;
    int id_usuario;
    private BD baseDatos;
    boolean layoutTareas;
    private LinearLayoutManager tareasLinearLayout;
    private StaggeredGridLayoutManager tareasStaggeredGridLayoutManager;


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
        tareasLinearLayout = new LinearLayoutManager(this);
        tareasStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        reyclerViewTareas.setLayoutManager(tareasStaggeredGridLayoutManager);
        layoutTareas = true;

        try {
            tareasList = baseDatos.leerTareasUsuario(id_usuario);
        } catch (Exception e) {
            Log.e("e", e.toString());
        }
        if (tareasList != null) {
            TareasRecyclerViewAdapter = new TareasRecyclerViewAdapter(tareasList, TareasActivity.this, reyclerViewTareas);//se traen las tareas del usuario.
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
                //se elimina el share preferences para cerrar sesion local
                SplashActivity.borrarPreferences(TareasActivity.this);
                break;

            case R.id.action_cambiar_vista:
                ///permite camiar e layout
                if (layoutTareas) {
                    reyclerViewTareas.setLayoutManager(tareasLinearLayout);
                    layoutTareas = false;
                } else {
                    reyclerViewTareas.setLayoutManager(tareasStaggeredGridLayoutManager);
                    layoutTareas = true;

                }
                break;

            case R.id.action_buscar:
                return true;


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
        inflater.inflate(R.menu.menu_tareas, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_buscar).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.action_buscar));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String texto) {
                List<tareas> listaFiltrada = new ArrayList<>();
                for (tareas row : tareasList) {
                    // busca por nombre de la tarea o estado de la misma
                    if (row.getNombre().toLowerCase().contains(texto.toLowerCase()) || row.getEstado().contains(texto)) {
                        listaFiltrada.add(row);
                    }
                }
                TareasRecyclerViewAdapter = (com.making.apps.organizador.adapters.TareasRecyclerViewAdapter) reyclerViewTareas.getAdapter();
                TareasRecyclerViewAdapter.getFilter().filter(texto.toString());

                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);

    }

    /**
     * Metodo que genera un cuado de dialogo para el registro del usuario
     */
    private void alerDialogRegistroTarea() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_insertar_editar_tarea, null);
        dialogBuilder.setView(dialogView);

        final TextInputEditText titulo = dialogView.findViewById(R.id.EditTexTituloTarea);
        final TextInputEditText descripcion = dialogView.findViewById(R.id.EditTexDescripcionTarea);
        final Spinner spinnerEstado = dialogView.findViewById(R.id.spinnerEstado);

        Button boton_guardar = dialogView.findViewById(R.id.buttonGuardar);
        Button boton_cancelar = dialogView.findViewById(R.id.buttonCancelar);

        final AlertDialog alertDialog = dialogBuilder.create();

        baseDatos = new BD(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.EstadosTareas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);


        boton_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulor = String.valueOf(titulo.getText());
                String descripcionr = String.valueOf(descripcion.getText());
                int id_tarea;
                if (!titulor.isEmpty() && !descripcionr.isEmpty()) {
                    try {
                        //valida si usuario existe en BD

                        //se inserta tarea
                        baseDatos.insertarTareas(titulor, descripcionr, spinnerEstado.getSelectedItem().toString(), id_usuario);
                        id_tarea = baseDatos.obtenermaxIdTareas();
                        tareas nuevaTarea = new tareas();
                        if (id_tarea > -1) nuevaTarea.setId(id_tarea);
                        nuevaTarea.setNombre(titulor);
                        nuevaTarea.setDescripcion(descripcionr);
                        nuevaTarea.setEstado(spinnerEstado.getSelectedItem().toString());
                        nuevaTarea.setId_usuario(id_usuario);

                        //se agrega nueva tarea a la lista y se notifica el cambio en el adapter
                        tareasList.add(nuevaTarea);
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

    /**
     * Metodo para editar las tareas, crea cuadro de dialogo.
     *
     * @param tareasListAdapter  el adapter actual que contiene las tareas
     * @param indexAdpter        el indice de la tarea en el adapter
     * @param tarea              la tarea que se quiere editar
     * @param activity           el contexto
     * @param recyclerViewTareas la lista visual en donde se van a actualizar las tareas
     */
    public void alerDialogEditarTarea(final List<tareas> tareasListAdapter, final int indexAdpter, final tareas tarea, final Activity activity, final RecyclerView recyclerViewTareas) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_insertar_editar_tarea, null);
        dialogBuilder.setView(dialogView);
        final TextView tituloventana = dialogView.findViewById(R.id.TextViewTituloVentana);
        final TextInputEditText titulo = dialogView.findViewById(R.id.EditTexTituloTarea);
        final TextInputEditText descripcion = dialogView.findViewById(R.id.EditTexDescripcionTarea);
        final Spinner spinnerEstado = dialogView.findViewById(R.id.spinnerEstado);

        Button boton_guardar = dialogView.findViewById(R.id.buttonGuardar);
        Button boton_eliminar = dialogView.findViewById(R.id.buttonCancelar);

        tituloventana.setText(R.string.texto_editar_tarea);
        titulo.setText(tarea.getNombre());
        descripcion.setText(tarea.getDescripcion());
        boton_eliminar.setText(R.string.texo_eliminar);

        final AlertDialog alertDialog = dialogBuilder.create();

        baseDatos = new BD(activity);
        //se llena el spinner de estados y se selecciona el que tiene la tarea
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity, R.array.EstadosTareas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);
        spinnerEstado.setSelection(adapter.getPosition("" + tarea.getEstado()));


        boton_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulor = String.valueOf(titulo.getText());
                String descripcionr = String.valueOf(descripcion.getText());
                String estador = String.valueOf(spinnerEstado.getSelectedItem().toString());

                if (!titulor.isEmpty() && !descripcionr.isEmpty()) {


                    //se actualiza tarea
                    baseDatos.actualizarTareas(tarea.getId(), titulor, descripcionr, estador, id_usuario);
                    tarea.setNombre(titulor);
                    tarea.setDescripcion(descripcionr);
                    tarea.setEstado(estador);

                    //se notifica el cambio a la lista
                    tareasListAdapter.set(indexAdpter, tarea);
                    //se obtiene el adapter actual
                    TareasRecyclerViewAdapter = (com.making.apps.organizador.adapters.TareasRecyclerViewAdapter) recyclerViewTareas.getAdapter();
                    //se notifica el cambio del item
                    TareasRecyclerViewAdapter.notifyItemChanged(indexAdpter, tarea);
                    //se notifica el cambio global
                    TareasRecyclerViewAdapter.notifyDataSetChanged();


                    alertDialog.dismiss();
                    baseDatos = null;
                    Toast.makeText(view.getContext(), R.string.text_tarea_actualizada, Toast.LENGTH_SHORT).show();


                } else {

                    Toast.makeText(view.getContext(), R.string.text_complete_todo, Toast.LENGTH_SHORT).show();
                }
            }
        });


        boton_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
                builder.setMessage(activity.getString(R.string.texto_pregunta_tarea)).setPositiveButton(activity.getString(R.string.TextoAceptar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        baseDatos = new BD(activity);
                        baseDatos.eliminarTareas(tarea.getId(), id_usuario);

                        //se notifica el cambio a la lista
                        tareasListAdapter.remove(indexAdpter);
                        //se obtiene el adapter actual
                        TareasRecyclerViewAdapter = (com.making.apps.organizador.adapters.TareasRecyclerViewAdapter) recyclerViewTareas.getAdapter();
                        //se notifica el cambio del item
                        TareasRecyclerViewAdapter.notifyItemRemoved(indexAdpter);
                        //se notifica el cambio global
                        TareasRecyclerViewAdapter.notifyDataSetChanged();


                        dialogInterface.dismiss();
                    }
                })
                        .setNegativeButton(activity.getString(R.string.texto_cancelar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();


                alertDialog.dismiss();
                baseDatos = null;

            }
        });


        alertDialog.show();


    }

}
