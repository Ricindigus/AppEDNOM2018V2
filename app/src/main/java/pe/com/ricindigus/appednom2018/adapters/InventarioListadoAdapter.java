package pe.com.ricindigus.appednom2018.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import pe.com.ricindigus.appednom2018.R;
import pe.com.ricindigus.appednom2018.modelo.Ficha;
import pe.com.ricindigus.appednom2018.modelo.Listado;

public class InventarioListadoAdapter extends RecyclerView.Adapter<InventarioListadoAdapter.ViewHolder>{
    ArrayList<Listado> listados;
    Context context;

    public InventarioListadoAdapter(ArrayList<Listado> listados, Context context) {
        this.listados = listados;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listado,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Listado listado = listados.get(position);
        holder.txtAula.setText(listado.getAula());
        holder.txtNroPostulantes.setText(listado.getNro_postulantes()+"");
        holder.txtCodigo.setText(listado.getCodigo_pagina());
        holder.txtFecha.setText(checkDigito(listado.getDia()) + "-"
                + checkDigito(listado.getMes()) + "-" + checkDigito(listado.getAnio()) + " "
                + checkDigito(listado.getHora()) + ":" + checkDigito(listado.getMinuto()));

//        if(asistenciaLocal.getSubidoEntrada() == 1){
//            holder.cv.setCardBackgroundColor(Color.WHITE);
//        }else{
//            holder.cv.setCardBackgroundColor(Color.rgb(227,242,253));
//        }
    }

    public String checkDigito (int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    @Override
    public int getItemCount() {
        return listados.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtAula;
        TextView txtNroPostulantes;
        TextView txtCodigo;
        TextView txtFecha;
        CardView cv;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.item_listado_cv);
            txtAula = itemView.findViewById(R.id.item_listado_txtAula);
            txtNroPostulantes = itemView.findViewById(R.id.item_listado_txtNroPostulantes);
            txtCodigo = itemView.findViewById(R.id.item_listado_txtCodigo);
            txtFecha = itemView.findViewById(R.id.item_listado_txtFecha);
        }
    }
}
