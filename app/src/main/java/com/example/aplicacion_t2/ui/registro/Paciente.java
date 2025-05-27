package com.example.aplicacion_t2.ui.registro;

public class Paciente {

    String id,nombre, edad, sexo, informacion;
    float altura,peso;
    private float imc;
    private String clasificacionIMC;

    public Paciente(String id,String nombre, String edad, String sexo, String informacion, float altura, float peso) {
        this.id =id;
        this.nombre = nombre;
        this.edad = edad;
        this.sexo = sexo;
        this.informacion = informacion;
        this.altura = altura;
        this.peso = peso;
        calcularIMC();
    }

    private void calcularIMC() {
        this.imc = peso / (altura * altura);
        this.clasificacionIMC = clasificarIMC(this.imc);
    }
    private String clasificarIMC(float imc) {
        if (imc < 18.5f) return "Bajo peso";
        else if (imc < 24.9f) return "Normal";
        else if (imc < 29.9f) return "Sobrepeso";
        else return "Obesidad";
    }

    public float getImc() {
        return imc;
    }
    public String getClasificacionIMC() {
        return clasificacionIMC;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getInformacion() {
        return informacion;
    }

    public void setInformacion(String informacion) {
        this.informacion = informacion;
    }

    public float getAltura() {
        return altura;
    }

    public void setAltura(float altura) {
        this.altura = altura;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

}
