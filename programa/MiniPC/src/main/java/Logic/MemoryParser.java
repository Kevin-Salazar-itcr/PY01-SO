/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.TreeMap;

public class MemoryParser {
    private int userMemory;       // Memoria de usuario
    private int kernelMemory;     // Memoria del kernel
    private int disc;   // memoria secundaria
    private int virtualMemory;    // Memoria virtual
    private String partitioning;  // Tipo de particionamiento -> "Particionamiento fijo", "Particionamiento dinamico", "Paginacion" (no hecho aun, para mas tarde)
    private int parts;            // Tamaño minimo de particiones (en particionamiento dinámico)
    int userStartIndex;
    int ultimaPosicion;
    
    public TreeMap<Integer, String> ram = new TreeMap<>();
    private TreeMap<Integer, String> disco = new TreeMap<>();
    private ArrayList<Integer> particionesDinamicas = new ArrayList<>();
    private ArrayList<Process> listaEspera = new ArrayList<>();
    public ArrayList<Process> listaProcesos = new ArrayList<>();
    
    public ArrayList<String> ejecucion = new ArrayList<>();
    
    // Constructor que inicializa todos los atributos
    public MemoryParser(int user, int kernel, int memSec, int memVirtual, String particionamiento, int tam) {
        actualizar(user, kernel, memSec, memVirtual, particionamiento, tam);
    }
    
    public void actualizar(int user, int kernel, int memSec, int memVirtual, String particionamiento, int tam) {
        this.userMemory = user;
        this.kernelMemory = kernel;
        this.disc = memSec;
        this.virtualMemory = memVirtual;
        this.partitioning = particionamiento;
        this.parts = tam;
        this.userStartIndex = this.kernelMemory;
        this.ultimaPosicion = this.userStartIndex;
        for (int i = 0; i < userMemory+kernelMemory; i++) {
            ram.put(i, "libre");
        }
        
        for (int i = 0; i < disc; i++) {
            disco.put(i, "libre");
        }
        inicializarParticionesDinamicas();
    }
    
    public void limpiar(){
        this.userStartIndex = this.kernelMemory;
        this.ultimaPosicion = this.userStartIndex;
        ram.clear();
        for (int i = 0; i < userMemory+kernelMemory; i++) {
            ram.put(i, "libre");
        }
        
        for (int i = 0; i < disc; i++) {
            disco.put(i, "libre");
        }
        inicializarParticionesDinamicas();
    }
    
    private void inicializarParticionesDinamicas() {
        int tamano = 4;
        int particiones = 0;
        int totalSize = 0;

        while (totalSize < userMemory) {
            if (totalSize + tamano > userMemory) {
                particionesDinamicas.add(userMemory - totalSize);
                break;
            }
            particionesDinamicas.add(tamano);
            particiones++;
            totalSize += tamano;

            if (particiones % 2 == 0) {
                tamano += 2;
            }
        }
    }

    // Getters y Setters
    public int getUserMemory() {
        return userMemory;
    }

    public void setUserMemory(int userMemory) {
        this.userMemory = userMemory;
    }

    public int getKernelMemory() {
        return kernelMemory;
    }

    public void setKernelMemory(int kernelMemory) {
        this.kernelMemory = kernelMemory;
    }

    public int getDisc() {
        return disc;
    }

    public void setDisc(int disc) {
        this.disc = disc;
    }

    public int getVirtualMemory() {
        return virtualMemory;
    }

    public void setVirtualMemory(int virtualMemory) {
        this.virtualMemory = virtualMemory;
    }

    public String getPartitioning() {
        return partitioning;
    }

    public void setPartitioning(String partitioning) {
        this.partitioning = partitioning;
    }

    public int getParts() {
        return parts;
    }

    public void setParts(int size) {
        this.parts = size;
    }

    public TreeMap<Integer, String> getRam() {
        return ram;
    }

    public void setRam(TreeMap<Integer, String> ram) {
        this.ram = ram;
    }

