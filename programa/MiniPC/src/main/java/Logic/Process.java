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
    
    public void executeInstruction(String instruction, String register, int value){
        System.out.println(instruction+"\n"+register+"\n"+value);
        if(instruction.equals("001")){ //load
            if(register.equals("00")){ //AX
                this.ownPCB.setAC(this.ownPCB.getAX());
            }
            else if(register.equals("01")){ //BX
                this.ownPCB.setAC(this.ownPCB.getBX());
            }
            else if(register.equals("10")){ //CX
                this.ownPCB.setAC(this.ownPCB.getCX());
            }
            else{ //DX
                this.ownPCB.setAC(this.ownPCB.getDX());
            }
        }
        else if(instruction.equals("010")){ //store
            if(register.equals("00")){ //AX
                this.ownPCB.setAX(this.ownPCB.getAC());
            }
            else if(register.equals("01")){ //BX
                this.ownPCB.setBX(this.ownPCB.getAC());
            }
            else if(register.equals("10")){ //CX
                this.ownPCB.setCX(this.ownPCB.getAC());
            }
            else{ //DX
                this.ownPCB.setDX(this.ownPCB.getAC());
            }
        }
        else if(instruction.equals("100")){ //sub
            if(register.equals("00")){ //AX
                this.ownPCB.setAC(this.ownPCB.getAC()-this.ownPCB.getAX());
            }
            else if(register.equals("01")){ //BX
                this.ownPCB.setAC(this.ownPCB.getAC()-this.ownPCB.getBX());
            }
            else if(register.equals("10")){ //CX
                this.ownPCB.setAC(this.ownPCB.getAC()-this.ownPCB.getCX());
            }
            else{ //DX
                this.ownPCB.setAC(this.ownPCB.getAC()-this.ownPCB.getDX());
            }
        }
        else if(instruction.equals("101")){ //add
            if(register.equals("00")){ //AX
                this.ownPCB.setAC(this.ownPCB.getAC()+this.ownPCB.getAX());
            }
            else if(register.equals("01")){ //BX
                this.ownPCB.setAC(this.ownPCB.getAC()+this.ownPCB.getBX());
            }
            else if(register.equals("10")){ //CX
                this.ownPCB.setAC(this.ownPCB.getAC()+this.ownPCB.getCX());
            }
            else{ //DX
                this.ownPCB.setAC(this.ownPCB.getAC()+this.ownPCB.getDX());
            }
        }
        else if(instruction.equals("011")){ //mov
            if(register.equals("00")){ //AX
                this.ownPCB.setAX(value);
            }
            else if(register.equals("01")){ //BX
                this.ownPCB.setBX(value);
            }
            else if(register.equals("10")){ //CX
                this.ownPCB.setCX(value);
            }
            else if(register.equals("11")){ //DX
                this.ownPCB.setDX(value);
            }
            else{ //AC
                this.ownPCB.setAC(value);
            }
        }
        else{
            System.out.println("DO NOTHING");
        }
    }
    
    @Override
    public String toString() {
        return "Process{" + "fileContent=" + fileContent + ", ownPCB=" + ownPCB + '}';
    }
    
    
}
