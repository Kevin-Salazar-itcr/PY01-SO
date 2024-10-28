/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author ksala
 */
public class LoadFile {
    private static LoadFile l;
    public ArrayList<Process> procesos = new ArrayList<>();
    public ArrayList<ArrayList<String>> codigos = new ArrayList<>();
    private LoadFile(){
        
    }
    public static LoadFile call(){
        if(l==null){
            l = new LoadFile();
        }
        return l;
    }
    
    public void limpiar(){
        l = new LoadFile();
    }
    
    public void openFiles(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setDialogTitle("Open source files");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Assembly Files (*.asm)", "asm"));

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            for (File file : fileChooser.getSelectedFiles()) {
                try {
                    String content = new String(Files.readAllBytes(file.toPath()));
                    if (!SyntaxManager.getInstance(content).verifyInstructions()) {
                        JOptionPane.showMessageDialog(null, "Invalid syntax detected in file: " + file.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    codigos.add(SyntaxManager.getInstance().getBinaryInstructions());
                    Logic.Process p = new Process(content);
                    procesos.add(p);

                } catch (IOException e) {
                }
            }
        }
    }
}