    public ArrayList<Integer> getParticionesDinamicas() {
        return particionesDinamicas;
    }

    public void setParticionesDinamicas(ArrayList<Integer> particionesDinamicas) {
        this.particionesDinamicas = particionesDinamicas;
    }

    public Logic.Process asignarProceso(Process proceso, ArrayList<String> codigo) {
        Logic.Process res = proceso;
        if (partitioning.equals("Particionamiento fijo")) {
            res = asignacionFija(proceso, codigo);
        } 
        
        /*
        else if (partitioning.equals("Paginacion")) {
            Paginacion(proceso);
        } 
        */
        else {
            res = asignacionDinamica(proceso, codigo);
        }
        
        if(listaProcesos.isEmpty()){
            res.tiempoLlegada = 1;
        }
        else{
            res.tiempoLlegada = new Random().nextInt(4) + 2;
        }
        if(!this.listaEspera.contains(res)){
            res.ownPCB.setState(State.READY);
            listaProcesos.add(res);    
        }
        
        return res;
    }

    private Logic.Process asignacionFija(Process proceso, ArrayList<String> codigo) {
        if (ultimaPosicion + codigo.size() <= (kernelMemory + userMemory)) {
            for (int i = 0; i < codigo.size(); i++) {
                ram.put(ultimaPosicion + i, codigo.get(i));
            }
            proceso.ownPCB.setPC(ultimaPosicion);
            ultimaPosicion += codigo.size();
        } else {
            System.out.println("Memoria insuficiente en particionamiento fijo para el proceso " + proceso);
            listaEspera.add(proceso);
        }
        return proceso;
    }

    private Logic.Process asignacionDinamica(Process proceso, ArrayList<String> codigo) {
        if (!firstFit(codigo, proceso) && !bestFit(codigo, proceso) && !nextFit(codigo, proceso)) {
            System.out.println("Memoria insuficiente en particionamiento dinamico para el proceso " + proceso);
            listaEspera.add(proceso);
        }
        return proceso;
    }

    private boolean bestFit(ArrayList<String> codigo, Logic.Process proceso) {
        int mejorInicio = -1;
        int mejorTamano = Integer.MAX_VALUE;
        int pos = userStartIndex;

        for (int particion : particionesDinamicas) {
            if (particion >= codigo.size() && particion < mejorTamano) {
                boolean libre = true;
                for (int i = 0; i < codigo.size(); i++) {
                    if (!ram.get(pos + i).equals("libre")) {
                        libre = false;
                        break;
                    }
                }
                if (libre) {
                    mejorInicio = pos;
                    mejorTamano = particion;
                }
            }
            pos += particion;
        }

        if (mejorInicio != -1) {
            proceso.ownPCB.setPC(mejorInicio);
            for (int i = mejorInicio; i < mejorInicio + codigo.size(); i++) {
                ram.put(i, codigo.get(i));
            }
            return true;
        }
        return false;
    }

    private boolean nextFit(ArrayList<String> codigo, Logic.Process proceso) {
        int pos = ultimaPosicion;

        for (int particion : particionesDinamicas) {
            if (particion >= codigo.size()) {
                boolean libre = true;
                for (int i = 0; i < codigo.size(); i++) {
                    if (!ram.get(pos + i).equals("libre")) {
                        libre = false;
                        break;
                    }
                }
                if (libre) {
                    proceso.ownPCB.setPC(pos);
                    for (int i = pos; i < pos + codigo.size(); i++) {
                        ram.put(i, codigo.get(i));
                    }
                    ultimaPosicion = pos + codigo.size();
                    return true;
                }
            }
            pos += particion;
            if (pos >= (userMemory + kernelMemory)) pos = userStartIndex;
        }

        return false;
    }

