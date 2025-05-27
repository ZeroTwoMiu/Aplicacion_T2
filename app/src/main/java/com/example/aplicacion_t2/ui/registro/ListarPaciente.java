package com.example.aplicacion_t2.ui.registro;

import static com.example.aplicacion_t2.ui.registro.RegistrarPaciente.servidor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aplicacion_t2.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class ListarPaciente extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ListView lista;
    private Button nuevo;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ListarPaciente() {
        // Required empty public constructor
    }

    public static ListarPaciente newInstance(String param1, String param2) {
        ListarPaciente fragment = new ListarPaciente();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listar_paciente, container, false);

        lista = view.findViewById(R.id.lstPacientes);
        nuevo = view.findViewById(R.id.btnNuevo);

        lista.setOnItemClickListener(this);
        nuevo.setOnClickListener(this);

        MostrarDatos();

        return view;
    }

    private void MostrarDatos() {
        String url = servidor + "mostrar_paciente.php";
        RequestParams requestParams = new RequestParams();

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(respuesta);
                    ArrayList<Paciente> pacientesList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject pacienteJson = jsonArray.getJSONObject(i);

                        String id_paciente = pacienteJson.getString("id_paciente");
                        String nom_paciente = pacienteJson.getString("nom_paciente");
                        String ed_paciente = pacienteJson.getString("ed_paciente");
                        String sex_paciente = pacienteJson.getString("sexo_paciente");
                        String info_paciente = pacienteJson.getString("info_paciente");
                        float alt_paciente = Float.parseFloat(pacienteJson.getString("alt_paciente"));
                        float peso_paciente = Float.parseFloat(pacienteJson.getString("peso_paciente"));

                        Paciente paciente = new Paciente(
                                id_paciente,
                                nom_paciente,
                                ed_paciente,
                                sex_paciente,
                                info_paciente,
                                alt_paciente,
                                peso_paciente
                        );
                        pacientesList.add(paciente);
                    }

                    PacienteAdapter adapter = new PacienteAdapter(getContext(), pacientesList);
                    lista.setAdapter(adapter);

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error al parsear JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String mensaje = "Error: " + statusCode + " - " + error.getMessage();
                Toast.makeText(getContext(),mensaje,Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        // Navegar al fragmento para registrar un nuevo paciente
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.registrarPaciente); // Usa el ID del destino
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent==lista) {
            TextView tvId = (TextView) view.findViewById(R.id.tvId);
            String idCont = tvId.getText().toString();
            PopupMenu popupMenu = new PopupMenu(getContext(),view);
            popupMenu.getMenuInflater().inflate(R.menu.opciones, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.opc_editar)
                    {
                        EditarContacto(idCont);
                    }
                    else if (item.getItemId() == R.id.opc_eliminar) {
                        new AlertDialog.Builder(getContext())
                                .setMessage("¿Estás seguro de que deseas eliminar este contacto?")
                                .setCancelable(false)
                                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        EliminiarPaciente(idCont);
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                        return true;
                    }
                    return false;
                }
            });
            popupMenu.show();
        }
    }

    private void EliminiarPaciente(String idCont) {
        String url = servidor + "eliminar_paciente.php";
        RequestParams requestParams = new RequestParams();
        requestParams.put("idCont",idCont);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                Toast.makeText(getContext(), "Respuesta: " + respuesta, Toast.LENGTH_LONG).show();
                MostrarDatos();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String mensaje = "Error al eliminar: " + statusCode + " - " + error.getMessage();
                Toast.makeText(getContext(),mensaje,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void EditarContacto(String idCont) {
        // ¡CORRECCIÓN CLAVE AQUÍ! Usar Navigation Components para navegar al fragmento de edición
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        Bundle bundle = new Bundle();
        bundle.putString("idCont", idCont);
        // Navegar usando la acción definida en mobile_navigation.xml
        navController.navigate(R.id.action_listarPaciente_to_editarPaciente2, bundle);
    }
}