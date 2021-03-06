package pe.com.ricindigus.appednom2018.fragments.registro_control_inventario;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.Date;

import pe.com.ricindigus.appednom2018.R;
import pe.com.ricindigus.appednom2018.modelo.Data;
import pe.com.ricindigus.appednom2018.modelo.InventarioReg;
import pe.com.ricindigus.appednom2018.modelo.SQLConstantes;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvListAsisFragment extends Fragment {
    Spinner spAulas;
    EditText edtLista;
    ImageView btnBuscar;
    Context context;
    int nroLocal;
    String usuario;

    TextView correctoTxtCodLista;
    TextView correctoTxtNroPostulantes;

    TextView errorListaAulaTxtCodLista;
    TextView errorListaAulaTxtNroPostulantes;
    TextView errorListaAulaTxtAula;

    TextView yaRegistradoTxtCodLista;
    TextView yaRegistradoTxtNroPostulantes;
    TextView yaRegistradoTxtAula;

    TextView errorCodigoListaTxtCodLista;

    LinearLayout lytCorrecto;
    LinearLayout lytErrorListaAula;
    LinearLayout lytYaRegistrado;
    LinearLayout lytErrorLista;

    TextView txtRegistrados;

    public InvListAsisFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public InvListAsisFragment(int nroLocal, Context context, String usuario) {
        this.context = context;
        this.nroLocal = nroLocal;
        this.usuario = usuario;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_inv_list_asis, container, false);
        spAulas = (Spinner) rootView.findViewById(R.id.inventario_lista_spAula);
        edtLista = (EditText) rootView.findViewById(R.id.inventario_lista_edtCodigo);
        btnBuscar = (ImageView) rootView.findViewById(R.id.inventario_lista_btnBuscar);

        correctoTxtCodLista = (TextView) rootView.findViewById(R.id.lista_correcta_txtCodLista);
        correctoTxtNroPostulantes = (TextView) rootView.findViewById(R.id.lista_correcta_txtNroPostulantes);

        errorListaAulaTxtCodLista = (TextView) rootView.findViewById(R.id.error_lista_txtCodLista);
        errorListaAulaTxtAula = (TextView) rootView.findViewById(R.id.error_lista_txtAula);
        errorListaAulaTxtNroPostulantes = (TextView) rootView.findViewById(R.id.error_lista_txtNroPostulantes);

        yaRegistradoTxtCodLista = (TextView) rootView.findViewById(R.id.error_lista_yaregistrada_txtCodLista);
        yaRegistradoTxtNroPostulantes = (TextView) rootView.findViewById(R.id.error_lista_yaregistrada_txtNroPostulantes);
        yaRegistradoTxtAula = (TextView) rootView.findViewById(R.id.error_lista_yaregistrada_txtAula);


        errorCodigoListaTxtCodLista = (TextView) rootView.findViewById(R.id.error_codigo_lista_txtLista);

        lytCorrecto = (LinearLayout) rootView.findViewById(R.id.inventario_lista_lytCorrecto);
        lytErrorListaAula = (LinearLayout) rootView.findViewById(R.id.inventario_lista_lytErrorAula);
        lytYaRegistrado = (LinearLayout) rootView.findViewById(R.id.inventario_lista_lytYaRegistrado);
        lytErrorLista = (LinearLayout) rootView.findViewById(R.id.inventario_lista_lytErrorLista);

        txtRegistrados = (TextView) rootView.findViewById(R.id.inventario_lista_txtRegistrados);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (context != null){
            Data data =  new Data(context);
            data.open();
            ArrayList<String> aulas =  data.getArrayAulasRegistro(nroLocal);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, aulas);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spAulas.setAdapter(dataAdapter);
            String aula = spAulas.getSelectedItem().toString();
            int nroAula = data.getNumeroAula(aula,nroLocal);
            txtRegistrados.setText("Registrados: " + data.getNroListasRegistradas(nroLocal,nroAula)+"/"+data.getNroListasTotales(nroLocal,nroAula));
            data.close();
        }

        spAulas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Data data =  new Data(context);
                data.open();
                String aula = spAulas.getSelectedItem().toString();
                int nroAula = data.getNumeroAula(aula,nroLocal);
                txtRegistrados.setText("Registrados: " + data.getNroListasRegistradas(nroLocal,nroAula)+"/"+data.getNroListasTotales(nroLocal,nroAula));
                data.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBoton();
            }
        });
    }

    public void clickBoton(){
        ocultarTeclado(edtLista);
        String codigoInventario = edtLista.getText().toString();
        Data data = new Data(context);
        data.open();
        InventarioReg inventarioReg = data.getInventarioReg(codigoInventario,3);
        String aula = spAulas.getSelectedItem().toString();
        int nroAula = 0;
        nroAula = data.getNumeroAula(aula,nroLocal);
        if(inventarioReg == null){
            mostrarErrorCodigo(codigoInventario);
        }else{
            if(inventarioReg.getNaula() == nroAula){
                if(inventarioReg.getEstado() == 0) registrarInventario(inventarioReg);
                else mostrarYaRegistrado(inventarioReg.getCodigo(),inventarioReg.getNpostulantes(),inventarioReg.getNaula());
            }else{
                mostrarErrorAula(inventarioReg.getCodigo(),inventarioReg.getNpostulantes(), inventarioReg.getNaula());
            }
        }
        edtLista.setText("");
        edtLista.requestFocus();
        data.close();
    }

    public void ocultarTeclado(View view){
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void registrarInventario(InventarioReg inventarioReg){
        Data data = new Data(context);
        data.open();
        Calendar calendario = Calendar.getInstance();
        int yy = calendario.get(Calendar.YEAR);
        int mm = calendario.get(Calendar.MONTH)+1;
        int dd = calendario.get(Calendar.DAY_OF_MONTH);
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);
        int seg = calendario.get(Calendar.SECOND);
        int nroPostulantes = data.getNroPostulantesListado(inventarioReg.getCodpagina());
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLConstantes.inventarioreg_dia,dd);
        contentValues.put(SQLConstantes.inventarioreg_mes,mm);
        contentValues.put(SQLConstantes.inventarioreg_anio,yy);
        contentValues.put(SQLConstantes.inventarioreg_hora,hora);
        contentValues.put(SQLConstantes.inventarioreg_min,minuto);
        contentValues.put(SQLConstantes.inventarioreg_seg,seg);
        contentValues.put(SQLConstantes.inventarioreg_npostulantes,nroPostulantes);
        contentValues.put(SQLConstantes.inventarioreg_estado,1);
        data.actualizarInventarioReg(inventarioReg.getCodigo(),3,contentValues);
        InventarioReg invReg = data.getInventarioReg(inventarioReg.getCodigo(),3);
        txtRegistrados.setText("Registrados: " + data.getNroListasRegistradas(nroLocal,inventarioReg.getNaula())+"/"+data.getNroListasTotales(nroLocal,inventarioReg.getNaula()));
        data.close();
        mostrarCorrecto(invReg.getCodigo(),invReg.getNpostulantes());
        final String c = invReg.getCodigo();
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("inventario").document(invReg.getCodigo());
        batch.update(documentReference, "check_registro", 1);
        batch.update(documentReference, "fecha_transferencia", FieldValue.serverTimestamp());
        batch.update(documentReference, "usuario_registro", usuario);
        batch.update(documentReference, "fecha_registro",
                new Timestamp(new Date(invReg.getAnio()-1900,invReg.getMes()-1,invReg.getDia(),
                        invReg.getHora(),invReg.getMin(),invReg.getSeg())));
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Data data = new Data(context);
                data.open();
                data.actualizarInventarioRegSubido(c,3);
                data.close();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "NO GUARDO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void mostrarCorrecto(String codLista, int nroPostulantes){
        lytErrorListaAula.setVisibility(View.GONE);
        lytYaRegistrado.setVisibility(View.GONE);
        lytErrorLista.setVisibility(View.GONE);
        lytCorrecto.setVisibility(View.VISIBLE);
        correctoTxtCodLista.setText(codLista);
        correctoTxtNroPostulantes.setText(""+nroPostulantes);
    }
    public void mostrarErrorCodigo(String codigoLista){
        lytErrorLista.setVisibility(View.VISIBLE);
        lytYaRegistrado.setVisibility(View.GONE);
        lytErrorListaAula.setVisibility(View.GONE);
        lytCorrecto.setVisibility(View.GONE);
        errorCodigoListaTxtCodLista.setText(codigoLista);

    }
    public void mostrarErrorAula(String codLista, int nroPostulantes,int aula){
        lytErrorLista.setVisibility(View.GONE);
        lytYaRegistrado.setVisibility(View.GONE);
        lytErrorListaAula.setVisibility(View.VISIBLE);
        lytCorrecto.setVisibility(View.GONE);
        errorListaAulaTxtCodLista.setText(codLista);
        errorListaAulaTxtNroPostulantes.setText(""+nroPostulantes);
        errorListaAulaTxtAula.setText(aula +"");
    }
    public void mostrarYaRegistrado(String codLista, int nroPostulantes, int aula){
        lytErrorLista.setVisibility(View.GONE);
        lytYaRegistrado.setVisibility(View.VISIBLE);
        lytErrorListaAula.setVisibility(View.GONE);
        lytCorrecto.setVisibility(View.GONE);
        yaRegistradoTxtCodLista.setText(codLista);
        yaRegistradoTxtNroPostulantes.setText(""+nroPostulantes);
        yaRegistradoTxtAula.setText(""+aula);
    }

    public String checkDigito (int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

}
