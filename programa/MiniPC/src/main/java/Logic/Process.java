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
        this.ownPCB = new PCB(State.READY);
        fillFileContent(file);
    }
    
    public ArrayList<String> getFileContent(){
        return fileContent;
    }
    
    public void setFileContent(ArrayList<String> fileContent){
        this.fileContent = fileContent;
    }
    
    public void fillFileContent(String file){
        setFileContent(new ArrayList<>(Arrays.asList(file.split("\n"))));
    }

    public void update(State state){
        ownPCB.setState(state);
    }
    
    public void previous(String Instruction){
        ownPCB.setPC(ownPCB.getPC()-1);
        ownPCB.setIR(Instruction);
    }
    
    public void next(String Instruction){
        ownPCB.setPC(ownPCB.getPC()+1);
        ownPCB.setIR(Instruction);
    }
    
    public void reset(String Instruction){
        this.ownPCB.setPC(ownPCB.getPCStart());
        ownPCB.setIR(Instruction);
        ownPCB.cleanRegisters();
    }
    
    public void executeInstruction(String instruction, String register, int value) {
        int registerValue = switch (register) {
            case "0000" -> this.ownPCB.getAX();
            case "0001" -> this.ownPCB.getBX();
            case "0010" -> this.ownPCB.getCX();
            case "0011" -> this.ownPCB.getDX();
            default -> 0;
        };

        switch (instruction) {
            case "0000" -> { // load
                this.ownPCB.setAC(registerValue);
            }
            case "0001" -> { // store
                setRegisterValue(register, this.ownPCB.getAC());
            }
            case "0010" -> { // add
                this.ownPCB.setAC(this.ownPCB.getAC() + registerValue);
            }
            case "0011" -> { // sub
                this.ownPCB.setAC(this.ownPCB.getAC() - registerValue);
            }
            case "0100" -> { // mov
                setRegisterValue(register, value);
            }
            case "0101" -> { // inc
                if (register.equals("0111")) { // AC
                    this.ownPCB.setAC(this.ownPCB.getAC() + 1);
                } else {
                    setRegisterValue(register, registerValue + 1);
                }
            }
            case "0110" -> { // dec
                if (register.equals("0111")) { // AC
                    this.ownPCB.setAC(this.ownPCB.getAC() - 1);
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
                String[] regs = register.split(",");
                System.out.println("interruption in progress0");
            }
            default -> System.out.println("DO NOTHING");
        }
    }

    private void setRegisterValue(String register, int value) {
        switch (register) {
            case "0000" -> this.ownPCB.setAX(value);
            case "0001" -> this.ownPCB.setBX(value);
            case "0010" -> this.ownPCB.setCX(value);
            case "0011" -> this.ownPCB.setDX(value);
            default -> this.ownPCB.setAC(value);
        }
    }

    private int getRegisterValue(String register) {
        return switch (register) {
            case "0000" -> this.ownPCB.getAX();
            case "0001" -> this.ownPCB.getBX();
            case "0010" -> this.ownPCB.getCX();
            case "0011" -> this.ownPCB.getDX();
            default -> this.ownPCB.getAC();
        };
    }

    @Override
    public String toString() {
        return "Process{" + "fileContent=" + fileContent + ", ownPCB=" + ownPCB + '}';
    }
    
    
}
