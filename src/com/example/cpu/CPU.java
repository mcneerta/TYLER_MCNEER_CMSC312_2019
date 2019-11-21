package src.com.example.cpu;

import src.com.example.process.Process;
import src.com.example.dispatcher.Dispatcher;
import src.com.example.instruction.Instruction;
import java.util.ArrayList;

/*
** CPU is used to simulate running the processes
 */
public class CPU{

    /*
    ** lockAvailable is the boolean for critical section control
     */
    private static Boolean lockAvailable = true;

    public static int totalTime = 0;
    /*
    ** processor is used to simulate running a process and handling instructions
     */
    public static void processor(ArrayList<Process> processes, int position){
        Process running = processes.get(position);
        int runtimeRemaining = running.getRuntime();
        int index = running.getIndex();
        ArrayList<Instruction> instructions = running.getInstructions();
        int numCycles = 0;

        System.out.println("Index: " + index);
        System.out.println("Number of instructions remaining: " + (instructions.size() - index));

        /*
        ** This while loop goes through all instructions
         */
        while(running.getIndex() < instructions.size()){
            index = running.getIndex();
            numCycles = instructions.get(index).getCycles();

            /*
            ** This handles calculate instructions and adjusts the remaining runtime
             */
            if(instructions.get(index).getInstructionName().equals("CALCULATE")){
                while(numCycles != 0){
                    System.out.println("Number of cycles: " + numCycles);
                    numCycles--;
                    runtimeRemaining--;
                    totalTime++;
                }
                running.setRuntime(runtimeRemaining);
            }

            /*
            ** This handles i/o instructions and calls handleInterrupts in the Dispatcher class
             */
            else if(instructions.get(index).getInstructionName().equals("I/O")){
                Dispatcher.handleInterrupts(processes, position);
            }

            /*
            ** This handles the start of a critical section and waits until the lock is available
             */
            else if(instructions.get(index).getInstructionName().equals("CRITICAL")){
                while(!lockAvailable){
                    System.out.println("Waiting for lock");
                }
                lockAvailable = false;
            }

            /*
            ** This handles the end of a critical section and releases the lock
             */
            else if(instructions.get(index).getInstructionName().equals("END")){
                lockAvailable = true;
            }

            /*
            ** This handles yield instructions and calls handleInterrupts in the Dispatcher class
             */
            else if(instructions.get(index).getInstructionName().equals("YIELD")){
                Dispatcher.handleInterrupts(processes, position);
            }

            /*
            ** This handles out instructions and prints out some data of the process
             */
            else if(instructions.get(index).getInstructionName().equals("OUT")){
                while(numCycles != 0){
                    System.out.println("Number of cycles: " + numCycles);
                    numCycles--;
                    runtimeRemaining--;
                    totalTime++;
                }
                running.setRuntime(runtimeRemaining);

                System.out.println("Name: " + running.getName());
                System.out.println("Memory needed: " + running.getMemory());
                System.out.println("Runtime remaining: " + running.getRuntime());
                System.out.println("Process priority: " + running.getPriority());
            }
            index++;
            running.setIndex(index);
        }

        Dispatcher.handleTermination(processes, position);
    }


}
