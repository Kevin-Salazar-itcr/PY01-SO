/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.TreeMap;
import java.util.Properties;
import java.util.Random;
import java.util.Stack;
/**
 *
 * @author ksala
 */
public final class CPU {
    private ArrayList<Process> waitingProcesses;
    private ArrayList<Process> processScheduler;
    private ArrayList<Process> disc;
    private TreeMap<Integer, String> ram;
    public int currentProcess;
    
    public int ramSize;
    public int discSize;
    public int virtualDiscSize;
    
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
    
    public int exitCode = 0; 

    public CPU() {
        this.waitingProcesses = new ArrayList<>();
        this.processScheduler = new ArrayList<>();
        this.disc = new ArrayList<>();
        this.ram = new TreeMap<>();       
        this.currentProcess = 0;
        this.ramSize = 0;
        this.discSize = 0;
        this.virtualDiscSize = 0;
        this.AX = 0;
        this.BX = 0;
        this.CX = 0;
        this.DX = 0;
        this.PC = 0;
        this.AC = 0;
        this.IR = "";
        this.zeroFlag = 0;
        this.signFlag = 0;
        this.stack = new Stack<>(); 
        configureMemory(false);
    }

    /*Setters, Getters*/
    public ArrayList<Process> getProcessTable() {
        return waitingProcesses;
    }

    public void setWaitingProcesses(ArrayList<Process> waitingProcesses) {
        this.waitingProcesses = waitingProcesses;
    }

    public TreeMap<Integer, String> getMemoryTable() {
        return ram;
    }

    public void setMemoryTable(TreeMap<Integer, String> ram) {
        this.ram = ram;
    }

    public void editTreeMap(int key, String value){
        this.ram.put(key, value);
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

    public ArrayList<Process> getWaitingProcesses() {
        return waitingProcesses;
    }

    public ArrayList<Process> getProcessScheduler() {
        return processScheduler;
    }

    public void setProcessScheduler(ArrayList<Process> processScheduler) {
        this.processScheduler = processScheduler;
    }

    public TreeMap<Integer, String> getRam() {
        return ram;
    }

    public void setRam(TreeMap<Integer, String> ram) {
        this.ram = ram;
    }

    public Stack<Integer> getStack() {
        return stack;
    }

    public void setStack(Stack<Integer> stack) {
        this.stack = stack;
    }
    
    //----------------------------------------------------------------

    //configure default values for memory values
    public void configureMemory(boolean change){
        this.ram.clear();
        if(change){
            return;
        }
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(System.getProperty("user.dir")+"\\config.properties")) {
            properties.load(input);

            ramSize = Integer.parseInt(properties.getProperty("ram"));
            discSize = Integer.parseInt(properties.getProperty("disc"));
            virtualDiscSize = Integer.parseInt(properties.getProperty("virtual"));
            
        } catch (IOException e) {}
        
    }
    
    /**
     * Returns the memory table as a String
     * 
     * @return String
     */
    public String formattingMemory() {
        StringBuilder sb = new StringBuilder();
        for (Integer key : ram.keySet()) {
            sb.append(key).append(":\t").append(ram.get(key)).append("\n");
        }
        return sb.toString();
    }

    //suits a random number to position between 2 limits
    public int generatePosition(int c, int lowerBound, int upperBound) {
        int maxPosition = upperBound - c;
        if (c > (upperBound - lowerBound)) {
            throw new IllegalArgumentException("Index out of range.");
        }
        Random random = new Random();
        while (true) {
            int rand = random.nextInt(maxPosition - lowerBound + 1) + lowerBound;

            // Check if all 'c' spaces are free
            boolean isFree = true;
            for (int i = 0; i < c && isFree; i++) {
                if (ram.containsKey(rand + i)) {
                    isFree = false;
                }
            }

            if (isFree) {
                return rand;
            }
        }
    }
    
    /**
     * Adds a process in the process queue
     * @param p new process
     */ 
    public void newProcess(Process p){
        if(this.processScheduler.size()<5){ //only 5 processes can be executing at time
            p.ownPCB.setState(State.READY);
            addProcess(p);
        }
        else{
            this.waitingProcesses.add(p);
        }
    }
    
    /**
     * Adds a process in main memory
     * @param p 
     */
    public void addProcess(Process p){
        System.out.println(p.toString());
        int pos = generatePosition(p.getFileContent().size(), 0, this.ramSize);
        int count = pos;        
        
        //setting it in main memory
        for(String x: SyntaxManager.getInstance().getBinaryInstructions()){
            editTreeMap(count++, x);
        }
        p.ownPCB.setPC(pos);
        p.ownPCB.setDirBase(pos);
        p.ownPCB.setDirEnd(p.getFileContent().size()+pos-1);
        p.ownPCB.setIR(this.ram.get(pos));
        this.processScheduler.add(p);
    }
    
    //returns current process (the current is whoever that isn't finished yet in the queue)
    public Process getCurrentProcess(){
        for(Process p: processScheduler){
            if (p.ownPCB.getState() != State.FINISHED){
                return p;
            }
        }
        return null;
    }
    
    public void updateRAM(int start, int end){
        while (processScheduler.size() < 5 && !waitingProcesses.isEmpty()) {
            addProcess(waitingProcesses.remove(0)); 
        }
        
        for(int i = start; i<=end;i++){
            this.ram.remove(i);
        }
    }
    
