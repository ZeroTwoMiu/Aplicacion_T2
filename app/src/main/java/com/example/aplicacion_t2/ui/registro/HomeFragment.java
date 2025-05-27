package com.example.aplicacion_t2.ui.registro;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
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
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private FragmentHomeBinding binding;
    private ArrayList<String> listaPacientes = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private ArrayList<String> listaIds = new ArrayList<>();

    private static final String servidor = "http://10.0.2.2/clinicaT2/";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.lstPacientes.setOnItemClickListener(this);


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
        String url = servidor + "mostrar_paciente.php"; // Cambia por tu URL real

        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody);
                    JSONArray jsonArray = new JSONArray(respuesta);
                    listaPacientes.clear();
                    listaIds.clear();

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
                        listaIds.add(id);
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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String idCont = listaIds.get(position); // Obtener ID del paciente seleccionado

        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.opciones, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.opc_editar) {
                EditarContacto(idCont);
            } else if (item.getItemId() == R.id.opc_eliminar) {
                new AlertDialog.Builder(getContext())
                        .setMessage("¿Estás seguro de que deseas eliminar este paciente?")
                        .setCancelable(false)
                        .setPositiveButton("Sí", (dialog, which) -> EliminiarPaciente(idCont))
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void EliminiarPaciente(String idCont) {
        String url = servidor + "eliminar_paciente.php";
        RequestParams requestParams = new RequestParams();
        requestParams.put("idCont", idCont);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                Toast.makeText(getContext(), "Eliminado correctamente", Toast.LENGTH_LONG).show();

                // Volver a cargar pacientes directamente
                cargarPacientes();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String mensaje = "Error: " + statusCode + " - " + error.getMessage();
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void EditarContacto(String idCont) {
        Bundle bundle = new Bundle();
        bundle.putString("idCont", idCont);

        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_listarPaciente_to_editarPaciente2, bundle);
    }

}