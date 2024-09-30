/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logic;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author ksala
 */
public class PCB {
    private static int processID = 0;
    public int id;
    private State state;
    private int PC;
    private int AC;
    private String IR;
    private int dirBase = 0;
    private int dirEnd = 0;
    private int execStart = 0;
    private int execEnd = 0;
    private int AX;
    private int BX;
    private int CX;
    private int DX;
    private Stack<Integer> stack;
    @Deprecated 
    private ArrayList<String> IO_Info;
    private int processSize;
    private int prior;
    public int burst = 0;
    
    
    public PCB(State state, int size){
        this.id = ++processID;
        this.state = state;
        this.PC = 0;
        this.AC = 0;
        this.AX = 0;
        this.BX = 0;
        this.CX = 0;
        this.DX = 0;
        this.IR = "0";
        this.stack = new Stack<>();
        this.IO_Info = new ArrayList<>();
        this.processSize = size;
        this.prior = processID;
    }

    public int getBurst() {
        return burst;
    }

    public void setBurst(int burst) {
        this.burst += burst;
    }

    public int getId(){
        return id;
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
    
    public void setdirBase(int PC){
        this.dirBase = PC;
    }

    public int getDirBase() {
        return dirBase;
    }

    public void setDirBase(int dirBase) {
        this.dirBase = dirBase;
    }

    public int getDirEnd() {
        return dirEnd;
    }

    public void setDirEnd(int dirEnd) {
        this.dirEnd = dirEnd;
    }

    public Stack<Integer> getStack() {
        return stack;
    }

    public void setStack(Stack<Integer> stack) {
        this.stack = stack;
    }

    public ArrayList<String> getIO_Info() {
        return IO_Info;
    }

    public void setIO_Info(ArrayList<String> IO_Info) {
        this.IO_Info = IO_Info;
    }

    public int getProcessSize() {
        return processSize;
    }

    public void setProcessSize(int processSize) {
        this.processSize = processSize;
    }

    public int getPrior() {
        return prior;
    }

    public void setPrior(int prior) {
        this.prior = prior;
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
        return "PCB{"+ "id="+ processID + ", state=" + state + ", PC=" + PC + ", AC=" + AC + ", IR=" + IR + ", dirBase=" + dirBase + ", dirEnd=" + dirEnd + ", execStart=" + execStart + ", execEnd=" + execEnd + ", AX=" + AX + ", BX=" + BX + ", CX=" + CX + ", DX=" + DX + ", stack=" + stack + ", IO_Info=" + IO_Info + ", processSize=" + processSize + ", prior=" + prior + ", burst=" + burst + '}';
    }

    
}