    private boolean firstFit(ArrayList<String> codigo, Logic.Process proceso) {
        int pos = userStartIndex;

        for (int particion : particionesDinamicas) {
            if (particion >= codigo.size()) {
                boolean libre = true;
                for (int i = 0; i < codigo.size(); i++) {
                    if (!ram.get(pos + i).equals("libre")) {
                        libre = false;
                        break;
                    }
                }
                if (libre) {
                    proceso.ownPCB.setPC(pos);
                    for (int i = 0; i < codigo.size(); i++) {
                        ram.put(i+pos, codigo.get(i));
                    }
                    return true;
                }
            }
            pos += particion;
        }

        return false;
    }
    
    //devuelve la memoria en un string
    public String formattingRam() {
        StringBuilder sb = new StringBuilder();
        sb.append("**********Kernel**********\n");
        for (Integer key : ram.keySet()) {
            if(key==this.userStartIndex){
                sb.append("**********User**********\n");
            }
            sb.append(key).append(":\t").append(ram.get(key)).append("\n");
            
        }
        return sb.toString();
    }
    
    public String formattingDisc() {
        StringBuilder sb = new StringBuilder();
        for (Integer key : disco.keySet()) {
            sb.append(key).append(":\t").append(disco.get(key)).append("\n");
        }
        return sb.toString();
    }

    public PriorityQueue<Logic.Process> procesosEjeucionesSJF = new PriorityQueue<>(new Comparator<Logic.Process>() {
        @Override
        public int compare(Logic.Process p1, Logic.Process p2) {
            return Integer.compare(p1.rafaga, p2.rafaga); // Orden ascendente por rafaga
        }
    });

    // Comparator para ordenar por rafaga
    Comparator<Logic.Process> comparadorPorRafaga = (Logic.Process p1, Logic.Process p2) -> Integer.compare(p1.rafaga, p2.rafaga); // Orden ascendente por rafaga
    

    // Método para ordenar procesos por tiempo de llegada
    public void ordenarPorTiempoDeLlegada() {
        Collections.sort(listaProcesos, new Comparator<Logic.Process>() {
            @Override
            public int compare(Logic.Process p1, Logic.Process p2) {
                return Integer.compare(p1.tiempoLlegada, p2.tiempoLlegada);
            }
        });
    }

    public void FCFS() {
        ejecucion.clear();
        ordenarPorTiempoDeLlegada();
        int tiempo = 1;

        for (Logic.Process procs : listaProcesos) {
            while (procs.rafaga > 0) {
                ejecucion.add(String.valueOf(procs.ownPCB.id));
                procs.rafaga -= 1;
                tiempo++;
            }
        }
    }

    public boolean verificarPorTiempo(int tiempo) {
        for (Logic.Process procs : listaProcesos) {
            if (procs.tiempoLlegada == tiempo) {
                return true;
            }
        }
        return false;
    }

    public Logic.Process obtenerProcesoTiempo(int tiempo) {
        for (Logic.Process procs : listaProcesos) {
            if (procs.tiempoLlegada == tiempo) {
                return procs;
            }
        }
        return null;
    }

    public void SJF() {
        ejecucion.clear();
        PriorityQueue<Logic.Process> procesosEjeuciones = new PriorityQueue<>(comparadorPorRafaga);
        int tiempoEjecucion = 1;

        for (Logic.Process p : listaProcesos) {
            if (p.tiempoLlegada <= tiempoEjecucion) {
                procesosEjeuciones.add(p);
            }
        }

        while (!procesosEjeuciones.isEmpty()) {
            Logic.Process procs = procesosEjeuciones.poll();

            while (procs.rafaga > 0) {
                ejecucion.add(String.valueOf(procs.ownPCB.id));
                procs.rafaga -= 1;
                tiempoEjecucion++;

                
                for (Logic.Process p : listaProcesos) {
                    if (p.tiempoLlegada == tiempoEjecucion) {
                        procesosEjeuciones.add(p);
                    }
                }

            }
        }
    }

