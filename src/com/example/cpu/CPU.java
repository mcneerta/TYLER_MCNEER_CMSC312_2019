package src.com.example.cpu;

import src.com.example.osdriver.OSDriver;
import src.com.example.process.Process;
import src.com.example.dispatcher.Dispatcher;
import src.com.example.instruction.Instruction;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/*
** CPU is used to simulate running the processes
 */
public class CPU{

    /*
    ** lockAvailable is the boolean for critical section control
     */
    private static Boolean lockAvailable = true;
    private static int timeQuantum = 0;

    private static ArrayList<Integer> mailboxes = new ArrayList<>();
    public static int totalTime = 0;

    /*
    ** processor is used to simulate running a process and handling instructions
     */
    public static void processor(ArrayList<Process> processes)throws FileNotFoundException{
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
        timeQuantum = 50;

        System.out.println("Index: " + index);
        System.out.println("Number of instructions remaining: " + (instructions.size() - index));

        /*
        ** This while loop goes through all instructions
         */
        if(OSDriver.schedulerFlag == 0 || running.getName().equals("Child")) {
            while (running.getIndex() < instructions.size()) {
                index = running.getIndex();
                Instruction runningInstruction = instructions.get(index);
                numCycles = runningInstruction.getCycles();

                if(totalTime == OSDriver.quitTime && OSDriver.quitTime != 0){
                    System.exit(0);
                }

                /*
                 ** This handles calculate instructions and adjusts the remaining runtime
                 */
                if (runningInstruction.getInstructionName().equals("CALCULATE")) {
                    while (numCycles != 0) {

                        if(totalTime == OSDriver.quitTime && OSDriver.quitTime != 0){
                            System.exit(0);
                        }

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

                else if (runningInstruction.getInstructionName().equals("FORK")) {
                    handleFork(running);
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

                        if(totalTime == OSDriver.quitTime && OSDriver.quitTime != 0){
                            System.exit(0);
                        }

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
                    System.out.println("Process variable: " + running.getVariable());
                }
                index++;
                running.setIndex(index);
            }
            if(!running.getName().equals("Child")){
                Dispatcher.handleTermination(processes);
            }
        }

        else{
            while (running.getIndex() < instructions.size() && (timeQuantum > 0 || lockAvailable == false)) {
                index = running.getIndex();
                Instruction runningInstruction = instructions.get(index);
                numCycles = runningInstruction.getCycles();

                if(totalTime == OSDriver.quitTime && OSDriver.quitTime != 0){
                    System.exit(0);
                }

                /*
                 ** This handles calculate instructions and adjusts the remaining runtime
                 */
                if (runningInstruction.getInstructionName().equals("CALCULATE")) {
                    while (numCycles != 0 && (timeQuantum > 0 || lockAvailable == false)) {

                        if(totalTime == OSDriver.quitTime && OSDriver.quitTime != 0){
                            System.exit(0);
                        }

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

                else if (runningInstruction.getInstructionName().equals("FORK")) {
                    handleFork(running);
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
                    while (numCycles != 0 && (timeQuantum > 0 || lockAvailable == false)) {

                        if(totalTime == OSDriver.quitTime && OSDriver.quitTime != 0){
                            System.exit(0);
                        }

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
                        System.out.println("Runtime remaining: " + running.getRuntime());
                        System.out.println("Process variable: " + running.getVariable());
                    }
                }
                if(timeQuantum > 0 || lockAvailable == false) {
                    index++;
                    running.setIndex(index);
                }
            }

            if(running.getIndex() >= instructions.size() && !running.getName().equals("Child")){
                Dispatcher.handleTermination(processes);
            }

        }
        if(timeQuantum <= 0 && OSDriver.schedulerFlag == 1){
            Dispatcher.handleTimeout(processes);
        }

    }


    private static void handleFork(Process parent)throws FileNotFoundException {
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

    }



}
