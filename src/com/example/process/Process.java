package src.com.example.process;

import src.com.example.instruction.Instruction;
import java.util.ArrayList;

/*
** Process is the class to create new process objects
 */
public class Process{

    /*
     ** State 1 = new state
     ** State 2 = running state
     ** State 3 = waiting state
     ** State 4 = ready state
     ** State 5 = terminated state
     */
    private int currentState;

    /*
    ** minMemory is the minimum memory required to run the process
    ** totRuntime is the total runtime of the process
    ** instructionNum is the number of instruction for the process
    ** processName is the name of the process
    ** instructions is the arrayList of instruction objects for the process
     */
    private int minMemory;
    private int totRuntime;
    private int instructionNum;
    private int processPriority;
    private int variableNum;
    private String processName;
    private ArrayList<Instruction> instructionsList = new ArrayList<>();
    private ArrayList<Integer> pageTable = new ArrayList<>();



    public Process(int state, int memory, int runtime, String name, ArrayList<Instruction> instructions, int index, int priority, int variable, ArrayList<Integer> pageNumberLookup){
        currentState = state;
        minMemory = memory;
        totRuntime = runtime;
        processName = name;
        instructionNum = index;
        instructionsList = instructions;
        processPriority = priority;
        variableNum = variable;
        pageTable = pageNumberLookup;
    }

    public void setState(int state){
        currentState = state;
    }

    public int getState(){
        return currentState;
    }

    public void setMemory(int memory){
        minMemory = memory;
    }

    public int getMemory(){
        return minMemory;
    }

    public void setRuntime(int runtime){
        totRuntime = runtime;
    }

    public int getRuntime(){
        return totRuntime;
    }

    public void setName(String name){
        processName = name;
    }

    public String getName(){
        return processName;
    }

    public void setInstructions(ArrayList<Instruction> instructions){
        instructionsList = instructions;
    }

    public ArrayList<Instruction> getInstructions(){
        return instructionsList;
    }

    public void setIndex(int index){
        instructionNum = index;
    }

    public int getIndex(){
        return instructionNum;
    }

    public void setPriority(int priority){
        processPriority = priority;
    }

    public int getPriority(){
        return processPriority;
    }

    public void setVariable(int variable){
        variableNum = variable;
    }

    public int getVariable(){
        return variableNum;
    }

    public void setPageTable(ArrayList<Integer> pageNumberTable){
        pageTable = pageNumberTable;
    }

    public ArrayList<Integer> getPageTable(){
        return pageTable;
    }

}
