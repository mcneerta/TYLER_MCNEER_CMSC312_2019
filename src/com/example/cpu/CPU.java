package src.com.example.cpu;

import src.com.example.osdriver.OSDriver;
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
    private static int timeQuantum = 0;

    public static int totalTime = 0;

    /*
    ** processor is used to simulate running a process and handling instructions
     */
    public static void processor(ArrayList<Process> processes){
        Process running = processes.get(OSDriver.position);
        int runtimeRemaining = running.getRuntime();
        int index = running.getIndex();
        ArrayList<Instruction> instructions = running.getInstructions();
        int numCycles = 0;
        timeQuantum = 50;

        System.out.println("Index: " + index);
        System.out.println("Number of instructions remaining: " + (instructions.size() - index));

        /*
        ** This while loop goes through all instructions
         */
        if(OSDriver.schedulerFlag == 0) {
            while (running.getIndex() < instructions.size()) {
                index = running.getIndex();
                Instruction runningInstruction = instructions.get(index);
                numCycles = runningInstruction.getCycles();


                /*
                 ** This handles calculate instructions and adjusts the remaining runtime
                 */
                if (runningInstruction.getInstructionName().equals("CALCULATE")) {
                    while (numCycles != 0) {
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
                else if (runningInstruction.getInstructionName().equals("I/O")) {
                    Dispatcher.handleInterrupts(processes);
                }

                /*
                 ** This handles the start of a critical section and waits until the lock is available
                 */
                else if (runningInstruction.getInstructionName().equals("CRITICAL")) {
                    while (!lockAvailable) {
                        System.out.println("Waiting for lock");
                    }
                    lockAvailable = false;
                }

                /*
                 ** This handles the end of a critical section and releases the lock
                 */
                else if (runningInstruction.getInstructionName().equals("END")) {
                    lockAvailable = true;
                }

                /*
                 ** This handles yield instructions and calls handleInterrupts in the Dispatcher class
                 */
                else if (runningInstruction.getInstructionName().equals("YIELD")) {
                    Dispatcher.handleInterrupts(processes);
                }

                /*
                 ** This handles out instructions and prints out some data of the process
                 */
                else if (runningInstruction.getInstructionName().equals("OUT")) {
                    while (numCycles != 0) {
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
        }

        else{
            while (running.getIndex() < instructions.size() && (timeQuantum > 0 || lockAvailable == false)) {
                index = running.getIndex();
                Instruction runningInstruction = instructions.get(index);
                numCycles = runningInstruction.getCycles();

                /*
                 ** This handles calculate instructions and adjusts the remaining runtime
                 */
                if (runningInstruction.getInstructionName().equals("CALCULATE")) {
                    while (numCycles != 0 && (timeQuantum > 0 || lockAvailable == false)) {
                        System.out.println("Number of cycles: " + numCycles);
                        numCycles--;
                        runtimeRemaining--;
                        totalTime++;
                        timeQuantum--;
                        runningInstruction.setCycles(numCycles);
                    }
                    running.setRuntime(runtimeRemaining);
                }

                /*
                 ** This handles i/o instructions and calls handleInterrupts in the Dispatcher class
                 */
                else if (runningInstruction.getInstructionName().equals("I/O")) {
                    Dispatcher.handleInterrupts(processes);
                }

                /*
                 ** This handles the start of a critical section and waits until the lock is available
                 */
                else if (runningInstruction.getInstructionName().equals("CRITICAL")) {
                    while (!lockAvailable) {
                        System.out.println("Waiting for lock");
                    }
                    lockAvailable = false;
                }

                /*
                 ** This handles the end of a critical section and releases the lock
                 */
                else if (runningInstruction.getInstructionName().equals("END")) {
                    lockAvailable = true;
                }

                /*
                 ** This handles yield instructions and calls handleInterrupts in the Dispatcher class
                 */
                else if (runningInstruction.getInstructionName().equals("YIELD")) {
                    Dispatcher.handleInterrupts(processes);
                }

                /*
                 ** This handles out instructions and prints out some data of the process
                 */
                else if (runningInstruction.getInstructionName().equals("OUT")) {
                    while (numCycles != 0 && (timeQuantum > 0 || lockAvailable == false)) {
                        System.out.println("Number of cycles: " + numCycles);
                        numCycles--;
                        runtimeRemaining--;
                        totalTime++;
                        timeQuantum--;
                        runningInstruction.setCycles(numCycles);
                    }
                    running.setRuntime(runtimeRemaining);

                    if(numCycles == 0) {
                        System.out.println("Name: " + running.getName());
                        System.out.println("Memory needed: " + running.getMemory());
                        System.out.println("Runtime remaining: " + running.getRuntime());
                        System.out.println("Process priority: " + running.getPriority());
                    }
                }
                if(timeQuantum > 0 || lockAvailable == false) {
                    index++;
                    running.setIndex(index);
                }
            }
        }
        if(timeQuantum <= 0 && OSDriver.schedulerFlag == 1){
            Dispatcher.handleTimeout(processes);
        }

        Dispatcher.handleTermination(processes);
    }



}
