/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pkg1testfuntions;

/**
 *
 * @author Innovation Computers
 */
public class proceso {
    // Atributo de la clase
    public String nombreProceso;
    public int rafaga;
    public int tiempoLlegada;
    public int contador = 0;
        

    // Constructor que inicializa el nombre del proceso
    public proceso(String nombreProceso, int rafaga, int tiempoLlegada) {
        this.nombreProceso = nombreProceso;
        this.rafaga = rafaga;
        this.tiempoLlegada = tiempoLlegada;
    }

    // Getter para obtener el nombre del proceso
    public String getNombreProceso() {
        return nombreProceso;
    }

    // Setter para modificar el nombre del proceso
    public void setNombreProceso(String nombreProceso) {
        this.nombreProceso = nombreProceso;
    }

    // Método para mostrar información del proceso
    public void mostrarProceso() {
        System.out.println("Nombre del Proceso: " + nombreProceso);
    }
}
