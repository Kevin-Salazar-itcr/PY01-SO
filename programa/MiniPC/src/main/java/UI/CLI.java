/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
/**
 *
 * @author ksala
 */
public class CLI {
    private static JTextArea terminal;
    private static int promptIndex;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Terminal");
        terminal = new JTextArea("> ");
        promptIndex = terminal.getText().length();

        terminal.setFont(new Font("Monospaced", Font.PLAIN, 14));
        terminal.setBackground(Color.BLACK);
        terminal.setForeground(Color.WHITE);
        terminal.setCaretColor(Color.WHITE);

        terminal.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    String command = terminal.getText().substring(promptIndex).trim();
                    terminal.append("\nComando ingresado: " + command + "\n> ");
                    promptIndex = terminal.getText().length();
                } 
                else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE) {
                    if (terminal.getCaretPosition() <= promptIndex) {
                        e.consume();
                    }
                } 
                else if (terminal.getCaretPosition() < promptIndex) {
                    terminal.setCaretPosition(terminal.getText().length());
                }
            }
        });

        frame.add(new JScrollPane(terminal));
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setVisible(true);
    }
}

