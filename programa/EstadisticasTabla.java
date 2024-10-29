import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class EstadisticasTabla extends JFrame {
    public EstadisticasTabla(ArrayList<ArrayList<Double>> estadisticas) {
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

    public static void main(String[] args) {

        // Añadir algunos datos de ejemplo
        ArrayList<ArrayList<Double>> estadisticas = new ArrayList<>();

        ArrayList<Double> datos1 = new ArrayList<>();
        datos1.add(1.0);
        datos1.add(0.0);
        datos1.add(3.0);
        datos1.add(3.0);
        datos1.add(3.0);
        datos1.add(1.0);
        estadisticas.add(datos1);

        ArrayList<Double> datos2 = new ArrayList<>();
        datos2.add(2.0);
        datos2.add(2.0);
        datos2.add(9.0);
        datos2.add(6.0);
        datos2.add(7.0);
        datos2.add(1.17);
        estadisticas.add(datos2);

        ArrayList<Double> datos3 = new ArrayList<>();
        datos3.add(3.0);
        datos3.add(4.0);
        datos3.add(13.0);
        datos3.add(4.0);
        datos3.add(9.0);
        datos3.add(2.25);
        estadisticas.add(datos3);

        ArrayList<Double> datos4 = new ArrayList<>();
        datos4.add(4.0);
        datos4.add(6.0);
        datos4.add(18.0);
        datos4.add(5.0);
        datos4.add(12.0);
        datos4.add(2.4);
        estadisticas.add(datos4);

        ArrayList<Double> datos5 = new ArrayList<>();
        datos5.add(5.0);
        datos5.add(8.0);
        datos5.add(20.0);
        datos5.add(2.0);
        datos5.add(12.0);
        datos5.add(6.0);
        estadisticas.add(datos5);

        new EstadisticasTabla(estadisticas);
    }
}
