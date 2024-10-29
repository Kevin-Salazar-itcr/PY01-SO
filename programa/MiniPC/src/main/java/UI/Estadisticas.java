/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class Estadisticas extends JFrame {
    public Estadisticas(ArrayList<ArrayList<Double>> estadisticas) {
        setTitle("Estadísticas de Procesos");

        // Nombres de las columnas
        String[] columnNames = { "ID", "Arrival time", "Finish time", "Service time", "Turnaround time", "Tr/Ts" };

        // Crear modelo de tabla
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Agregar datos al modelo
        for (ArrayList<Double> datos : estadisticas) {
            Object[] row = new Object[6];
            row[0] = datos.get(0).intValue(); // ID como entero
            row[1] = datos.get(1).intValue(); // Arrival time como entero
            row[2] = datos.get(2).intValue(); // Finish time como entero
            row[3] = datos.get(3).intValue(); // Service time como entero
            row[4] = datos.get(4).intValue(); // Turnaround time como entero
            row[5] = datos.get(5); // Tr/Ts como double

            model.addRow(row);
        }

        // Crear la tabla con el modelo
        JTable table = new JTable(model);

        // Agregar la tabla en un JScrollPane para el desplazamiento
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        // Configurar el JFrame
        add(scrollPane);
        setSize(600, 250);
        setLocationRelativeTo(null); // Centrar en la pantalla
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
