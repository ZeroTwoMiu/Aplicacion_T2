package com.example.aplicacion_t2.ui.registro;

import static com.example.aplicacion_t2.ui.registro.RegistrarPaciente.servidor;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aplicacion_t2.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class EditarPaciente extends Fragment implements View.OnClickListener {

    private EditText nombre, edad, sexo, altura, peso, informacion;
    private Button guardar, listar;
    String idCont;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public EditarPaciente() {
        // Required empty public constructor
    }

    public static EditarPaciente newInstance(String param1, String param2) {
        EditarPaciente fragment = new EditarPaciente();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        nombre = (EditText) getView().findViewById(R.id.etNombreE);
        edad = (EditText) getView().findViewById(R.id.etEdadE);
        sexo = (EditText) getView().findViewById(R.id.etSexoE);
        altura = (EditText) getView().findViewById(R.id.etAlturaE);
        peso = (EditText) getView().findViewById(R.id.etPesoE);
        informacion = (EditText) getView().findViewById(R.id.etInformacionE);
        guardar = (Button) getView().findViewById(R.id.btnGuardar);
        listar = (Button) getView().findViewById(R.id.btnListar);
        guardar.setOnClickListener(this);
        listar.setOnClickListener(this);
        idCont = getArguments().getString("idCont");
        ConsultarPaciente(idCont);
    }

    private void ConsultarPaciente(String idCont) {
        //Declarar la URL
        String url = servidor + "consultar_paciente.php";

        //Enviar parámetros
        RequestParams requestParams = new RequestParams();
        requestParams.put("idCont",idCont);

        //Envio al web service y respuesta
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, requestParams, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(respuesta);

                    // Recorrer el array JSON y agregar cada contacto a la lista
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject contactoJson = jsonArray.getJSONObject(i);

                        String id_paciente = contactoJson.getString("id_paciente");
                        String nom_paciente = contactoJson.getString("nom_paciente");
                        int ed_paciente = Integer.parseInt(contactoJson.getString("ed_paciente"));
                        String sex_paciente = contactoJson.getString("sexo_paciente");
                        float alt_paciente = Float.parseFloat(contactoJson.getString("alt_paciente"));
                        float pes_paciente = Float.parseFloat(contactoJson.getString("peso_paciente"));
                        String info_paciente = contactoJson.getString("info_paciente");

                        nombre.setText(nom_paciente);
                        edad.setText(String.valueOf(ed_paciente));
                        sexo.setText(sex_paciente);
                        altura.setText(String.valueOf(alt_paciente));
                        peso.setText(String.valueOf(pes_paciente));
                        informacion.setText(info_paciente);

                    }


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String mensaje = "Error: " + statusCode + " - " + error.getMessage();
                Toast.makeText(getContext(),mensaje,Toast.LENGTH_LONG).show();
            }
        });

    }

    private void ActualizarPaciente(String idCont, String nom, int ed, String sex, float alt, float pes, String info) {

        //Declarar la URL
        String url = servidor + "actualizar_paciente.php";

        //Enviar parámetros
        RequestParams requestParams = new RequestParams();
        requestParams.put("idCont",idCont);
        requestParams.put("nombres",nom);
        requestParams.put("edad",ed);
        requestParams.put("sexo",sex);
        requestParams.put("altura",alt);
        requestParams.put("peso",pes);
        requestParams.put("informacion",info);

        //Envio al web service y respuesta
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                Toast.makeText(getContext(), "Respuesta: " + respuesta, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String mensaje = "Error: " + statusCode + " - " + error.getMessage();
                Toast.makeText(getContext(),mensaje,Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editar_paciente, container, false);
    }

    @Override
    public void onClick(View v) {
        if(v==guardar)
        {
            String nom = nombre.getText().toString();
            int ed = Integer.parseInt(edad.getText().toString());
            String sex = sexo.getText().toString();
            float alt = Float.parseFloat(altura.getText().toString());
            float pes = Float.parseFloat(peso.getText().toString());
            String info = informacion.getText().toString();

            if(nom.isEmpty() || sex.isEmpty() || info.isEmpty()) {
                Toast.makeText(getContext(),"Completar los datos requeridos",Toast.LENGTH_LONG).show();
            }
            else if(ed < 0) {
                Toast.makeText(getContext(),"La edad tiene que ser mayor o igual a 0",Toast.LENGTH_LONG).show();
            }
            else if(alt <= 0 || pes <= 0) {
                Toast.makeText(getContext(),"La altura y el peso tienen que ser mayores a 0",Toast.LENGTH_LONG).show();
            }
            else {
                ActualizarPaciente(idCont, nom, ed, sex, alt, pes, info);
            }

        }

        else if(v==listar)
        {
            // Crear el Intent para abrir ListaActivity
            Intent intent = new Intent(getContext(), ListarPaciente.class);
            startActivity(intent); // Iniciar la actividad
        }

    }
}