import java.io.*;
import java.util.*;

public class Process{

/*
** State 1 = new state
** State 2 = running state
** State 3 = waiting state
** State 4 = ready state
** State 5 = terminated state
*/
private int currentState;
private int minMemory;
private int totRuntime;
private String processName;
private ArrayList<Instruction> instructions = new ArrayList<>();


  public Process(int state, int memory, int runtime, String name, ArrayList<Instruction> instructions){
    currentState = state;
    minMemory = memory;
    totRuntime = runtime;
    processName = name;
    this.instructions = instructions;
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
    this.instructions = instructions;
  }

  public ArrayList<Instruction> getInstructions(){
    return instructions;
  }

}