    public void SRT() {
        ejecucion.clear();
        PriorityQueue<Logic.Process> procesosEjeuciones = new PriorityQueue<>(comparadorPorRafaga);
        int tiempoEjecucion = 1;

        for (Logic.Process p : listaProcesos) {
            if (p.tiempoLlegada <= tiempoEjecucion) {
                procesosEjeuciones.add(p);
            }
        }
        Logic.Process procs = null;

        while (!procesosEjeuciones.isEmpty() || procs != null) {

            if (procs == null || !procesosEjeuciones.isEmpty() && procesosEjeuciones.peek().rafaga < procs.rafaga) {
                if (procs != null) {
                    procesosEjeuciones.add(procs);
                }
                procs = procesosEjeuciones.poll();
            }

            ejecucion.add(String.valueOf(procs.ownPCB.id));
            procs.rafaga -= 1;
            tiempoEjecucion++;

            if (procs.rafaga == 0) {
                procs = null;
            }

            for (Logic.Process p : listaProcesos) {
                if (p.tiempoLlegada == tiempoEjecucion) {
                    procesosEjeuciones.add(p);
                }
            }
        }
    }

    public void RoundRobin(int quantum) {
        ejecucion.clear();
        Queue<Logic.Process> colaProcesos = new LinkedList<>();
        int tiempoEjecucion = 1;

        for (Logic.Process p : listaProcesos) {
            if (p.tiempoLlegada <= tiempoEjecucion) {
                colaProcesos.add(p);
            }
        }
        
        Logic.Process procs = null;
        int tiempoRestanteQuantum = quantum;

        while (!colaProcesos.isEmpty() || procs != null) {

            if (procs == null) {
                procs = colaProcesos.poll();
                tiempoRestanteQuantum = quantum;
            }
            ejecucion.add(String.valueOf(procs.ownPCB.id));
            procs.rafaga -= 1;
            tiempoEjecucion++;
            tiempoRestanteQuantum--;

            if (procs.rafaga == 0) {
                procs = null;
            } else if (tiempoRestanteQuantum == 0) {
                colaProcesos.add(procs);
                procs = null;
            }

            for (Logic.Process p : listaProcesos) {
                if (p.tiempoLlegada == tiempoEjecucion) {
                    colaProcesos.add(p);
                }
            }
        }

    }

    // Método para calcular el Ratio de Respuesta
    public double ResponseRatio(Logic.Process p, int tiempoActual) {
        int tiempoEspera = tiempoActual - p.tiempoLlegada;
        double RR = (tiempoEspera + p.rafaga) / p.rafaga;
        return RR;
    }

    public void HRRN() {
        ejecucion.clear();
        ArrayList<Logic.Process> procesosListos = new ArrayList<>(listaProcesos); // Lista de procesos por ejecutar
        int tiempoEjecucion = 1; // Tiempo actual en el que inicia la ejecución

        while (!procesosListos.isEmpty()) {
            // Calcular el ratio de respuesta para cada proceso que ha llegado
            Logic.Process siguienteProceso = null;
            double mayorRatio = -1; // Para encontrar el mayor ratio

            for (Logic.Process p : procesosListos) {
                if (p.tiempoLlegada <= tiempoEjecucion) { // Proceso ha llegado
                    double ratioRespuesta = ResponseRatio(p, tiempoEjecucion);

                    if (ratioRespuesta > mayorRatio) {
                        mayorRatio = ratioRespuesta;
                        siguienteProceso = p;
                    }
                }
            }

            if (siguienteProceso != null) {
                // Ejecutar el proceso con el mayor ratio de respuesta
                for (int i = 0; i < siguienteProceso.rafaga; i++) {
                    ejecucion.add(String.valueOf(siguienteProceso.ownPCB.id)); // Registro de la ejecución
                    tiempoEjecucion++;
                }
                // Eliminar el proceso de la lista de procesos listos
                procesosListos.remove(siguienteProceso);
            } else {
                // Si ningún proceso está listo, avanzar el tiempo
                tiempoEjecucion++;
            }
        }
    }
}
