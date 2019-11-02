package src.com.example.dispatcher;

import src.com.example.process.Process;
import src.com.example.cpu.CPU;
import src.com.example.instruction.Instruction;
import src.com.example.osdriver.OSDriver;

import java.util.ArrayList;

public class Dispatcher{

    public static void dispatch(ArrayList<Process> processes, int position){
        processes.get(position).setState(2);
        CPU.processor(processes, position);
    }

    public static void handleInterrupts(ArrayList<Process> processes, int position){
        int numCycles = 0;
        Process waitingProcess = processes.get(position);
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
        position++;
        OSDriver.getDispatcher(processes, position);
    }

    public static void handleTermination(ArrayList<Process> processes, int position){
        System.out.println("Number of processes: " + processes.size());
        processes.get(position).setState(5);
        System.out.println("Passed set state 5");
        processes.remove(position);
        System.out.println("Passed remove");
        System.out.println("Number of processes: " + processes.size());
        OSDriver.getDispatcher(processes, position);
    }

}
