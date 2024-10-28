package pkg1testfuntions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Clase CPU para la simulación de algoritmos de planificación.
 */
public class CPU {

    public ArrayList<proceso> listaProcesos = new ArrayList<>();
    public ArrayList<String> ejecucion = new ArrayList<>(); // Lista para registrar la ejecución de los procesos

    @Override
    public String toString() {
        return "CPU{" + "ejecucion=" + ejecucion + '}';
    }

    // PriorityQueue configurado con un Comparator para ordenar por rafaga
    public PriorityQueue<proceso> procesosEjeucionesSJF = new PriorityQueue<>(new Comparator<proceso>() {
        @Override
        public int compare(proceso p1, proceso p2) {
            return Integer.compare(p1.rafaga, p2.rafaga); // Orden ascendente por rafaga
        }
    });

    // Comparator para ordenar por rafaga
    Comparator<proceso> comparadorPorRafaga = new Comparator<proceso>() {
        @Override
        public int compare(proceso p1, proceso p2) {
            return Integer.compare(p1.rafaga, p2.rafaga); // Orden ascendente por rafaga
        }
    };

    // Método para ordenar procesos por tiempo de llegada
    public void ordenarPorTiempoDeLlegada() {
        Collections.sort(listaProcesos, new Comparator<proceso>() {
            @Override
            public int compare(proceso p1, proceso p2) {
                return Integer.compare(p1.tiempoLlegada, p2.tiempoLlegada);
            }
        });
    }

    public void FCFS() {
        ejecucion.clear();
        ordenarPorTiempoDeLlegada();
        int tiempo = 1;

        for (proceso procs : listaProcesos) {
            while (procs.rafaga > 0) {
                ejecucion.add(procs.nombreProceso);
                procs.rafaga -= 1;
                tiempo++;
            }
        }
    }

    public boolean verificarPorTiempo(int tiempo) {
        for (proceso procs : listaProcesos) {
            if (procs.tiempoLlegada == tiempo) {
                return true;
            }
        }
        return false;
    }

    public proceso obtenerProcesoTiempo(int tiempo) {
        for (proceso procs : listaProcesos) {
            if (procs.tiempoLlegada == tiempo) {
                return procs;
            }
        }
        return null;
    }

    public void SJF() {

        PriorityQueue<proceso> procesosEjeuciones = new PriorityQueue<>(comparadorPorRafaga);
        int tiempoEjecucion = 0;

        for (proceso p : listaProcesos) {
            if (p.tiempoLlegada <= tiempoEjecucion) {
                procesosEjeuciones.add(p);
            }
        }

        while (!procesosEjeuciones.isEmpty()) {
            proceso procs = procesosEjeuciones.poll();

            while (procs.rafaga > 0) {
                ejecucion.add(procs.nombreProceso);
                procs.rafaga -= 1;
                tiempoEjecucion++;

                
                for (proceso p : listaProcesos) {
                    if (p.tiempoLlegada == tiempoEjecucion) {
                        procesosEjeuciones.add(p);
                    }
                }

            }
        }
    }

    public void SRT() {

        PriorityQueue<proceso> procesosEjeuciones = new PriorityQueue<>(comparadorPorRafaga);
        int tiempoEjecucion = 0;

        for (proceso p : listaProcesos) {
            if (p.tiempoLlegada <= tiempoEjecucion) {
                procesosEjeuciones.add(p);
            }
        }
        proceso procs = null;

        while (!procesosEjeuciones.isEmpty() || procs != null) {

            if (procs == null || !procesosEjeuciones.isEmpty() && procesosEjeuciones.peek().rafaga < procs.rafaga) {
                if (procs != null) {
                    procesosEjeuciones.add(procs);
                }
                procs = procesosEjeuciones.poll();
            }

            ejecucion.add(procs.nombreProceso);
            procs.rafaga -= 1;
            tiempoEjecucion++;

            if (procs.rafaga == 0) {
                procs = null;
            }

            for (proceso p : listaProcesos) {
                if (p.tiempoLlegada == tiempoEjecucion) {
                    procesosEjeuciones.add(p);
                }
            }
        }
    }

    public void RoundRobin(int quantum) {

        Queue<proceso> colaProcesos = new LinkedList<>();
        int tiempoEjecucion = 0;

        for (proceso p : listaProcesos) {
            if (p.tiempoLlegada <= tiempoEjecucion) {
                colaProcesos.add(p);
            }
        }

        proceso procs = null;
        int tiempoRestanteQuantum = quantum;

        while (!colaProcesos.isEmpty() || procs != null) {

            if (procs == null) {
                procs = colaProcesos.poll();
                tiempoRestanteQuantum = quantum;
            }

            ejecucion.add(procs.nombreProceso);
            procs.rafaga -= 1;
            tiempoEjecucion++;
            tiempoRestanteQuantum--;

            if (procs.rafaga == 0) {
                procs = null;
            } else if (tiempoRestanteQuantum == 0) {
                colaProcesos.add(procs);
                procs = null;
            }

            for (proceso p : listaProcesos) {
                if (p.tiempoLlegada == tiempoEjecucion) {
                    colaProcesos.add(p);
                }
            }
        }

    }

    // Método para calcular el Ratio de Respuesta
    public double ResponseRatio(proceso p, int tiempoActual) {
        int tiempoEspera = tiempoActual - p.tiempoLlegada;
        double RR = (tiempoEspera + p.rafaga) / p.rafaga;
        return RR;
    }

    public void HRRN() {
        ArrayList<proceso> procesosListos = new ArrayList<>(listaProcesos); // Lista de procesos por ejecutar
        int tiempoEjecucion = 0; // Tiempo actual en el que inicia la ejecución

        while (!procesosListos.isEmpty()) {
            // Calcular el ratio de respuesta para cada proceso que ha llegado
            proceso siguienteProceso = null;
            double mayorRatio = -1; // Para encontrar el mayor ratio

            for (proceso p : procesosListos) {
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
                    ejecucion.add(siguienteProceso.nombreProceso); // Registro de la ejecución
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
