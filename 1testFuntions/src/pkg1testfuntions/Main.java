/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package pkg1testfuntions;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 *
 * @author Innovation Computers
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        
        
        
        
        proceso p1 = new proceso("Proceso 1", 4, 0); // Proceso con ráfaga de 4 y llega en el tiempo 0
        proceso p2 = new proceso("Proceso 2", 2, 0); // Proceso con ráfaga de 2 y llega en el tiempo 1
        proceso p3 = new proceso("Proceso 3", 1, 2); // Proceso con ráfaga de 1 y llega en el tiempo 2
        proceso p4 = new proceso("Proceso 4", 5, 3); // Proceso con ráfaga de 5 y llega en el tiempo 3
        proceso p5 = new proceso("Proceso 5", 3, 4); // Proceso con ráfaga de 3 y llega en el tiempo 4

        CPU prueba = new CPU(); 
        
        prueba.listaProcesos.add(p1);
        prueba.listaProcesos.add(p2);
        prueba.listaProcesos.add(p3);
        prueba.listaProcesos.add(p4);
        prueba.listaProcesos.add(p5);
        
        
        
        prueba.HRRN();
        System.out.println(prueba.toString());
        
        JFrame frame = new JFrame("Diagrama de Gantt");
        GanttChartPanel panel = new GanttChartPanel(prueba);
        frame.add(new JScrollPane(panel));
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
                             
    }
    
}
