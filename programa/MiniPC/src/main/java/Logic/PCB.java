/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

/**
 *
 * @author ksala
 */
public class PCB {
    private static int processID;
    private State state;
    private int PC;
    private int AC;
    private String IR;
    private int PCStart;
    private int AX;
    private int BX;
    private int CX;
    private int DX;
    
    public PCB(State state){
        processID++;
        this.state = state;
        this.PC = 0;
        this.AC = 0;
        this.AX = 0;
        this.BX = 0;
        this.CX = 0;
        this.DX = 0;
        this.IR = "0";
        this.PCStart = this.PC;
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
    
    public static int getProcessID() {
        return processID;
    }

    public static void setProcessID(int processID) {
        PCB.processID = processID;
    }
    
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
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
    
    public int getPC() {
        return PC;
    }
    
    public void setPC(int PC){
        this.PC = PC;
    }
    
    public int getPCStart() {
        return PCStart;
    }
    
    public void setPCStart(int PC){
        this.PCStart = PC;
    }
    
    public void cleanRegisters(){
        setAC(0);
        setAX(0);
        setBX(0);
        setCX(0);
        setDX(0);
    }

    @Override
    public String toString() {
        return "PCB{" + "\nstate=" + state + ", \nPC=" + PC + ", \nAC=" + AC + ", \nIR=" + IR + ", \nPCStart=" + PCStart + ", \nAX=" + AX + ", \nBX=" + BX + ", \nCX=" + CX + ", \nDX=" + DX + '}';
    }
    
    
}
