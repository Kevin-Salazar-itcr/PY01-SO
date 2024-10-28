import java.util.*;

class Proceso {
    String nombre;
    int tamano;

    public Proceso(String nombre, int tamano) {
        this.nombre = nombre;
        this.tamano = tamano;
    }

    @Override
    public String toString() {
        return nombre + "(" + tamano + ")";
    }
}

class CPU {
    TreeMap<Integer, String> memoria = new TreeMap<>();
    List<Integer> particionesDinamicas = new ArrayList<>();
    List<Proceso> espera = new ArrayList<>();
    int ramSize = 20;
    int kernelSize = 6;
    int userSize = 14;
    int userStartIndex = kernelSize;
    int ultimaPosicion = userStartIndex;
    boolean particionamientoFijo;

    public CPU(int modo) {
        for (int i = 0; i < ramSize; i++) {
            memoria.put(i, "libre");
        }

        if (modo == 1) {
            this.particionamientoFijo = true;
        } else {
            this.particionamientoFijo = false;
            inicializarParticionesDinamicas();
        }
    }

    private void inicializarParticionesDinamicas() {
        int tamano = 4;
        int particiones = 0;
        int totalSize = 0;

        while (totalSize < userSize) {
            if (totalSize + tamano > userSize) {
                particionesDinamicas.add(userSize - totalSize);
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

    public void asignarProceso(Proceso proceso) {
        if (particionamientoFijo) {
            asignacionFija(proceso);
        } else {
            asignacionDinamica(proceso);
        }
    }

    private void asignacionFija(Proceso proceso) {
        if (ultimaPosicion + proceso.tamano <= ramSize) {
            for (int i = 0; i < proceso.tamano; i++) {
                memoria.put(ultimaPosicion + i, proceso.nombre);
            }
            ultimaPosicion += proceso.tamano;
        } else {
            System.out.println("Memoria insuficiente en particionamiento fijo para el proceso " + proceso);
            espera.add(proceso);
        }
    }

    private void asignacionDinamica(Proceso proceso) {
        if (!firstFit(proceso) && !bestFit(proceso) && !nextFit(proceso)) {
            System.out.println("Memoria insuficiente en particionamiento dinamico para el proceso " + proceso);
            espera.add(proceso);
        }
    }

    private boolean bestFit(Proceso proceso) {
        int mejorInicio = -1;
        int mejorTamano = Integer.MAX_VALUE;
        int pos = userStartIndex;

        for (int particion : particionesDinamicas) {
            if (particion >= proceso.tamano && particion < mejorTamano) {
                boolean libre = true;
                for (int i = 0; i < proceso.tamano; i++) {
                    if (!memoria.get(pos + i).equals("libre")) {
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
            for (int i = mejorInicio; i < mejorInicio + proceso.tamano; i++) {
                memoria.put(i, proceso.nombre);
            }
            return true;
        }
        return false;
    }

    private boolean nextFit(Proceso proceso) {
        int pos = ultimaPosicion;

        for (int particion : particionesDinamicas) {
            if (particion >= proceso.tamano) {
                boolean libre = true;
                for (int i = 0; i < proceso.tamano; i++) {
                    if (!memoria.get(pos + i).equals("libre")) {
                        libre = false;
                        break;
                    }
                }
                if (libre) {
                    for (int i = pos; i < pos + proceso.tamano; i++) {
                        memoria.put(i, proceso.nombre);
                    }
                    ultimaPosicion = pos + proceso.tamano;
                    return true;
                }
            }
            pos += particion;
            if (pos >= ramSize) pos = userStartIndex;
        }

        return false;
    }

    private boolean firstFit(Proceso proceso) {
        int pos = userStartIndex;

        for (int particion : particionesDinamicas) {
            if (particion >= proceso.tamano) {
                boolean libre = true;
                for (int i = 0; i < proceso.tamano; i++) {
                    if (!memoria.get(pos + i).equals("libre")) {
                        libre = false;
                        break;
                    }
                }
                if (libre) {
                    for (int i = pos; i < pos + proceso.tamano; i++) {
                        memoria.put(i, proceso.nombre);
                    }
                    return true;
                }
            }
            pos += particion;
        }

        return false;
    }

    public void imprimirMemoria() {
        System.out.println("Estado de memoria:");
        for (int i = 0; i < ramSize; i++) {
            System.out.print(memoria.get(i) + " ");
        }
        System.out.println("\n----------------------------");
    }
}

public class Main {
    public static void main(String[] args) {
        // Prueba con particionamiento fijo
        CPU cpuFijo = new CPU(1);
        System.out.println("Prueba con particionamiento fijo:");
        cpuFijo.asignarProceso(new Proceso("A", 3));
        cpuFijo.asignarProceso(new Proceso("B", 4));
        cpuFijo.asignarProceso(new Proceso("C", 2));
        cpuFijo.imprimirMemoria();

        // Prueba con particionamiento dinamico
        CPU cpuDinamico = new CPU(2);
        System.out.println("Prueba con particionamiento dinamico:");
        cpuDinamico.asignarProceso(new Proceso("X", 6));
        cpuDinamico.asignarProceso(new Proceso("Y", 2));
        cpuDinamico.asignarProceso(new Proceso("Z", 2));
        cpuDinamico.imprimirMemoria();
    }
}
