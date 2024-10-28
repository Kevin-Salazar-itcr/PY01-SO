/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import java.util.ArrayList;
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
    
    private TreeMap<Integer, String> ram = new TreeMap<>();
    private TreeMap<Integer, String> disco = new TreeMap<>();
    private ArrayList<Integer> particionesDinamicas = new ArrayList<>();
    private ArrayList<Process> listaEspera = new ArrayList<>();
    
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

}
