package src.com.example.osdriver;

import src.com.example.cpu.CPU;
import src.com.example.mmu.MMU;
import src.com.example.process.Process;
import src.com.example.dispatcher.Dispatcher;
import src.com.example.instruction.Instruction;
import src.com.example.scheduler.Scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static src.com.example.mmu.MMU.checkLimitRR;

/*
** This is the driver class for the OS simulator
 */
public class OSDriver {

    public static ArrayList<Process> waitingQueue = new ArrayList<Process>();
    public static int position = 0;
    public static int rrPosition = 0;
    public static int quitTime = 0;
    public static int numProcesses = 0;
    public static ArrayList<Process> compareProcesses = new ArrayList<Process>();
    public static ArrayList<Process> processes = new ArrayList<Process>();

    private static int state = 1;
    private static int minMemory = 0;
    private static int runtime = 0;
    private static int numParse = 0;
    private static int random = 0;
    private static int cyclePercent = 0;
    private static int numCycles = 0;
    private static int instructionIndex = 0;
    private static int index = 0;
    private static String name = " ";
    private static String parse = " ";
    private static String inputFileName = " ";
    private static Random rand = new Random();

    public static void main(String[] args) throws FileNotFoundException, InterruptedException{

        int numFiles = 0;
        Scanner input = new Scanner(System.in);
        System.out.println("Enter desired number of program files: ");
        numFiles = input.nextInt();
        System.out.println("Enter how many cycles for the simulator to run (0 indicates a complete run): ");
        quitTime = input.nextInt();

        /*
         ** This while loop loops the entire process for creating processes for each of the different files
         */
        while (numFiles != 0) {
            index = 0;
            //inputFileName = promptForFileName();
            System.out.println("Enter desired number of processes: ");
            numProcesses = input.nextInt();

            /*
             ** This while loop loops for the selected number of processes from a specifies
             */
            while (index < numProcesses) {
                collectProcesses();
            }
            numFiles--;

        }

        processes = memCheck(processes);
        getDispatcher();
    }


    /*
     ** promptForFileName simply prompts the user for the file name and returns the given string
     */
    public static String promptForFileName() {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter an input file name: ");
        String inputFileName = in.next();
        return inputFileName;
    }

    public static void collectProcesses()throws FileNotFoundException{
        inputFileName = promptForFileName();
        while(index < numProcesses) {
            File readFile = getFile(inputFileName);
            Scanner reader = new Scanner(readFile);
            parse = reader.nextLine();
            numParse = parse.indexOf("Name:") + 5;
            parse = parse.substring(numParse).trim();
            name = parse;
            parse = reader.nextLine();
            numParse = parse.indexOf("Memory:") + 7;
            parse = parse.substring(numParse).trim();
            minMemory = Integer.parseInt(parse);
            parse = reader.nextLine();
            numParse = parse.indexOf("Total runtime:") + 15;
            parse = parse.substring(numParse).trim();
            runtime = Integer.parseInt(parse);


            ArrayList<Instruction> instructions = new ArrayList<Instruction>();
            ArrayList<Integer> pageTable = new ArrayList<>();
            Process process = new Process(state, minMemory, runtime, name, instructions, instructionIndex, 0, 0, pageTable);
            runtime = 0;

            /*
             **This while loop reads in all the instructions of the process from the file
             */
            while (!parse.contains("EXE")) {

                /*
                 ** This reads in the calculate instruction and randomally edits
                 ** the number of cycles needed to complete the instruction
                 */
                if (parse.contains("CALCULATE")) {
                    numParse = parse.indexOf("CALCULATE") + 9;
                    parse = parse.substring(numParse).trim();
                    numCycles = Integer.parseInt(parse);
                    cyclePercent = numCycles / 5;
                    random = rand.nextInt(2);

                    if (random == 0) {
                        numCycles = numCycles - rand.nextInt(cyclePercent);
                    } else {
                        numCycles = numCycles + rand.nextInt(cyclePercent);
                    }

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
                    cyclePercent = numCycles / 5;
                    random = rand.nextInt(2);

                    if (random == 0) {
                        numCycles = numCycles - ((int) Math.random() * cyclePercent);
                    } else {
                        numCycles = numCycles + ((int) Math.random() * cyclePercent);
                    }

                    //runtime += numCycles;
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
                } else if (parse.contains("FORK")) {
                    numCycles = 0;
                    System.out.println("fork");
                    Instruction next = new Instruction("FORK", numCycles);
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
                    cyclePercent = numCycles / 5;
                    random = rand.nextInt(2);

                    if (random == 0) {
                        numCycles = numCycles - rand.nextInt(cyclePercent);
                    } else {
                        numCycles = numCycles + rand.nextInt(cyclePercent);
                    }

                    runtime += numCycles;
                    System.out.println("out");
                    Instruction next = new Instruction("OUT", numCycles);
                    instructions.add(next);
                }

                parse = reader.nextLine();
            }
            process.setInstructions(instructions);
            process.setRuntime(runtime);
            System.out.println(runtime);
            process.setPriority(rand.nextInt(10) + 1);
            process.setVariable(rand.nextInt(100) + 1);
            waitingQueue.add(process);
            compareProcesses.add(process);
            index++;
        }
    }

