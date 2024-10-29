package UI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class Gantt extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private int indice = 0;
    private ArrayList<String> tareas;
    private ArrayList<String> valores;
    private JScrollPane scrollPane;

    public Gantt(ArrayList<String> tareas, ArrayList<String> valores) {
        this.tareas = tareas;
        this.valores = valores;

        // Modelo de tabla con celdas no editables
        model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Ninguna celda es editable
            }
        };

        model.addColumn("Tarea");
        valores.forEach(valor -> model.addRow(new Object[] { valor })); // Agregar filas con valores iniciales

        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDefaultRenderer(Object.class, (tbl, value, isSelected, hasFocus, row, column) -> {
            JLabel cell = new JLabel(value == null ? "" : value.toString(), JLabel.CENTER);
            cell.setOpaque(true);
            cell.setBackground("█".equals(value) ? Color.BLUE : Color.WHITE);
            return cell;
        });

        scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        JButton mostrarBtn = new JButton("Mostrar");
        mostrarBtn.addActionListener(e -> agregarColumna());

        add(scrollPane, BorderLayout.CENTER);
        add(mostrarBtn, BorderLayout.SOUTH);
        setSize(800, 200);
        setVisible(true);
    }

    private void agregarColumna() {
        if (indice < tareas.size()) {
            model.addColumn(String.valueOf(indice + 1));
            String tarea = tareas.get(indice);
            for (int i = 0; i < valores.size(); i++) {
                if (valores.get(i).equals(tarea)) {
                    model.setValueAt("█", i, model.getColumnCount() - 1);
                    break;
                }
            }
            indice++;
            scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getMaximum());
        }
    }
}