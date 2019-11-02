package src.com.example.cpu;

import src.com.example.process.Process;
import src.com.example.dispatcher.Dispatcher;
import src.com.example.instruction.Instruction;

import java.util.ArrayList;

public class CPU{

    public static void processor(ArrayList<Process> processes, int position){
        Process running = processes.get(position);
        int runtimeRemaining = running.getRuntime();
        int index = running.getIndex();
        //System.out.println("Number of processes: " + processes.size());
        ArrayList<Instruction> instructions = running.getInstructions();
        int numCycles = 0;

        System.out.println("Index: " + index);
        System.out.println("Number of instructions remaining: " + (instructions.size() - index));

        while(running.getIndex() < instructions.size()){
            index = running.getIndex();
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
                Dispatcher.handleInterrupts(processes, position);
            }

            else if(instructions.get(index).getInstructionName().equals("YIELD")){
                //running.setIndex(index+1);
                Dispatcher.handleInterrupts(processes, position);
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
            running.setIndex(index);
        }

        //System.out.println("Number of processes: " + processes.size());
        Dispatcher.handleTermination(processes, position);
    }

}