    //updates the values in current process
    public void updateProcess(State state) {
        getCurrentProcess().ownPCB.setAX(getAX());
        getCurrentProcess().ownPCB.setBX(getBX());
        getCurrentProcess().ownPCB.setCX(getCX());
        getCurrentProcess().ownPCB.setDX(getDX());
        getCurrentProcess().ownPCB.setPC(getPC());
        getCurrentProcess().ownPCB.setAC(getAC());
        getCurrentProcess().ownPCB.setIR(getIR());
        getCurrentProcess().ownPCB.setStack(stack);
        Process p = getCurrentProcess();
        getCurrentProcess().ownPCB.setState(state);
        if(state == State.FINISHED){
            this.processScheduler.remove(p);
            updateRAM(p.ownPCB.getDirBase(), p.ownPCB.getDirEnd());
        }
    }

    public void changeContext(State state) {
        if(getCurrentProcess()!=null){
            updateProcess(state);            
        }
        setAX(0);
        setBX(0);
        setCX(0);
        setDX(0);
        setPC(0);
        setAC(0);
        this.setStack(new Stack<>());
        setIR("");
    }

    /**
     * Resets the memory to the original state
     */
    public void resetMemory(){
        configureMemory(false);
        setWaitingProcesses(new ArrayList<>());
        currentProcess = 0;
    }
    
    public void resetMemory(int ram, int disk, int virtual){
        this.ramSize = ram;
        this.discSize = disk;
        this.virtualDiscSize = virtual;
        configureMemory(true);
        setWaitingProcesses(new ArrayList<>());
        currentProcess = 0;
    }
    
    /**
     * Runs the process in queue 
     */
    public void run(){
        setPC(getCurrentProcess().ownPCB.getPC());
        
        long startTimeMillis = System.currentTimeMillis();
        getCurrentProcess().startTime = startTimeMillis;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        getCurrentProcess().startHour = dateFormat.format(new Date(startTimeMillis));
        
        
        setIR(this.ram.get(getCurrentProcess().ownPCB.getPC()));
        execute();
    }
    
    public Process reset(){
        setAX(0);
        setBX(0);
        setCX(0);
        setDX(0);
        setPC(getCurrentProcess().ownPCB.getDirBase());
        setAC(0);
        String instruction = this.ram.get(getCurrentProcess().ownPCB.getDirBase());
        setIR(instruction);
        
        updateProcess(State.READY);
        
        return getCurrentProcess();
    }
    
    public boolean finish(){          
        this.exitCode = 0;
        changeContext(State.FINISHED); 
        if(getCurrentProcess()!=null){
            run();
            return false;
        }
        return true;
    }
    
    //moves a process line to its last instruction
    public void killProcess(int exitcode){
        setPC(getCurrentProcess().ownPCB.getDirEnd());
        this.exitCode = exitcode;
    }
    
    //listens a value from terminal
    public void listen(int value){
        if(value>=256 || value <-256){killProcess(-1);}
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
                getCurrentProcess().ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            }
            case "0001" -> { // store
                getCurrentProcess().ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            }
            case "0010" -> { // add
                getCurrentProcess().ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            }
            case "0011" -> { // sub
                getCurrentProcess().ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            }
            case "0100" -> { // mov
                getCurrentProcess().ownPCB.setBurst(1);
                if(set[2].matches("^(0000|0001|0010|0011)$")){
                    executeInstruction(set[0], set[1]+","+set[2], 0);
                }else{
                    executeInstruction(set[0], set[1], Integer.parseInt(set[2]));
                }
            }
            case "0101" -> { // inc
                getCurrentProcess().ownPCB.setBurst(1);
                getCurrentProcess().ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            }
            case "0110" -> { // dec
                getCurrentProcess().ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            }
            case "0111" -> { // swap
                getCurrentProcess().ownPCB.setBurst(2);
                executeInstruction(set[0], set[1]+","+set[2], 0);
            }
            case "1000" -> { //int
                getCurrentProcess().ownPCB.setBurst(2);
                executeInstruction(set[0], set[1]+","+set[2], 0);
            }
            case "1001" -> { //cmp
                getCurrentProcess().ownPCB.setBurst(1);
                executeInstruction(set[0], set[1]+","+set[2], 0);
            }            
            case "1010" -> { //jumps
                getCurrentProcess().ownPCB.setBurst(2);
                executeInstruction(set[0], set[1], Integer.parseInt(set[2]));
            }         
            case "1011" -> { //param
                getCurrentProcess().ownPCB.setBurst(3);
                String[] arr = parseParamString(getIR());
                String numbers = String.join(",", Arrays.copyOfRange(arr, 1, arr.length));
                executeInstruction(arr[0], numbers, 0);
            
            }
            case "1100" -> { //push
                getCurrentProcess().ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            } 
            case "1101" -> { //pop
                getCurrentProcess().ownPCB.setBurst(1);
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
                String[] values = register.split(" ");
                if(this.stack.size()+values.length > 5){
                    killProcess(-1);
                    break;
                }
                int i = 0;
                while (i<values.length){
                   this.stack.push(Integer.valueOf(values[i++]));
                }
            }
            case "1100" -> { //push
                if(this.stack.size() == 5){
                    killProcess(-1);
                    break;
                }
                int regValue = getRegisterValue(register);
                this.stack.push(regValue);
            }
            case "1101" -> { //pop
                if(this.stack.isEmpty()){
                    killProcess(-1);
                    break;
                }
                int popValue = this.stack.pop();
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
        setIR(this.ram.get(getPC()));
        execute();
        
        return getCurrentProcess();
    }
}
