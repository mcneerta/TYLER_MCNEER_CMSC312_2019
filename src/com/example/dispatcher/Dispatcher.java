package src.com.example.dispatcher;

import src.com.example.mmu.MMU;
import src.com.example.process.Process;
import src.com.example.cpu.CPU;
import src.com.example.instruction.Instruction;
import src.com.example.osdriver.OSDriver;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static src.com.example.cpu.CPU.processesThread;
import static src.com.example.cpu.CPU.rrProcessesThread;
import static src.com.example.osdriver.OSDriver.*;

public class Dispatcher{

    /*
    ** Dispatch is made just to simulate the dispatcher placing a process on the CPU
     */
    public static void dispatch(ArrayList<Process> processes)throws FileNotFoundException, InterruptedException{
        processes.get(OSDriver.position).setState(2);
        if(processesThread.getState() != Thread.State.RUNNABLE && rrProcessesThread.getState() != Thread.State.RUNNABLE) {
            CPU.getThreads();
        }
        else{
            CPU.processor(processes);
        }
    }

    public static void rrDispatch(ArrayList<Process> processes)throws FileNotFoundException, InterruptedException{
        if(processes.size() == 0){
            System.exit(0);
        }
        processes.get(OSDriver.rrPosition).setState(2);
        CPU.rrProcessor(processes);
        }

    /*
    ** handleInterrupts handles I/O and Yield instructions and gives the calls the dispatcher for the next process
     */
    public static void handleInterrupts(ArrayList<Process> processes)throws FileNotFoundException, InterruptedException{
        int numCycles = 0;
        Process waitingProcess = processes.get(OSDriver.position);
        int runtimeRemaining = waitingProcess.getRuntime();
        Instruction waitingInstruction = waitingProcess.getInstructions().get(waitingProcess.getIndex());
        numCycles = waitingInstruction.getCycles();
        while(numCycles != 0){
            numCycles--;
        }
        waitingProcess.setRuntime(runtimeRemaining);
        waitingProcess.setState(4);
        waitingProcess.setIndex(waitingProcess.getIndex() + 1);
        waitingProcess.getPageTable().remove(0);
        OSDriver.position++;
        OSDriver.getDispatcher();
    }

    public static void rrHandleInterrupts(ArrayList<Process> processes)throws FileNotFoundException, InterruptedException{
        int numCycles = 0;
        Process waitingProcess = processes.get(OSDriver.rrPosition);
        int runtimeRemaining = waitingProcess.getRuntime();
        Instruction waitingInstruction = waitingProcess.getInstructions().get(waitingProcess.getIndex());
        numCycles = waitingInstruction.getCycles();
        while(numCycles != 0){
            numCycles--;
        }
        waitingProcess.setRuntime(runtimeRemaining);
        waitingProcess.setState(4);
        waitingProcess.setIndex(waitingProcess.getIndex() + 1);
        waitingProcess.getPageTable().remove(0);
        OSDriver.rrPosition++;
        getRRDispatcher();
    }

    public static void handleTimeout()throws FileNotFoundException, InterruptedException{
            OSDriver.rrPosition++;
            CPU.timeQuantum = 50;
            getRRDispatcher();
    }

    /*
    ** handleTermination removes a process from the system and calls the dispatcher for the next process
     */
    public static void handleTermination(ArrayList<Process> processes)throws FileNotFoundException, InterruptedException{
        Scanner input = new Scanner(System.in);
        String readInput;
        System.out.println("Number of processes: " + processes.size());
        processes.get(OSDriver.position).setState(5);
        MMU.memUsed -= processes.get(OSDriver.position).getMemory();
        System.out.println("Process finished after " + processesThread.getTime()  + " cycles");
        processes.remove(OSDriver.position);
        System.out.println("Passed remove");
        System.out.println("Number of processes: " + processes.size());
        System.out.println("Would you like to add a new process? (Y or N)");
        readInput = input.nextLine();

        if(readInput.trim().equals("y") || readInput.trim().equals("Y")){
            OSDriver.numProcesses++;
            OSDriver.collectProcesses();
        }

        memCheck(processes);
        OSDriver.getDispatcher();
    }

     public static void rrHandleTermination()throws FileNotFoundException, InterruptedException{
        Scanner input = new Scanner(System.in);
        String readInput;
        ArrayList<Process> processes = OSDriver.compareProcesses;
        System.out.println("Number of processes: " + processes.size());
        processes.get(OSDriver.rrPosition).setState(5);
        MMU.memUsed -= processes.get(OSDriver.rrPosition).getMemory();
        System.out.println("Process(RR) finished after " + rrProcessesThread.getTime() + " cycles");
        OSDriver.compareProcesses.remove(OSDriver.rrPosition);
        System.out.println("Passed remove");
        System.out.println("RR handled");
        System.out.println("Number of processes: " + processes.size());
        if(compareProcesses.size() > 0) {
            compareProcesses.get(0).setState(2);
        }

        rrMemCheck(processes);
        getRRDispatcher();
    }


}
