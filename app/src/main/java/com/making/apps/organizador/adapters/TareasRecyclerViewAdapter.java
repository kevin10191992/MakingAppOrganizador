package com.making.apps.organizador.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.making.apps.organizador.R;
import com.making.apps.organizador.TareasActivity;
import com.making.apps.organizador.pojos.tareas;

import java.util.List;

public class TareasRecyclerViewAdapter extends RecyclerView.Adapter<TareasRecyclerViewAdapter.TareasViewHolder> {

    private List<tareas> tareasList;
    private TareasActivity activity;
    private RecyclerView recyclerViewTareas;

    public TareasRecyclerViewAdapter(List<tareas> tareasList, TareasActivity activity, RecyclerView recyclerViewTareas) {
        this.tareasList = tareasList;
        this.activity = activity;
        this.recyclerViewTareas = recyclerViewTareas;
    }

    @NonNull
    @Override
    public TareasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_tareas, viewGroup, false);
        return new TareasViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TareasViewHolder tareasViewHolder, final int i) {
        tareasViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.alerDialogEditarTarea(tareasList, i, tareasList.get(i), activity, recyclerViewTareas);
            }
        });

        tareasViewHolder.nombre.setText(tareasList.get(i).getNombre());
        tareasViewHolder.descripcion.setText(tareasList.get(i).getDescripcion());
        tareasViewHolder.estado.setText(tareasList.get(i).getEstado());


    }

    @Override
    public int getItemCount() {
        return tareasList.size();
    }


    class TareasViewHolder extends RecyclerView.ViewHolder {
        private TextView nombre;
        private TextView descripcion;
        private TextView estado;

        TareasViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.TextViewTituloVentana);
            descripcion = itemView.findViewById(R.id.EditTexTituloTarea);
            estado = itemView.findViewById(R.id.textViewTareaEstado);


        }
    }

}
