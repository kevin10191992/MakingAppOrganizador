package com.making.apps.organizador.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.making.apps.organizador.R;
import com.making.apps.organizador.pojos.tareas;

import java.util.List;

public class TareasRecyclerViewAdapter extends RecyclerView.Adapter<TareasRecyclerViewAdapter.TareasViewHolder> {

    private List<tareas> tareasList;

    public TareasRecyclerViewAdapter(List<tareas> tareasList) {
        this.tareasList = tareasList;
    }

    @NonNull
    @Override
    public TareasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_tareas, viewGroup, false);
        return new TareasViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull TareasViewHolder tareasViewHolder, int i) {
        tareasViewHolder.nombre.setText(tareasList.get(i).getNombre());
        tareasViewHolder.descripcion.setText(tareasList.get(i).getDescripcion());
        tareasViewHolder.estado.setText(tareasList.get(i).getEstado());
        tareasViewHolder.card_view_tareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), R.string.text_complete_todo, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return tareasList.size();
    }


    class TareasViewHolder extends RecyclerView.ViewHolder {

        private TextView nombre;
        private TextView descripcion;
        private TextView estado;
        private CardView card_view_tareas;

        TareasViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.TextViewTituloTarea);
            descripcion = itemView.findViewById(R.id.EditTexDescripcionTarea);
            estado = itemView.findViewById(R.id.textViewTareaEstado);
            card_view_tareas = itemView.findViewById(R.id.card_view_tareas);
        }
    }

}
