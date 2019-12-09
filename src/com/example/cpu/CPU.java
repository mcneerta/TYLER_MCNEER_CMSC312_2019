package src.com.example.cpu;

import src.com.example.osdriver.OSDriver;
import src.com.example.process.Process;
import src.com.example.dispatcher.Dispatcher;
import src.com.example.instruction.Instruction;
import src.com.example.processList.ProcessList;
import src.com.example.processList.RRProcessList;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static src.com.example.osdriver.OSDriver.processes;

/*
** CPU is used to simulate running the processes
 */
public class CPU{

    /*
    ** lockAvailable is the boolean for critical section control
     */
    private static Boolean lockAvailable = true;
    public static int timeQuantum = 50;

    private static ArrayList<Integer> mailboxes = new ArrayList<>();
    //public static int totalTime = 0;
    public static ProcessList processesThread = new ProcessList(processes,0);
    public static RRProcessList rrProcessesThread = new RRProcessList(OSDriver.compareProcesses,0);

    /*
    ** processor is used to simulate running a process and handling instructions
     */
    public static void processor(ArrayList<Process> processes)throws FileNotFoundException, InterruptedException{
        Process running;

        if(processes.size() > 1){
            running = processes.get(OSDriver.position);
        }

        else{
            running = processes.get(0);
        }
        int runtimeRemaining = running.getRuntime();
        int index = running.getIndex();
        ArrayList<Instruction> instructions = running.getInstructions();
        int numCycles = 0;
        int childTime = 0;

        System.out.println("Index: " + index);
        System.out.println("Number of instructions remaining: " + (instructions.size() - index));

        /*
        ** This while loop goes through all instructions
         */
            while (running.getIndex() < instructions.size()) {
                index = running.getIndex();
                Instruction runningInstruction = instructions.get(index);
                numCycles = runningInstruction.getCycles();

                if(processesThread.getTime() == OSDriver.quitTime && OSDriver.quitTime != 0){
                    System.exit(0);
                }

                /*
                 ** This handles calculate instructions and adjusts the remaining runtime
                 */
                if (runningInstruction.getInstructionName().equals("CALCULATE")) {
                    while (numCycles != 0) {

                        if(processesThread.getTime() == OSDriver.quitTime && OSDriver.quitTime != 0){
                            System.exit(0);
                        }

                        System.out.println("Number of cycles: " + numCycles);
                        numCycles--;
                        runtimeRemaining--;
                        processesThread.setTime(processesThread.getTime()+1);
                        processesThread.sleep(10);
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
                    if(!lockAvailable)
                        System.out.println("Waiting for lock");

                    while (!lockAvailable) {

                    }
                    lockAvailable = false;
                }

                /*
                 ** This handles the end of a critical section and releases the lock
                 */
                else if (runningInstruction.getInstructionName().equals("END")) {
                    lockAvailable = true;
                }

                else if (runningInstruction.getInstructionName().equals("FORK")) {
                    childTime = handleFork(running);
                    processesThread.setTime(processesThread.getTime() - 95);//- childTime);
                }

                /*
                 ** This handles yield instructions and calls handleInterrupts in the Dispatcher class
                 */
                else if (runningInstruction.getInstructionName().equals("YIELD")) {
                    Dispatcher.handleInterrupts(processes);
                }

                else if (runningInstruction.getInstructionName().equals("RECEIVE")) {
                    running.setVariable(mailboxes.get(mailboxes.size()-1));
                }

                /*
                 ** This handles out instructions and prints out some data of the process
                 */
                else if (runningInstruction.getInstructionName().equals("OUT")) {
                    while (numCycles != 0) {

                        if(processesThread.getTime() == OSDriver.quitTime && OSDriver.quitTime != 0){
                            System.exit(0);
                        }

                        System.out.println("Number of cycles: " + numCycles);
                        numCycles--;
                        runtimeRemaining--;
                        processesThread.setTime(processesThread.getTime()+1);
                        processesThread.sleep(10);
                    }
                    running.setRuntime(runtimeRemaining);

                    System.out.println("Name: " + running.getName());
                    System.out.println("Memory needed: " + running.getMemory());
                    System.out.println("Runtime remaining: " + running.getRuntime());
                    System.out.println("Process priority: " + running.getPriority());
                    System.out.println("Process variable: " + running.getVariable());
                }
                index++;
                running.setIndex(index);
            }
            if(!running.getName().equals("Child")){
                Dispatcher.handleTermination(processes);
            }

    }

    public static void rrProcessor(ArrayList<Process> processes) throws FileNotFoundException, InterruptedException{
        Process running;

        if(processes.size() > 1){
            running = processes.get(OSDriver.rrPosition);
        }

        else{
            running = processes.get(0);
        }
        int runtimeRemaining = running.getRuntime();
        int index = running.getIndex();
        ArrayList<Instruction> instructions = running.getInstructions();
        int numCycles = 0;
        int childTime = 0;

        System.out.println("Index: " + index);
        System.out.println("Number of instructions remaining: " + (instructions.size() - index));

        while (running.getIndex() < instructions.size() && (timeQuantum > 0 || lockAvailable == false)) {
            index = running.getIndex();
            Instruction runningInstruction = instructions.get(index);
            numCycles = runningInstruction.getCycles();

            if(rrProcessesThread.getTime() == OSDriver.quitTime && OSDriver.quitTime != 0){
                System.exit(0);
            }

            /*
             ** This handles calculate instructions and adjusts the remaining runtime
             */
            if (runningInstruction.getInstructionName().equals("CALCULATE")) {
                while (numCycles != 0 && (timeQuantum > 0 || lockAvailable == false)) {

                    if(rrProcessesThread.getTime() == OSDriver.quitTime && OSDriver.quitTime != 0){
                        System.exit(0);
                    }

                    //System.out.println("Number of cycles: " + numCycles);
                    numCycles--;
                    runtimeRemaining--;
                    rrProcessesThread.setTime(rrProcessesThread.getTime()+1);
                    timeQuantum--;
                    runningInstruction.setCycles(numCycles);
                    rrProcessesThread.sleep(10);
                }
                running.setRuntime(runtimeRemaining);
            }

            /*
             ** This handles i/o instructions and calls handleInterrupts in the Dispatcher class
             */
            else if (runningInstruction.getInstructionName().equals("I/O")) {
                Dispatcher.rrHandleInterrupts(processes);
            }

            /*
             ** This handles the start of a critical section and waits until the lock is available
             */
            else if (runningInstruction.getInstructionName().equals("CRITICAL")) {
                if(!lockAvailable){
                    System.out.println("Waiting for lock");
                }

                while (!lockAvailable) {

                }
                lockAvailable = false;
            }

            /*
             ** This handles the end of a critical section and releases the lock
             */
            else if (runningInstruction.getInstructionName().equals("END")) {
                lockAvailable = true;
            }

            else if (runningInstruction.getInstructionName().equals("FORK")) {
                childTime = handleFork(running);
                rrProcessesThread.setTime(rrProcessesThread.getTime() + 95);//childTime);
            }

            /*
             ** This handles yield instructions and calls handleInterrupts in the Dispatcher class
             */
            else if (runningInstruction.getInstructionName().equals("YIELD")) {
                Dispatcher.rrHandleInterrupts(processes);
            }

            else if (runningInstruction.getInstructionName().equals("RECEIVE")) {
                running.setVariable(mailboxes.get(mailboxes.size()-1));
            }

            /*
             ** This handles out instructions and prints out some data of the process
             */
            else if (runningInstruction.getInstructionName().equals("OUT")) {
                while (numCycles != 0 && (timeQuantum > 0 || lockAvailable == false)) {

                    if(rrProcessesThread.getTime() == OSDriver.quitTime && OSDriver.quitTime != 0){
                        System.exit(0);
                    }

                    //System.out.println("Number of cycles: " + numCycles);
                    numCycles--;
                    runtimeRemaining--;
                    rrProcessesThread.setTime(rrProcessesThread.getTime()+1);
                    timeQuantum--;
                    runningInstruction.setCycles(numCycles);
                    rrProcessesThread.sleep(10);
                }
                running.setRuntime(runtimeRemaining);

                if(numCycles == 0) {
                    System.out.println("Name: " + running.getName());
                    System.out.println("Memory needed: " + running.getMemory());
                    System.out.println("Runtime remaining: " + running.getRuntime());
                    System.out.println("Process priority: " + running.getPriority());
                    System.out.println("Process variable: " + running.getVariable());
                }
            }
            if(timeQuantum > 0 || lockAvailable == false) {
                index++;
                running.setIndex(index);
            }
        }

        if(running.getIndex() >= instructions.size() && !running.getName().equals("Child")){
            Dispatcher.rrHandleTermination();
        }

        if(timeQuantum <= 0){
            Dispatcher.handleTimeout();
        }
    }

    public static void getThreads(){
        if(processesThread.getState() != Thread.State.RUNNABLE ) {
            processesThread.start();
        }
        if(rrProcessesThread.getState() != Thread.State.RUNNABLE ) {
            rrProcessesThread.start();
        }
    }




    private static int handleFork(Process parent)throws FileNotFoundException, InterruptedException{
        mailboxes.add(parent.getVariable());
        System.out.println("Parent variable: " + mailboxes.get(mailboxes.size()-1));
        String name = " ";
        String parse = " ";
        int numParse = 0;
        int runtime = 0;
        int numCycles = 0;
        Random rand = new Random();
        File forkFile = new File("Child.txt");
        Scanner reader = new Scanner(forkFile);
        parse = reader.nextLine();
        numParse = parse.indexOf("Name:") + 5;
        parse = parse.substring(numParse).trim();
        name = parse;

        ArrayList<Instruction> instructions = new ArrayList<Instruction>();
        ArrayList<Process> childList = new ArrayList<Process>();
        Process childProcess = new Process(1, 0, runtime, name, instructions, 0, 0, 0);

        while (!parse.contains("EXE")) {

            /*
             ** This reads in the calculate instruction and randomally edits
             ** the number of cycles needed to complete the instruction
             */
            if (parse.contains("CALCULATE")) {
                numParse = parse.indexOf("CALCULATE") + 9;
                parse = parse.substring(numParse).trim();
                numCycles = Integer.parseInt(parse);
                runtime += numCycles;
                System.out.println("calc");
                Instruction next = new Instruction("CALCULATE", numCycles);
                instructions.add(next);
            }

            /*
             ** This reads in the i/o instruction and randomally edits
             ** the number of cycles needed to complete the instruction
             */
            else if (parse.contains("I/O")) {
                numParse = parse.indexOf("I/O") + 3;
                parse = parse.substring(numParse).trim();
                numCycles = Integer.parseInt(parse);
                runtime += numCycles;
                System.out.println("i/o");
                Instruction next = new Instruction("I/O", numCycles);
                instructions.add(next);
            }

            /*
             ** This reads in the yield instruction
             */
            else if (parse.contains("YIELD")) {
                numCycles = 0;
                System.out.println("yield");
                Instruction next = new Instruction("YIELD", numCycles);
                instructions.add(next);
            }

            /*
             ** This reads in the critical instruction
             */
            else if (parse.contains("CRITICAL")) {
                numCycles = 0;
                System.out.println("critical");
                Instruction next = new Instruction("CRITICAL", numCycles);
                instructions.add(next);
            }

            /*
             ** This reads in the end instruction
             */
            else if (parse.contains("END")) {
                numCycles = 0;
                System.out.println("end");
                Instruction next = new Instruction("END", numCycles);
                instructions.add(next);
            }

            else if (parse.contains("FORK")) {
                numCycles = 0;
                System.out.println("fork");
                Instruction next = new Instruction("FORK", numCycles);
                instructions.add(next);
            }

            else if (parse.contains("RECEIVE")) {
                numCycles = 0;
                System.out.println("receive");
                Instruction next = new Instruction("RECEIVE", numCycles);
                instructions.add(next);
            }

            /*
             ** This reads in the out instruction and randomally edits
             ** the number of cycles needed to complete the instruction
             */
            else if (parse.contains("OUT")) {
                numParse = parse.indexOf("OUT") + 3;
                parse = parse.substring(numParse).trim();
                numCycles = Integer.parseInt(parse);
                runtime += numCycles;
                System.out.println("out");
                Instruction next = new Instruction("OUT", numCycles);
                instructions.add(next);
            }

            parse = reader.nextLine();
        }
        childProcess.setInstructions(instructions);
        childProcess.setRuntime(runtime);
        System.out.println(runtime);
        childProcess.setPriority(rand.nextInt(10) + 1);
        childList.add(childProcess);

        processor(childList);
        childList.remove(0);
        return runtime;
    }



}
