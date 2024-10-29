package UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import Logic.MemoryParser;
import java.util.ArrayList;

public class Gantt extends JPanel {

    private MemoryParser cpu;
    private int timeUnitWidth = 50; // Ancho de cada unidad de tiempo
    private int processHeight = 30; // Altura de cada proceso
    private int currentStep = 0; // Paso actual para mostrar la ejecución paso a paso

    public Gantt(MemoryParser cpu) {
        this.cpu = cpu;
        
        // Calcula el tamaño dinámico basado en la cantidad de ejecuciones y procesos
        setPreferredSize(new Dimension(800, 2000));
        
        // Botón de avance de paso
        JButton stepButton = new JButton("Step");
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentStep < cpu.ejecucion.size()) {
                    currentStep++;
                    repaint();
                }
            }
        });
        this.add(stepButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int tiempoTotal = cpu.ejecucion.size();
        
        // Dibujar el encabezado de tiempo
        g.drawString("Tiempo", 10, 20);
        for (int i = 0; i < tiempoTotal; i++) {
            int x = 70 + i * timeUnitWidth;
            g.drawString("" + (i + 1), x, 20);
        }

        // Dibujar los procesos
        for (int i = 0; i < cpu.listaProcesos.size(); i++) {
            Logic.Process p = cpu.listaProcesos.get(i);
            g.drawString(String.valueOf(p.ownPCB.id), 10, (i + 2) * processHeight);
        }

        // Dibujar la ejecución de cada proceso hasta el paso actual
        for (int tiempo = 0; tiempo < currentStep; tiempo++) {
            
            String procesoNombre = cpu.ejecucion.get(tiempo);
            for (int i = 0; i < cpu.listaProcesos.size(); i++) {
                if (String.valueOf(cpu.listaProcesos.get(i).ownPCB.id).equals(procesoNombre)) {
                    int x = 70 + tiempo * timeUnitWidth;
                    int y = (i + 1) * processHeight;
                    g.setColor(Color.GREEN);
                    g.fillRect(x, y + 10, timeUnitWidth, processHeight - 10);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y + 10, timeUnitWidth, processHeight - 10);
                }
            }
        }
    }
}
