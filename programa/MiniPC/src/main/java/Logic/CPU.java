/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
/**
 *
 * @author ksala
 */
public final class CPU {
    private ArrayList<Process> processTable;
    private HashMap<Integer, String> memoryTable;
    private int sysSpace;
    private int usrSpace;
    public int currentProcess;
    public int limInf = 0;
    public int limSup = 0;

    public CPU(){
        this.processTable = new ArrayList<>();
        this.memoryTable = new HashMap<>();
        this.sysSpace = 10;
        this.usrSpace = 10;
        this.currentProcess = -1;
        configureMemory();
    }

    /*Setters, Getters*/
    public ArrayList<Process> getProcessTable() {
        return processTable;
    }

    public void setProcessTable(ArrayList<Process> processTable) {
        this.processTable = processTable;
    }

    public HashMap<Integer, String> getMemoryTable() {
        return memoryTable;
    }

    public void setMemoryTable(HashMap<Integer, String> memoryTable) {
        this.memoryTable = memoryTable;
    }

    public int getSysSpace() {
        return sysSpace;
    }

    public void setSysSpace(int sysSpace) {
        this.sysSpace = sysSpace;
    }

    public int getUsrSpace() {
        return usrSpace;
    }
    
    public void editHashMap(int key, String value){
        this.memoryTable.put(key, value);
    }

    public void setUsrSpace(int usrSpace) {
        this.usrSpace = usrSpace;
    }
    
    /**
     * Fills the memory with nullptr
     */
    public void configureMemory(){
        int n = getUsrSpace()+getSysSpace();
        this.memoryTable.clear();
        for (int i = 0; i < n; i++) {
            this.memoryTable.put(i, "NULL");
        }
    }
    
    /**
     * Returns the memory table as a String
     * 
     * @return String
     */
    public String formattingMemory() {
        StringBuilder sb = new StringBuilder();
        for (Integer key : memoryTable.keySet()) {
            sb.append(key).append(":\t").append(memoryTable.get(key)).append("\n");
        }
        return sb.toString();
    }

    //suits a random number to position between 2 limits
    public static int generatePosition(int c, int lowerBound, int upperBound) {
        int minPosition = lowerBound;
        int maxPosition = upperBound - c;
        
        if (c > (upperBound - lowerBound)) {
            throw new IllegalArgumentException("index out of range.");
        }

        Random random = new Random();
        return random.nextInt(maxPosition - minPosition + 1) + minPosition;
    }
    
    /**
     * Adds a process reference to the process table
     * @param p 
     */ 
    public void addProcess(Process p){
        ArrayList<String> l = SyntaxManager.getInstance().getBinaryInstructions();
        
        //setting UserMemory
        int pos = generatePosition(SyntaxManager.getInstance().getValues().size(), this.getSysSpace(), this.getUsrSpace() + getSysSpace());
        System.out.println("posRandom: "+pos);
        SyntaxManager.getInstance().replaceValuesWithMap(pos);
        int count=pos;
        
        for(int val:SyntaxManager.getInstance().getValues()){
            editHashMap(count, String.valueOf(val));
            count++;
        }
                
        pos = generatePosition(p.getFileContent().size(), 0, getSysSpace());
        count = pos;        
        this.limInf = pos;
        //setting System memory
        for(String x: SyntaxManager.getInstance().getBinaryInstructions()){
            editHashMap(count, x);
            this.limSup = count;
            count++;
        }
        p.ownPCB.setPC(pos);
        p.ownPCB.setPCStart(pos);
        p.ownPCB.setIR(this.memoryTable.get(pos));
        this.processTable.add(p);
        currentProcess++;
    }
    
    /**
     * Resets the memory to the original state
     */
    public void resetMemory(){
        setSysSpace(10);
        setUsrSpace(10);
        configureMemory();
        setProcessTable(new ArrayList<Process>());
        currentProcess = 0;
    }
    
    public void resetMemory(int sys, int usr){
        setSysSpace(sys);
        setUsrSpace(usr);
        configureMemory();
        setProcessTable(new ArrayList<Process>());
        currentProcess = -1;
    }
    
    /**
     * Runs the process in queue 
     * @return the current process
     */
    public Process run(){
        processTable.get(currentProcess).update(State.RUNNING);
        execute(this.memoryTable.get(processTable.get(currentProcess).ownPCB.getPC()));
        
        return processTable.get(currentProcess);
    }
    
    public Process reset(){
        processTable.get(currentProcess).update(State.READY);
        processTable.get(currentProcess).reset(this.memoryTable.get(processTable.get(currentProcess).ownPCB.getPCStart()));
        return processTable.get(currentProcess);
    }
    
    public void finish(){
        processTable.get(currentProcess).update(State.FINISHED);
    }
    
    public void execute(String instruction){
        System.out.println("IR->"+instruction);
        String[] set = instruction.split(" ");
        int value = 0;
        try{
            String valueInTable = this.memoryTable.get(Integer.valueOf(set[2]));
            value = Integer.parseInt(valueInTable);
        }
        catch(Exception e){
            //some instructions doesn´t have a value at end
        }
        System.out.println("valor final: "+value);
        processTable.get(currentProcess).executeInstruction(set[0], set[1], value);
    }
    
    public Process forwardStep(){
        String instruction = this.memoryTable.get(processTable.get(currentProcess).ownPCB.getPC()+1);
        processTable.get(currentProcess).next(instruction);
        
        execute(instruction);
        
        System.out.println(processTable.get(currentProcess));
        return processTable.get(currentProcess);
    }
    
    @Deprecated 
    public Process backwardStep(){
        processTable.get(currentProcess).previous(this.memoryTable.get(processTable.get(currentProcess).ownPCB.getPC()-1));
        return processTable.get(currentProcess);
    }
}
