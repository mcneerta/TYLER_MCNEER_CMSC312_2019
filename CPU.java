import java.io.*;
import java.util.*;

public class CPU{

  public static void processor(ArrayList<Process> processes, int position, int index){
      Process running = processes.get(position);
      int runtimeRemaining = running.getRuntime();
      System.out.println("Number of processes: " + processes.size());
      ArrayList<Instruction> instructions = running.getInstructions();
      int numCycles = 0;
      System.out.println("Number of instructions: " + instructions.size());
      System.out.println("Index: " + index);

      while(index < instructions.size()){
          numCycles = instructions.get(index).getCycles();

          if(instructions.get(index).getInstructionName().equals("CALCULATE")){
            while(numCycles != 0){
              System.out.println("Number of cycles: " + numCycles);
              numCycles--;
              runtimeRemaining--;
            }
            running.setRuntime(runtimeRemaining);
          }

          else if(instructions.get(index).getInstructionName().equals("I/O")){
            Dispatcher.handleInterrupts(processes, position, index);
          }

          else if(instructions.get(index).getInstructionName().equals("YIELD")){
            Dispatcher.handleInterrupts(processes, position, index);
          }

          else if(instructions.get(index).getInstructionName().equals("OUT")){
            while(numCycles != 0){
              System.out.println("Number of cycles: " + numCycles);
              numCycles--;
              runtimeRemaining--;
            }
            running.setRuntime(runtimeRemaining);

            System.out.println("Name: " + running.getName());
            System.out.println("Memory needed: " + running.getMemory());
            System.out.println("Runtime remaining: " + running.getRuntime());
          }
          index++;
      }

      System.out.println("Number of processes: " + processes.size());
      Dispatcher.handleTermination(processes, position, index);
}

}
