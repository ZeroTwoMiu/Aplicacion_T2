package com.example.aplicacion_t2.ui.registro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aplicacion_t2.R;

import java.util.List;

public class PacienteAdapter extends BaseAdapter {

    private Context context;
    private List<Paciente> pacientes;

    public PacienteAdapter(Context context, List<Paciente> pacientes) {
        this.context = context;
        this.pacientes = pacientes;
    }

    @Override
    public int getCount() {
        return pacientes.size();
    }

    @Override
    public Object getItem(int position) {
        return pacientes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflar el layout del item
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_paciente, parent, false);
        }

        // Obtener los datos del contacto
        Paciente paciente = pacientes.get(position);

        // Referenciar las vistas
        TextView tvId = convertView.findViewById(R.id.tvId);
        TextView tvNombres = convertView.findViewById(R.id.tvNombres);
        TextView tvEdad = convertView.findViewById(R.id.tvEdad);
        TextView tvIMC = convertView.findViewById(R.id.tvIMC);
        TextView tvClasificacion = convertView.findViewById(R.id.tvClasificacionIMC);

        // Establecer los valores
        tvId.setText(paciente.getId());
        tvNombres.setText("Nombres y Apellidos: "+paciente.getNombre());
        tvEdad.setText("Edad: "+paciente.getEdad());
        tvIMC.setText("IMC: " + String.format("%.2f", paciente.getImc()));
        tvClasificacion.setText("Clasificación: " + paciente.getClasificacionIMC());
        return convertView;
    }

}
