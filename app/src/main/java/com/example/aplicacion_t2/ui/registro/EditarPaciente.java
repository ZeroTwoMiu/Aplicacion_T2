package com.example.aplicacion_t2.ui.registro;

import static com.example.aplicacion_t2.ui.registro.RegistrarPaciente.servidor;

import android.content.Intent; // Aunque no lo usaremos para la navegación de fragmentos, puede quedarse si se usa para otras cosas.
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController; // Importar NavController
import androidx.navigation.Navigation; // Importar Navigation

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
    private Button guardar, listar; // Tus botones en XML son btnGuardarE y btnListarE
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
            idCont = getArguments().getString("idCont");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editar_paciente, container, false);

        nombre = (EditText) view.findViewById(R.id.etNombreE);
        edad = (EditText) view.findViewById(R.id.etEdadE);
        sexo = (EditText) view.findViewById(R.id.etSexoE);
        altura = (EditText) view.findViewById(R.id.etAlturaE);
        peso = (EditText) view.findViewById(R.id.etPesoE);
        informacion = (EditText) view.findViewById(R.id.etInformacionE);

        // ¡ATENCIÓN AQUÍ! Tus IDs en el XML son btnGuardarE y btnListarE
        guardar = (Button) view.findViewById(R.id.btnGuardarE); // Corregido el ID
        listar = (Button) view.findViewById(R.id.btnListarE);   // Corregido el ID

        guardar.setOnClickListener(this);
        listar.setOnClickListener(this);

        if (idCont != null && !idCont.isEmpty()) {
            ConsultarPaciente(idCont);
        } else {
            Toast.makeText(getContext(), "ID de paciente no recibido para editar.", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    private void ConsultarPaciente(String idCont) {
        String url = servidor + "consultar_paciente.php";
        RequestParams requestParams = new RequestParams();
        requestParams.put("idCont",idCont);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, requestParams, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(respuesta);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject contactoJson = jsonArray.getJSONObject(i);
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
                    Toast.makeText(getContext(), "Error al parsear JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String mensaje = "Error al consultar paciente: " + statusCode + " - " + error.getMessage();
                Toast.makeText(getContext(),mensaje,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ActualizarPaciente(String idCont, String nom, int ed, String sex, float alt, float pes, String info) {
        String url = servidor + "actualizar_paciente.php";
        RequestParams requestParams = new RequestParams();
        requestParams.put("idCont",idCont);
        requestParams.put("nombres",nom);
        requestParams.put("edad",ed);
        requestParams.put("sexo",sex);
        requestParams.put("altura",alt);
        requestParams.put("peso",pes);
        requestParams.put("informacion",info);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                Toast.makeText(getContext(), "Respuesta: " + respuesta, Toast.LENGTH_LONG).show();
                // Opcional: Después de actualizar, puedes navegar de vuelta a la lista automáticamente
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_editarPaciente_to_listarPaciente); // Navega usando la acción
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String mensaje = "Error al actualizar: " + statusCode + " - " + error.getMessage();
                Toast.makeText(getContext(),mensaje,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v==guardar) {
            String nom = nombre.getText().toString();
            int ed = 0;
            if (!edad.getText().toString().isEmpty()) {
                ed = Integer.parseInt(edad.getText().toString());
            }

            String sex = sexo.getText().toString();
            float alt = 0.0f;
            if (!altura.getText().toString().isEmpty()) {
                alt = Float.parseFloat(altura.getText().toString());
            }

            float pes = 0.0f;
            if (!peso.getText().toString().isEmpty()) {
                pes = Float.parseFloat(peso.getText().toString());
            }

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
        else if(v==listar) {
            // ¡CORRECCIÓN CLAVE AQUÍ! Usar Navigation Components para volver al fragmento de lista
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            // Navegar usando la acción definida en mobile_navigation.xml
            navController.navigate(R.id.action_editarPaciente_to_listarPaciente);
        }
    }
}