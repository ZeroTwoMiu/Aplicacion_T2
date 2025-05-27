package com.example.aplicacion_t2.ui.registro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.aplicacion_t2.R;
import com.example.aplicacion_t2.databinding.FragmentHomeBinding;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ArrayList<String> listaPacientes = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private static final String SERVIDOR = "http://10.0.2.2/clinicaT2/"; // Cambia a tu IP/URL

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Texto observable para título (opcional)
        homeViewModel.getText().observe(getViewLifecycleOwner(), binding.textView2::setText);

        // Configurar adaptador inicial vacío
        adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1,
                listaPacientes);
        binding.lstPacientes.setAdapter(adapter);

        // Botón "Nuevo Paciente"
        binding.btnNuevo.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_listarPaciente_to_registrarPaciente2);
        });

        cargarPacientes(); // Cargar lista desde servidor

        return root;
    }

    private void cargarPacientes() {
        String url = SERVIDOR + "mostrar_paciente.php"; // Cambia por tu URL real

        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody);
                    JSONArray jsonArray = new JSONArray(respuesta);
                    listaPacientes.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject pacienteJson = jsonArray.getJSONObject(i);

                        String id = pacienteJson.getString("id_paciente");
                        String nombre = pacienteJson.getString("nom_paciente");
                        String edad = pacienteJson.getString("ed_paciente");
                        String sexo = pacienteJson.getString("sexo_paciente");
                        String info = pacienteJson.getString("info_paciente");
                        float altura = (float) pacienteJson.getDouble("alt_paciente");
                        float peso = (float) pacienteJson.getDouble("peso_paciente");

                        // Crear instancia de Paciente para calcular IMC y clasificación
                        Paciente paciente = new Paciente(id, nombre, edad, sexo, info, altura, peso);

                        float imc = paciente.getImc();
                        String clasificacion = paciente.getClasificacionIMC();

                        // Crear string con todos los datos necesarios
                        String pacienteInfo = nombre + " - " + edad + " años - IMC: " + String.format("%.2f", imc)
                                + " (" + clasificacion + ")";

                        listaPacientes.add(pacienteInfo);
                    }


                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al cargar pacientes: " + statusCode, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