    /*
     ** getFile verifies that the file exists and returns it if it does exists
     ** and prompts the user again if it does not
     */
    private static File getFile(String fileName) {
        boolean isFile = false;
        File myFile = new File(fileName);

        if (myFile.exists()) {
            isFile = true;
        }

        if (!isFile) {
            System.out.println(fileName + " was not found.");
            System.out.println();
        }

        while (!isFile) {
            try {
                fileName = promptForFileName();
                myFile = new File(fileName);

                if (myFile.exists()) {
                    isFile = true;
                } else {
                    throw new FileNotFoundException();
                }
            } catch (FileNotFoundException e) {
                System.out.println();
                System.out.println(fileName + " was not found.");
                System.out.println();
            }
        }

        return myFile;
    }

    /*
     ** getDispatcher checks if there are any processes remaining and if there are it calls
     ** dispatch in the Dispatcher class
     */
    public static void getDispatcher() throws FileNotFoundException, InterruptedException {

        if (processes.size() == 0 && compareProcesses.size() == 0) {
            System.exit(0);
        }

        if (processes.size() == 0) {
            CPU.processesThread.interrupt();
            System.out.println("Thread interrupted");
        }


        if (CPU.rrProcessesThread.isInterrupted() && CPU.processesThread.isInterrupted()) {
            System.exit(0);
        }

        if (position == processes.size()) {
            position = 0;
        }

        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).getState() == 3 || processes.get(i).getState() == 5) {
                position++;
            }
        }
        if (processes.size() != 0 || !CPU.processesThread.isInterrupted()) {
            Dispatcher.dispatch(processes);
        } else {
            getRRDispatcher();
        }
    }

    public static void getRRDispatcher() throws FileNotFoundException, InterruptedException{

        if (processes.size() == 0 && compareProcesses.size() == 0) {
            System.exit(0);
        }

        if (compareProcesses.size() == 0) {
            CPU.rrProcessesThread.interrupt();
            System.out.println("RR thread interrupted");
        }

        if (CPU.rrProcessesThread.isInterrupted() && CPU.processesThread.isInterrupted()) {
            System.exit(0);
        }

        if(rrPosition == compareProcesses.size()){
            rrPosition = 0;
        }
        //rrMemCheck(compareProcesses);
        Dispatcher.rrDispatch(compareProcesses);
    }
    /*
     ** memCheck calls checkLimit to load all possible processes in memory
     */
    public static ArrayList<Process> memCheck(ArrayList<Process> processes) throws FileNotFoundException, InterruptedException{
        processes = MMU.checkLimit(processes);
        return Scheduler.scheduling(processes);
    }

    public static ArrayList<Process> rrMemCheck(ArrayList<Process> processes) throws FileNotFoundException, InterruptedException{
        processes = MMU.checkLimitRR(processes);
        return Scheduler.rrScheduling(processes);
    }

}
