package pe.com.ricindigus.appednom2018.fragments.reportes.listados_de_registros;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;

import pe.com.ricindigus.appednom2018.R;
import pe.com.ricindigus.appednom2018.adapters.AsistenciaAulaAdapter;
import pe.com.ricindigus.appednom2018.modelo.AsistenciaReg;
import pe.com.ricindigus.appednom2018.modelo.Data;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListAsisAulaFragment extends Fragment {
    Context context;
    int nroLocal;
    Spinner spAulas;
    RecyclerView recyclerView;
    String usuario;
    ArrayList<AsistenciaReg> asistenciaAulas;
    ArrayList<AsistenciaReg> noEnviados;
    Data data;
    FloatingActionButton fabUpLoad;
    AsistenciaAulaAdapter asistenciaAulaAdapter;
    boolean b = false;

    TextView txtTotal;
    TextView txtSinRegistro;
    TextView txtRegistrados;
    TextView txtTransferidos;

    public ListAsisAulaFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ListAsisAulaFragment(Context context, int nroLocal, String usuario) {
        this.context = context;
        this.nroLocal = nroLocal;
        this.usuario = usuario;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list_asis_aula, container, false);
        spAulas = (Spinner) rootView.findViewById(R.id.asistencia_aula_spAula);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.listado_recycler);
        fabUpLoad = (FloatingActionButton) rootView.findViewById(R.id.listado_btnUpload);
        txtTotal = (TextView) rootView.findViewById(R.id.lista_txtTotales);
        txtSinRegistro = (TextView) rootView.findViewById(R.id.lista_txtSinRegistro);
        txtRegistrados = (TextView) rootView.findViewById(R.id.lista_txtRegistrados);
        txtTransferidos = (TextView) rootView.findViewById(R.id.lista_txtTransferidos);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Data d =  new Data(context);
        d.open();
        ArrayList<String> aulas =  d.getArrayAulasListado(nroLocal);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, aulas);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAulas.setAdapter(dataAdapter);
        d.close();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        cargaData();
        asistenciaAulaAdapter = new AsistenciaAulaAdapter(asistenciaAulas,context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(asistenciaAulaAdapter);

        spAulas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargaData();
                asistenciaAulaAdapter = new AsistenciaAulaAdapter(asistenciaAulas,context);
                recyclerView.setAdapter(asistenciaAulaAdapter);
                Data d = new Data(context);
                d.open();
                String aula = spAulas.getSelectedItem().toString();
                int nroAula = d.getNumeroAula(aula,nroLocal);
                txtTotal.setText("Total: " + asistenciaAulas.size());
                txtSinRegistro.setText("Sin Registro: " + d.getNroAsistenciasAulaSinRegistro(nroLocal,nroAula));
                txtRegistrados.setText("Registrados: " + d.getNroAsistenciasAulaLeidas(nroLocal,nroAula));
                txtTransferidos.setText("Transferidos: " + d.getNroAsistenciasAulaTransferidos(nroLocal,nroAula));
                d.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fabUpLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b = false;
                noEnviados = new ArrayList<>();
                data = new Data(context);
                data.open();
                int seleccion = spAulas.getSelectedItemPosition();
                String aula = spAulas.getSelectedItem().toString();
                int nroAula = 0;
                if(seleccion > 0) nroAula = data.getNumeroAula(aula,nroLocal);
                noEnviados = data.getAsistenciasAulaSinEnviar(nroLocal,nroAula);
                data.close();
                if(noEnviados.size() > 0){
                    final int total = noEnviados.size();
                    int i = 0;
                    for (final AsistenciaReg asistenciaAula : noEnviados){
                        i++;
                        final int j = i;
                        final String c = asistenciaAula.getDni();
                        WriteBatch batch = FirebaseFirestore.getInstance().batch();
                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("asistencia").document(asistenciaAula.getDni());
                        batch.update(documentReference, "check_registro", 1);
                        batch.update(documentReference, "fecha_transferencia", FieldValue.serverTimestamp());
                        batch.update(documentReference, "usuario_registro", usuario);
                        batch.update(documentReference, "fecha_registro",
                                new Timestamp(new Date(asistenciaAula.getAnio_aula()-1900,asistenciaAula.getMes_aula()-1,asistenciaAula.getDia_aula(),
                                        asistenciaAula.getHora_aula(),asistenciaAula.getMin_aula(),asistenciaAula.getSeg_aula())));
                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Data data = new Data(context);
                                data.open();
                                data.actualizarAsistenciaRegAulaSubido(c);
                                data.close();
                                if (j == total) {
                                    Toast.makeText(context, total + " registros subidos", Toast.LENGTH_SHORT).show();
                                    cargaData();
                                    asistenciaAulaAdapter = new AsistenciaAulaAdapter(asistenciaAulas,context);
                                    recyclerView.setAdapter(asistenciaAulaAdapter);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "NO GUARDO", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else{
                    Toast.makeText(context, "No hay registros nuevos para subir", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public void cargaData(){
        asistenciaAulas = new ArrayList<AsistenciaReg>();
        Data d = new Data(context);
        d.open();
        String aula = spAulas.getSelectedItem().toString();
        int nroAula = d.getNumeroAula(aula,nroLocal);
        asistenciaAulas = d.getListadoAsistenciaAula(nroLocal,nroAula);
        txtTotal.setText("Total: " + asistenciaAulas.size());
        txtSinRegistro.setText("Sin Registro: " + d.getNroAsistenciasAulaSinRegistro(nroLocal,nroAula));
        txtRegistrados.setText("Registrados: " + d.getNroAsistenciasAulaLeidas(nroLocal,nroAula));
        txtTransferidos.setText("Transferidos: " + d.getNroAsistenciasAulaTransferidos(nroLocal,nroAula));
        d.close();
    }
    public String checkDigito (int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
}
