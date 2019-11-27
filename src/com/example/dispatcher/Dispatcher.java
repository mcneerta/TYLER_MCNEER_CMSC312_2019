package src.com.example.dispatcher;

import src.com.example.mmu.MMU;
import src.com.example.process.Process;
import src.com.example.cpu.CPU;
import src.com.example.instruction.Instruction;
import src.com.example.osdriver.OSDriver;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Dispatcher{

    /*
    ** Dispatch is made just to simulate the dispatcher placing a process on the CPU
     */
    public static void dispatch(ArrayList<Process> processes) throws FileNotFoundException {
        processes.get(OSDriver.position).setState(2);
        CPU.processor(processes);
    }

    /*
    ** handleInterrupts handles I/O and Yield instructions and gives the calls the dispatcher for the next process
     */
    public static void handleInterrupts(ArrayList<Process> processes)throws FileNotFoundException{
        int numCycles = 0;
        Process waitingProcess = processes.get(OSDriver.position);
        int runtimeRemaining = waitingProcess.getRuntime();
        Instruction waitingInstruction = waitingProcess.getInstructions().get(waitingProcess.getIndex());
        numCycles = waitingInstruction.getCycles();
        while(numCycles != 0){
            numCycles--;
            runtimeRemaining--;
        }
        waitingProcess.setRuntime(runtimeRemaining);
        waitingProcess.setState(4);
        waitingProcess.setIndex(waitingProcess.getIndex() + 1);
        OSDriver.position++;
        OSDriver.getDispatcher(processes);
    }

    public static void handleTimeout(ArrayList<Process> processes)throws FileNotFoundException{
            OSDriver.position++;
            OSDriver.getDispatcher(processes);
    }

    /*
    ** handleTermination removes a process from the system and calls the dispatcher for the next process
     */
    public static void handleTermination(ArrayList<Process> processes)throws FileNotFoundException{
        System.out.println("Number of processes: " + processes.size());
        processes.get(OSDriver.position).setState(5);
        MMU.memUsed -= processes.get(OSDriver.position).getMemory();
        System.out.println("Process finished after " + CPU.totalTime + " cycles");
        processes.remove(OSDriver.position);
        System.out.println("Passed remove");
        System.out.println("Number of processes: " + processes.size());
        OSDriver.memCheck(processes);
        OSDriver.getDispatcher(processes);
    }

}
