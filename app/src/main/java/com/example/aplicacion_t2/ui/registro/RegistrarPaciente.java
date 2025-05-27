package com.example.aplicacion_t2.ui.registro;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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

import cz.msebera.android.httpclient.Header;

public class RegistrarPaciente extends Fragment implements View.OnClickListener {

    private EditText nombre, edad, sexo, altura, peso, informacion;
    private Button guardar, listar;
    public static final String servidor = "http://10.0.2.2/clinicaT2/";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public RegistrarPaciente() {
        // Required empty public constructor
    }

    public static RegistrarPaciente newInstance(String param1, String param2) {
        RegistrarPaciente fragment = new RegistrarPaciente();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // NO uses onCreate para manipular las vistas!!
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Solo recupera argumentos si hay
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // Aquí inflamos y enlazamos las vistas correctamente
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrar_paciente, container, false);

        // Aquí inicializamos las vistas con findViewById sobre la vista inflada
        nombre = view.findViewById(R.id.etNombreCompleto);
        edad = view.findViewById(R.id.etEdad);
        sexo = view.findViewById(R.id.etSexo);
        altura = view.findViewById(R.id.etAltura);
        peso = view.findViewById(R.id.etPeso);
        informacion = view.findViewById(R.id.etInformacion);
        guardar = view.findViewById(R.id.btnGuardar);
        listar = view.findViewById(R.id.btnListar);

        // Configuramos los listeners
        guardar.setOnClickListener(this);
        listar.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == guardar) {
            String nom = nombre.getText().toString();
            String edadStr = edad.getText().toString();
            String sex = sexo.getText().toString();
            String alturaStr = altura.getText().toString();
            String pesoStr = peso.getText().toString();
            String info = informacion.getText().toString();

            // Validar campos vacíos
            if (nom.isEmpty() || edadStr.isEmpty() || sex.isEmpty() || alturaStr.isEmpty() || pesoStr.isEmpty() || info.isEmpty()) {
                Toast.makeText(getContext(), "Completar todos los campos", Toast.LENGTH_LONG).show();
                return;
            }

            int ed;
            float alt, pes;

            try {
                ed = Integer.parseInt(edadStr);
                alt = Float.parseFloat(alturaStr);
                pes = Float.parseFloat(pesoStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Edad, altura y peso deben ser números válidos", Toast.LENGTH_LONG).show();
                return;
            }

            // Validaciones lógicas
            if (ed < 0) {
                Toast.makeText(getContext(), "La edad tiene que ser mayor o igual a 0", Toast.LENGTH_LONG).show();
            } else if (alt <= 0 || pes <= 0) {
                Toast.makeText(getContext(), "La altura y el peso tienen que ser mayores a 0", Toast.LENGTH_LONG).show();
            } else {
                GuardarPaciente(nom, ed, sex, alt, pes, info);
            }

        } else if (v == listar) {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_registrarPaciente_to_listarPaciente);
        }
    }

    private void GuardarPaciente(String nom, int ed, String sex, float alt, float pes, String info) {
        //Declarar la URL
        String url = servidor + "guardar_paciente.php";

        //Enviar parámetros
        RequestParams requestParams = new RequestParams();
        requestParams.put("nombres", nom);
        requestParams.put("edad", ed);
        requestParams.put("sexo", sex);
        requestParams.put("altura", alt);
        requestParams.put("peso", pes);
        requestParams.put("informacion", info);

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
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }
}
