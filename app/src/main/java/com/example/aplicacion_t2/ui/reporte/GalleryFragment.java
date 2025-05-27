package com.example.aplicacion_t2.ui.reporte;

import static com.example.aplicacion_t2.ui.registro.RegistrarPaciente.servidor; // Importar el servidor

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.aplicacion_t2.R; // Asegúrate de que R se importe correctamente
import com.example.aplicacion_t2.databinding.FragmentGalleryBinding;
import com.example.aplicacion_t2.ui.registro.Paciente; // Importa la clase Paciente
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private PieChart pieChart;
    private TextView imcSummaryTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pieChart = binding.pieChartImc; // Enlazar el PieChart
        imcSummaryTextView = binding.textImcSummary; // Enlazar el TextView para el resumen

        // Cargar y mostrar los datos del IMC
        cargarDatosIMC();

        return root;
    }

    private void cargarDatosIMC() {
        String url = servidor + "mostrar_paciente.php"; // Usa la misma URL para obtener pacientes

        AsyncHttpClient cliente = new AsyncHttpClient();
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String respuesta = new String(responseBody);
                    JSONArray jsonArray = new JSONArray(respuesta);

                    // Map para almacenar la cuenta de cada clasificación de IMC
                    Map<String, Integer> imcClasificaciones = new HashMap<>();
                    imcClasificaciones.put("Bajo peso", 0);
                    imcClasificaciones.put("Normal", 0);
                    imcClasificaciones.put("Sobrepeso", 0);
                    imcClasificaciones.put("Obesidad", 0);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject pacienteJson = jsonArray.getJSONObject(i);

                        // Asegúrate de que los campos existan y sean correctos
                        String id = pacienteJson.getString("id_paciente");
                        String nombre = pacienteJson.getString("nom_paciente");
                        String edad = pacienteJson.getString("ed_paciente"); // Mantener como String si es necesario
                        String sexo = pacienteJson.getString("sexo_paciente");
                        String info = pacienteJson.getString("info_paciente");
                        double alturaDbl = pacienteJson.getDouble("alt_paciente");
                        double pesoDbl = pacienteJson.getDouble("peso_paciente");

                        // Convertir a float si tu clase Paciente lo espera así, o ajustar Paciente para usar double
                        Paciente paciente = new Paciente(id, nombre, edad, sexo, info, (float) alturaDbl, (float) pesoDbl);

                        String clasificacion = paciente.getClasificacionIMC();
                        if (imcClasificaciones.containsKey(clasificacion)) {
                            imcClasificaciones.put(clasificacion, imcClasificaciones.get(clasificacion) + 1);
                        } else {
                            // En caso de una clasificación inesperada, aunque con tu código Paciente, no debería ocurrir
                            imcClasificaciones.put(clasificacion, 1);
                        }
                    }

                    actualizarGraficoYResumen(imcClasificaciones);

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error JSON al cargar datos de IMC: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al cargar pacientes para el reporte: " + statusCode, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarGraficoYResumen(Map<String, Integer> imcClasificaciones) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        StringBuilder summary = new StringBuilder("Resumen de Clasificación IMC:\n");
        int totalPacientes = 0;

        // Definir colores para las categorías
        int[] MATERIAL_COLORS = {
                Color.rgb(255, 102, 0),    // Naranja (Bajo peso)
                Color.rgb(106, 150, 31),   // Verde (Normal)
                Color.rgb(193, 37, 82),    // Rojo oscuro (Sobrepeso)
                Color.rgb(0, 128, 255)     // Azul (Obesidad)
        };

        // Orden de las categorías para colores consistentes
        String[] order = {"Bajo peso", "Normal", "Sobrepeso", "Obesidad"};

        for (int i = 0; i < order.length; i++) {
            String clasificacion = order[i];
            int count = imcClasificaciones.getOrDefault(clasificacion, 0); // Usar getOrDefault para seguridad
            if (count > 0) { // Solo añadir al gráfico si hay pacientes en esa categoría
                entries.add(new PieEntry(count, clasificacion));
                colors.add(MATERIAL_COLORS[i]);
            }
            summary.append("- ").append(clasificacion).append(": ").append(count).append(" pacientes\n");
            totalPacientes += count;
        }

        if (totalPacientes == 0) {
            summary.append("\nNo hay datos de pacientes para mostrar.");
            pieChart.clear(); // Limpiar el gráfico si no hay datos
            pieChart.setNoDataText("No hay datos de pacientes para el reporte.");
        } else {
            PieDataSet dataSet = new PieDataSet(entries, "Clasificación IMC");
            dataSet.setColors(colors);
            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.WHITE); // Color del texto de los valores

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(pieChart)); // Formato de porcentaje
            pieChart.setData(data);

            pieChart.setUsePercentValues(true); // Mostrar valores como porcentaje
            pieChart.getDescription().setEnabled(false); // Deshabilitar descripción
            pieChart.setExtraOffsets(5f, 10f, 5f, 5f);
            pieChart.setDragDecelerationFrictionCoef(0.95f);
            pieChart.setDrawHoleEnabled(true);
            pieChart.setHoleColor(Color.WHITE);
            pieChart.setTransparentCircleColor(Color.WHITE);
            pieChart.setTransparentCircleAlpha(110);
            pieChart.setHoleRadius(58f);
            pieChart.setTransparentCircleRadius(61f);
            pieChart.setDrawCenterText(true);
            pieChart.setCenterText("Distribución IMC"); // Texto central del gráfico
            pieChart.setCenterTextSize(16f);
            pieChart.setCenterTextColor(Color.BLACK);
            pieChart.setEntryLabelColor(Color.WHITE); // Color de las etiquetas de las entradas
            pieChart.setEntryLabelTextSize(10f);

            pieChart.animateY(1400); // Animación
            pieChart.invalidate(); // Refrescar el gráfico
        }

        imcSummaryTextView.setText(summary.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}