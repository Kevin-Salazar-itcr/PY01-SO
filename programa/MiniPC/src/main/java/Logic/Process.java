/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author ksala
 */
public class Process {
    private ArrayList<String> fileContent;
    public PCB ownPCB;
    
    public Process(String file){
        setFileContent(new ArrayList<>(Arrays.asList(file.split("\n"))));
        this.ownPCB = new PCB(State.READY, getFileContent().size());
    }
    
    public final ArrayList<String> getFileContent(){
        return fileContent;
    }
    
    public final void setFileContent(ArrayList<String> fileContent){
        this.fileContent = fileContent;
    }
    
    public void reset(String Instruction){
        this.ownPCB.setPC(ownPCB.getDirBase());
        ownPCB.setIR(Instruction);
        ownPCB.cleanRegisters();
    }
    
    @Override
    public String toString() {
        return "Process{" + "fileContent=" + fileContent + ", ownPCB=" + ownPCB + '}';
    }   
}
