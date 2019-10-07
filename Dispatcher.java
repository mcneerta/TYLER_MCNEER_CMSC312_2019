import java.io.*;
import java.util.*;

public class Dispatcher{

  public static void dispatch(ArrayList<Process> processes, int position, int index){
    processes.get(position).setState(2);
    CPU.processor(processes, position, index);
  }

  public static void handleInterrupts(ArrayList<Process> processes, int position, int index){
    int numCycles = 0;
    int runtimeRemaining = processes.get(position).getRuntime();
    Process waitingProcess = processes.get(position);
    Instruction waitingInstruction = waitingProcess.getInstructions().get(index);
    waitingProcess.setState(3);
    numCycles = waitingInstruction.getCycles();
    while(numCycles != 0){
      numCycles--;
      runtimeRemaining--;
    }
    processes.get(position).setRuntime(runtimeRemaining);
    processes.get(position).setState(4);
    OSDriver.getDispatcher(processes, index);
  }

  public static void handleTermination(ArrayList<Process> processes, int position, int index){
    System.out.println("Number of processes: " + processes.size());
    processes.get(position).setState(5);
    System.out.println("Passed set state 5");
    processes.remove(position);
    System.out.println("Passed remove");
    OSDriver.getDispatcher(processes, index);
  }

}
