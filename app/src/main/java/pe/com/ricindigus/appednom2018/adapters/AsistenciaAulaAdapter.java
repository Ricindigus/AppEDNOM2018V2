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
import pe.com.ricindigus.appednom2018.modelo.AsistenciaAula;
import pe.com.ricindigus.appednom2018.modelo.AsistenciaLocal;

public class AsistenciaAulaAdapter extends RecyclerView.Adapter<AsistenciaAulaAdapter.ViewHolder>{
    ArrayList<AsistenciaAula> asistenciaAulas;
    Context context;

    public AsistenciaAulaAdapter(ArrayList<AsistenciaAula> asistenciaAulas, Context context) {
        this.asistenciaAulas = asistenciaAulas;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_asistencia,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AsistenciaAula asistenciaAula = asistenciaAulas.get(position);
        holder.txtDni.setText(asistenciaAula.getDni());
        holder.txtNombres.setText(asistenciaAula.getNombres() + " " + asistenciaAula.getApepat() + " " + asistenciaAula.getApemat());
        holder.txtAula.setText(asistenciaAula.getAula());
        holder.txtFecha.setText(checkDigito(asistenciaAula.getAula_dia()) + "-"
                + checkDigito(asistenciaAula.getAula_mes()) + "-" + checkDigito(asistenciaAula.getAula_anio()) + " "
                + checkDigito(asistenciaAula.getAula_hora()) + ":" + checkDigito(asistenciaAula.getAula_minuto()));

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
        return asistenciaAulas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtDni;
        TextView txtNombres;
        TextView txtAula;
        TextView txtFecha;
        CardView cv;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.item_asistencia_cv);
            txtDni = itemView.findViewById(R.id.item_asistencia_txtDni);
            txtNombres = itemView.findViewById(R.id.item_asistencia_txtNombres);
            txtAula = itemView.findViewById(R.id.item_asistencia_txtAula);
            txtFecha = itemView.findViewById(R.id.item_asistencia_txtFecha);
        }
    }
}