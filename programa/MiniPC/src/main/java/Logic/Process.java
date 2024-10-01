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
    public long startTime ;
    public long finisTime;
    public String startHour;
    public String finsHour;
    public long totalTime = 0; 

    public PCB ownPCB;
    
    public Process(String file){
        setFileContent(new ArrayList<>(Arrays.asList(file.split("\n"))));
        this.ownPCB = new PCB(State.NEW, getFileContent().size());
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
    
    public String code(){
        
        StringBuilder sb = new StringBuilder();
        for (String x : fileContent) {
            sb.append(x).append("\n");
        }
        return sb.toString();
    
    }
    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public void setFinsHour(String finsHour) {
        this.finsHour = finsHour;
    }
    
    public void setTotalTime() {
        this.totalTime =((finisTime - startTime) / 1000);
    }
    
    @Override
    public String toString() {
        return "Process{" + "fileContent=" + fileContent + ", ownPCB=" + ownPCB + '}';
    }   
}
