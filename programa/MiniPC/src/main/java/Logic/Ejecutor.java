/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import java.util.Arrays;
import java.util.Stack;

/**
 *
 * @author ksala
 */
public class Ejecutor {

    private Stack<Integer> stack = new Stack<>();
    public Logic.Process p = null;
    private int zeroFlag;
    private int signFlag;
    public Ejecutor(){
        
    }
    public static String[] parseParamString(String str) {
        String[] parts = str.split(" ", 2);
        String[] numbers = parts[1].split(",\\s*");
        String[] result = new String[numbers.length + 1];
        result[0] = parts[0]; // Add "param"
        System.arraycopy(numbers, 0, result, 1, numbers.length); // Add numbers
        
        return result;
    }
    
    public void setProcess(Logic.Process p){
        this.p = p;
    }
    
    public Logic.Process getProcess(){
        execute();
        return p;
    }
    
    //prepares the execution
    public void execute(){
        String[] set = p.ownPCB.getIR().split(" ");
        
        switch (set[0]) {
            case "0000" -> { // load
                p.ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            }
            case "0001" -> { // store
                p.ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            }
            case "0010" -> { // add
                p.ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            }
            case "0011" -> { // sub
                p.ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            }
            case "0100" -> { // mov
                p.ownPCB.setBurst(1);
                if(set[2].matches("^(0000|0001|0010|0011)$")){
                    executeInstruction(set[0], set[1]+","+set[2], 0);
                }else{
                    executeInstruction(set[0], set[1], Integer.parseInt(set[2]));
                }
            }
            case "0101" -> { // inc
                p.ownPCB.setBurst(1);
                p.ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            }
            case "0110" -> { // dec
                p.ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            }
            case "0111" -> { // swap
                p.ownPCB.setBurst(2);
                executeInstruction(set[0], set[1]+","+set[2], 0);
            }
            case "1000" -> { //int
                p.ownPCB.setBurst(2);
                executeInstruction(set[0], set[1]+","+set[2], 0);
            }
            case "1001" -> { //cmp
                p.ownPCB.setBurst(1);
                executeInstruction(set[0], set[1]+","+set[2], 0);
            }            
            case "1010" -> { //jumps
                p.ownPCB.setBurst(2);
                executeInstruction(set[0], set[1], Integer.parseInt(set[2]));
            }         
            case "1011" -> { //param
                p.ownPCB.setBurst(3);
                String[] arr = parseParamString(p.ownPCB.getIR());
                String numbers = String.join(",", Arrays.copyOfRange(arr, 1, arr.length));
                executeInstruction(arr[0], numbers, 0);
            
            }
            case "1100" -> { //push
                p.ownPCB.setBurst(1);
                executeInstruction(set[0], set[1], 0);
            } 
            case "1101" -> { //pop
                p.ownPCB.setBurst(1);
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
            case "0000" -> p.ownPCB.getAX();
            case "0001" -> p.ownPCB.getBX();
            case "0010" -> p.ownPCB.getCX();
            case "0011" -> p.ownPCB.getDX();
            default -> 0;
        };

        switch (instruction) {
            case "0000" -> { // load
                p.ownPCB.setAC(registerValue);
            }
            case "0001" -> { // store
                setRegisterValue(register, p.ownPCB.getAC());
            }
            case "0010" -> { // add
                p.ownPCB.setAC(p.ownPCB.getAC() + registerValue);
            }
            case "0011" -> { // sub
                p.ownPCB.setAC(p.ownPCB.getAC() - registerValue);
            }
            case "0100" -> { // mov
                String[] regs = register.split(",");
                if(regs.length==2){
                    registerValue = switch (regs[1]) {
                        case "0000" -> p.ownPCB.getAX();
                        case "0001" -> p.ownPCB.getBX();
                        case "0010" -> p.ownPCB.getCX();
                        case "0011" -> p.ownPCB.getDX();
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
                    p.ownPCB.setAC(p.ownPCB.getAC() + 1);
                } else {
                    setRegisterValue(register, registerValue + 1);
                }
            }
            case "0110" -> { // dec
                if (register.equals("0111")) { // AC
                    p.ownPCB.setAC(p.ownPCB.getAC() - 1);
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
                this.zeroFlag = (res == 0? 1 : 0);
                this.signFlag = (res < 0? 1 : 0);
            }
            case "1010" -> { //jumps
                int startScope = p.ownPCB.getDirBase();
                int endScope = p.ownPCB.getDirEnd();
                int valueJump = p.ownPCB.getPC()+value;
                //validating previous to jump
                if(endScope < valueJump || startScope > valueJump){
                    p.ownPCB.setPC(endScope);
                    return;
                }
                valueJump--;
                switch(register){ //in this case, register is jumpType
                    case "0000" -> { //JMP
                        p.ownPCB.setPC(valueJump);
                        break;
                    } 
                    case "0001" -> { //JG
                        //sf = 1 ? reg2 is greater
                        p.ownPCB.setPC(this.signFlag ==1 ? p.ownPCB.getPC() : valueJump);
                        break;
                    } 
                    case "0010" -> { //JL
                        //sf = 0 ? reg1 is greater
                        p.ownPCB.setPC(this.signFlag ==0 ? p.ownPCB.getPC() : valueJump);
                        break;
                    }
                    case "0011" -> { //JGE
                        p.ownPCB.setPC((this.signFlag ==1 || this.zeroFlag ==1) ? p.ownPCB.getPC() : valueJump);
                        break;
                    }
                    case "0100" -> { //JLE
                        p.ownPCB.setPC((this.signFlag ==0 || this.zeroFlag ==1) ? p.ownPCB.getPC() : valueJump);
                        break;
                    }
                    case "0101" -> { //JE
                        //zf = 1? equals
                        p.ownPCB.setPC(this.zeroFlag ==0 ? p.ownPCB.getPC() : valueJump);
                        break;
                    }
                    case "0110" -> { //JNE
                        //zf = 0? no equals
                        p.ownPCB.setPC(this.signFlag ==1 ? p.ownPCB.getPC() : valueJump);
                        break;
                    } 
                    default -> {break;}
                }
            }
            case "1011" -> { //param
                String[] values = register.split(" ");
                if(this.stack.size()+values.length > 5){
                    break;
                }
                int i = 0;
                while (i<values.length){
                   this.stack.push(Integer.valueOf(values[i++]));
                }
            }
            case "1100" -> { //push
                if(this.stack.size() == 5){
                    break;
                }
                int regValue = getRegisterValue(register);
                this.stack.push(regValue);
            }
            case "1101" -> { //pop
                if(this.stack.isEmpty()){
                    break;
                }
                int popValue = this.stack.pop();
                setRegisterValue(register, popValue);
            }
            
            default -> {break;}
        }   
        
    }

    private void setRegisterValue(String register, int value) {
        switch (register) {
            case "0000" -> p.ownPCB.setAX(value);
            case "0001" -> p.ownPCB.setBX(value);
            case "0010" -> p.ownPCB.setCX(value);
            case "0011" -> p.ownPCB.setDX(value);
            default -> p.ownPCB.setAC(value);
        }
    }

    private int getRegisterValue(String register) {
        return switch (register) {
            case "0000" -> p.ownPCB.getAX();
            case "0001" -> p.ownPCB.getBX();
            case "0010" -> p.ownPCB.getCX();
            case "0011" -> p.ownPCB.getDX();
            default -> p.ownPCB.getAC();
        };
    }
    
}
