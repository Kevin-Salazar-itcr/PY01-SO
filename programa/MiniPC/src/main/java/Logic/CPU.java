/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;
/**
 *
 * @author ksala
 */
public final class CPU {
    private ArrayList<Process> processTable;
    private Process[] mainProcesses = new Process[5];
    private HashMap<Integer, String> memoryTable;
    private int sysSpace;
    private int usrSpace;
    public int currentProcess;
    private int AX;
    private int BX;
    private int CX;
    private int DX;
    private int PC;
    private int AC;
    private String IR;
    private int zeroFlag;
    private int signFlag;
    private Stack<Integer> stack;
    
    public int limInf = 0;
    public int limSup = 0;

    public CPU(){
        this.processTable = new ArrayList<>();
        this.memoryTable = new HashMap<>();
        this.sysSpace = 10;
        this.usrSpace = 10;
        this.currentProcess = 0;
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

    public int getAX() {
        return AX;
    }

    public void setAX(int AX) {
        this.AX = AX;
    }

    public int getBX() {
        return BX;
    }

    public void setBX(int BX) {
        this.BX = BX;
    }

    public int getCX() {
        return CX;
    }

    public void setCX(int CX) {
        this.CX = CX;
    }

    public int getDX() {
        return DX;
    }

    public void setDX(int DX) {
        this.DX = DX;
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public int getAC() {
        return AC;
    }

    public void setAC(int AC) {
        this.AC = AC;
    }

    public String getIR() {
        return IR;
    }

    public void setIR(String IR) {
        this.IR = IR;
    }

    public int getZeroFlag() {
        return zeroFlag;
    }

    public void setZeroFlag(int zeroFlag) {
        this.zeroFlag = zeroFlag;
    }

    public int getSignFlag() {
        return signFlag;
    }

    public void setSignFlag(int signFlag) {
        this.signFlag = signFlag;
    }
    
    //----------------------------------------------------------------
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
         
        int pos = generatePosition(p.getFileContent().size(), 0, getSysSpace());
        int count = pos;        
        this.limInf = pos;
        //setting System memory
        for(String x: SyntaxManager.getInstance().getBinaryInstructions()){
            editHashMap(count, x);
            this.limSup = count;
            count++;
        }
        p.ownPCB.setPC(pos);
        p.ownPCB.setDirBase(pos);
        p.ownPCB.setDirEnd(p.getFileContent().size()+pos-1);
        p.ownPCB.setIR(this.memoryTable.get(pos));
        this.processTable.add(p);
    }
    
    //returns current process (the current is whoever that isn't finished yet in the queue)
    public Process getCurrentProcess(){
        for(Process p: processTable){
            if (p.ownPCB.getState() != State.FINISHED){
                return p;
            }
        }
        return null;
    }
    
    //updates the values in current process
    public void updateProcess(State state) {
        getCurrentProcess().ownPCB.setState(state);
        getCurrentProcess().ownPCB.setAX(getAX());
        getCurrentProcess().ownPCB.setBX(getBX());
        getCurrentProcess().ownPCB.setCX(getCX());
        getCurrentProcess().ownPCB.setDX(getDX());
        getCurrentProcess().ownPCB.setPC(getPC());
        getCurrentProcess().ownPCB.setAC(getAC());
        getCurrentProcess().ownPCB.setIR(getIR());
    }

    public void changeContext(State state) {
        getCurrentProcess().ownPCB.setState(state);
        if(getCurrentProcess()!=null){
            getCurrentProcess().ownPCB.setAX(getAX());
            getCurrentProcess().ownPCB.setBX(getBX());
            getCurrentProcess().ownPCB.setCX(getCX());
            getCurrentProcess().ownPCB.setDX(getDX());
            getCurrentProcess().ownPCB.setPC(getPC());
            getCurrentProcess().ownPCB.setAC(getAC());
            getCurrentProcess().ownPCB.setIR(getIR());
        }
        setAX(0);
        setBX(0);
        setCX(0);
        setDX(0);
        setPC(0);
        setAC(0);
        setIR("");
    }

    /**
     * Resets the memory to the original state
     */
    public void resetMemory(){
        setSysSpace(10);
        setUsrSpace(10);
        configureMemory();
        setProcessTable(new ArrayList<>());
        currentProcess = 0;
    }
    
    public void resetMemory(int sys, int usr){
        setSysSpace(sys);
        setUsrSpace(usr);
        configureMemory();
        setProcessTable(new ArrayList<>());
        currentProcess = 0;
    }
    
    /**
     * Runs the process in queue 
     * @return the current process
     */
    public Process run(){
        setPC(getCurrentProcess().ownPCB.getPC());
        setIR(this.memoryTable.get(getCurrentProcess().ownPCB.getPC()));
        execute();
        return getCurrentProcess();
    }
    
    public Process reset(){
        setAX(0);
        setBX(0);
        setCX(0);
        setDX(0);
        setPC(getCurrentProcess().ownPCB.getDirBase());
        setAC(0);
        String instruction = this.memoryTable.get(getCurrentProcess().ownPCB.getDirBase());
        setIR(instruction);
        
        updateProcess(State.READY);
        
        return getCurrentProcess();
    }
    
    public void finish(){
        changeContext(State.FINISHED);
        
    }
    
    //listens a value from terminal
    public void listen(int value){
        setDX(value);
        getCurrentProcess().ownPCB.setDX(value);
    }
    
    public static String[] parseParamString(String str) {
        String[] parts = str.split(" ", 2);
        String[] numbers = parts[1].split(",\\s*");
        String[] result = new String[numbers.length + 1];
        result[0] = parts[0]; // Add "param"
        System.arraycopy(numbers, 0, result, 1, numbers.length); // Add numbers
        
        return result;
    }
    
    //prepares the execution
    public void execute(){
        String[] set = getIR().split(" ");
        System.out.println(getIR());
        
        switch (set[0]) {
            case "0000" -> { // load
                executeInstruction(set[0], set[1], 0);
            }
            case "0001" -> { // store
                executeInstruction(set[0], set[1], 0);
            }
            case "0010" -> { // add
                executeInstruction(set[0], set[1], 0);
            }
            case "0011" -> { // sub
                executeInstruction(set[0], set[1], 0);
            }
            case "0100" -> { // mov
                if(set[2].matches("^(0000|0001|0010|0011)$")){
                    executeInstruction(set[0], set[1]+","+set[2], 0);
                }else{
                    executeInstruction(set[0], set[1], Integer.parseInt(set[2]));
                }
            }
            case "0101" -> { // inc
                executeInstruction(set[0], set[1], 0);
            }
            case "0110" -> { // dec
                executeInstruction(set[0], set[1], 0);
            }
            case "0111" -> { // swap
                executeInstruction(set[0], set[1]+","+set[2], 0);
            }
            case "1000" -> { //int
                executeInstruction(set[0], set[1]+","+set[2], 0);
            }
            case "1001" -> { //cmp
                executeInstruction(set[0], set[1]+","+set[2], 0);
            }
            
            case "1010" -> { //jumps
                executeInstruction(set[0], set[1], Integer.parseInt(set[2]));
            }
            
            case "1011" -> { //param
                String[] arr = parseParamString(getIR());
                String numbers = String.join(",", Arrays.copyOfRange(arr, 1, arr.length));
                executeInstruction(arr[0], numbers, 0);
            
            }
            case "1100" -> { //push
                executeInstruction(set[0], set[1], 0);
            } 
            case "1101" -> { //pop
                executeInstruction(set[0], set[1], 0);
            }
            
            default -> {break;}
            
            
            
        }
    }
    
    /**
     * Executes an instruccion of the current process
     * @param instruction the instruction to receive
     * @param register the register(s) to modify
     * @param value optional value, it depends of the executed instruction if it's needed
     */
    public void executeInstruction(String instruction, String register, int value) {
        State state = State.RUNNING;
        int registerValue = switch (register) {
            case "0000" -> getAX();
            case "0001" -> getBX();
            case "0010" -> getCX();
            case "0011" -> getDX();
            default -> 0;
        };

        switch (instruction) {
            case "0000" -> { // load
                setAC(registerValue);
            }
            case "0001" -> { // store
                setRegisterValue(register, getAC());
            }
            case "0010" -> { // add
                setAC(getAC() + registerValue);
            }
            case "0011" -> { // sub
                setAC(getAC() - registerValue);
            }
            case "0100" -> { // mov
                String[] regs = register.split(",");
                if(regs.length==2){
                    registerValue = switch (regs[1]) {
                        case "0000" -> getAX();
                        case "0001" -> getBX();
                        case "0010" -> getCX();
                        case "0011" -> getDX();
                        default -> 0;
                    };
                    setRegisterValue(regs[0], registerValue);
                }
                else{
                    setRegisterValue(register, value);
                }
            }
            case "0101" -> { // inc
                if (register.equals("0111")) { // AC
                    setAC(getAC() + 1);
                } else {
                    setRegisterValue(register, registerValue + 1);
                }
            }
            case "0110" -> { // dec
                if (register.equals("0111")) { // AC
                    setAC(getAC() - 1);
                } else {
                    setRegisterValue(register, registerValue - 1);
                }
            }
            case "0111" -> { // swap
                // Extract both registers from the input format
                String[] regs = register.split(",");
                int reg1Value = getRegisterValue(regs[0]);
                int reg2Value = getRegisterValue(regs[1]);

                // Swap values
                setRegisterValue(regs[0], reg2Value);
                setRegisterValue(regs[1], reg1Value);
            }
            case "1000" -> { //int
                System.out.println("interruption in progress");
                state = State.BLOCKED;
            }
            case "1001" -> { //cmp
                String[] regs = register.split(",");
                int reg1Value = getRegisterValue(regs[0]);
                int reg2Value = getRegisterValue(regs[1]);
                
                int res = reg2Value - reg1Value;
                this.setZeroFlag(res == 0? 1 : 0);
                this.setSignFlag(res < 0? 1 : 0);
            }
            
            case "1010" -> { //jumps
                int startScope = getCurrentProcess().ownPCB.getDirBase();
                int endScope = getCurrentProcess().ownPCB.getDirEnd();
                int valueJump = getPC()+value;
                //validating previous to jump
                if(endScope < valueJump || startScope > valueJump){
                    setPC(endScope);
                    return;
                }
                valueJump--;
                switch(register){ //in this case, register is jumpType
                    case "0000" -> { //JMP
                        setPC(valueJump);
                        break;
                    } 
                    case "0001" -> { //JG
                        //sf = 1 ? reg2 is greater
                        setPC(this.getSignFlag()==1 ? getPC() : valueJump);
                        break;
                    } 
                    case "0010" -> { //JL
                        //sf = 0 ? reg1 is greater
                        setPC(this.getSignFlag()==0 ? getPC() : valueJump);
                        break;
                    }
                    case "0011" -> { //JGE
                        setPC((this.getSignFlag()==1 || this.getZeroFlag()==1) ? getPC() : valueJump);
                        break;
                    }
                    case "0100" -> { //JLE
                        setPC((this.getSignFlag()==0 || this.getZeroFlag()==1) ? getPC() : valueJump);
                        break;
                    }
                    case "0101" -> { //JE
                        //zf = 1? equals
                        setPC(this.getZeroFlag()==0 ? getPC() : valueJump);
                        break;
                    }
                    case "0110" -> { //JNE
                        //zf = 0? no equals
                        setPC(this.getSignFlag()==1 ? getPC() : valueJump);
                        break;
                    } 
                    default -> {break;}
                }
            }
            
            case "1011" -> { //param
                String[] values = register.split(",");
                if(getCurrentProcess().ownPCB.getStack().size()+values.length > 5){
                    break;
                }
                int i = 0;
                while (i<values.length){
                    getCurrentProcess().ownPCB.getStack().push(Integer.valueOf(values[i++]));
                }
            }
            
            case "1100" -> { //push
                if(getCurrentProcess().ownPCB.getStack().size() == 5){
                    break;
                }
                int regValue = getRegisterValue(register);
                getCurrentProcess().ownPCB.getStack().push(regValue);
            }
            
            case "1101" -> { //pop
                if(getCurrentProcess().ownPCB.getStack().isEmpty()){
                    break;
                }
                int popValue = getCurrentProcess().ownPCB.getStack().pop();
                setRegisterValue(register, popValue);
            }
            
            default -> {break;}
        }   
        
        this.updateProcess(state);
    }

    private void setRegisterValue(String register, int value) {
        switch (register) {
            case "0000" -> setAX(value);
            case "0001" -> setBX(value);
            case "0010" -> setCX(value);
            case "0011" -> setDX(value);
            default -> setAC(value);
        }
    }

    private int getRegisterValue(String register) {
        return switch (register) {
            case "0000" -> getAX();
            case "0001" -> getBX();
            case "0010" -> getCX();
            case "0011" -> getDX();
            default -> getAC();
        };
    }
    
    public Process forwardStep(){
        setPC(getPC()+1);
        setIR(this.memoryTable.get(getPC()));
        execute();
        
        return getCurrentProcess();
    }
}