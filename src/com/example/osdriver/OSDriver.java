package src.com.example.osdriver;

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

public class OSDriver{

    public static ArrayList<Process> waitingQueue = new ArrayList<Process>();

    public static void main(String[] args)throws FileNotFoundException{
        int numProcesses = 0;
        int numFiles = 0;
        int state = 1;
        int minMemory = 0;
        int runtime = 0;
        int numParse = 0;
        int random = 0;
        int cyclePercent = 0;
        int index = 0;
        int numCycles = 0;
        int instructionIndex = 0;
        String name = " ";
        String parse = " ";
        ArrayList<Process> processes = new ArrayList<Process>();
        Random rand = new Random();
        Scanner input = new Scanner(System.in);
        System.out.println("Enter desired number of program files: ");
        numFiles = input.nextInt();

        while(numFiles != 0){
            index = 0;
            String inputFileName = promptForFileName();
            System.out.println("Enter desired number of processes: ");
            numProcesses = input.nextInt();

            while(index < numProcesses){
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


                System.out.println(numProcesses);
                ArrayList<Instruction> instructions = new ArrayList<Instruction>();
                Process process = new Process(state, minMemory, runtime, name, instructions, instructionIndex);
                runtime = 0;

                while(!parse.contains("EXE")){

                    if(parse.contains("CALCULATE")){
                        numParse = parse.indexOf("CALCULATE") + 9;
                        parse = parse.substring(numParse).trim();
                        numCycles = Integer.parseInt(parse);
                        cyclePercent = numCycles/5;
                        random = rand.nextInt(2);
                        //System.out.println("Random: " + random);

                        if(random == 0){
                            numCycles = numCycles - rand.nextInt(cyclePercent);
                        }

                        else{
                            numCycles = numCycles + rand.nextInt(cyclePercent);
                        }

                        runtime+= numCycles;
                        System.out.println("calc");
                        Instruction next = new Instruction("CALCULATE", numCycles);
                        instructions.add(next);
                    }

                    else if(parse.contains("I/O")){
                        numParse = parse.indexOf("I/O") + 3;
                        parse = parse.substring(numParse).trim();
                        numCycles = Integer.parseInt(parse);
                        cyclePercent = numCycles/5;
                        random = rand.nextInt(2);

                        if(random == 0){
                            numCycles = numCycles - ((int) Math.random() * cyclePercent);
                        }

                        else{
                            numCycles = numCycles + ((int) Math.random() * cyclePercent);
                        }

                        runtime+= numCycles;
                        System.out.println("i/o");
                        Instruction next = new Instruction("I/O", numCycles);
                        instructions.add(next);
                    }

                    //Just sends program to waiting queue
                    else if(parse.contains("YIELD")){
                        numCycles = 0;
                        System.out.println("yield");
                        Instruction next = new Instruction("YIELD", numCycles);
                        instructions.add(next);
                    }

                    else if(parse.contains("OUT")){
                        numParse = parse.indexOf("OUT") + 3;
                        parse = parse.substring(numParse).trim();
                        numCycles = Integer.parseInt(parse);
                        cyclePercent = numCycles/5;
                        random = rand.nextInt(2);

                        if(random == 0){
                            numCycles = numCycles - rand.nextInt(cyclePercent);
                        }

                        else{
                            numCycles = numCycles + rand.nextInt(cyclePercent);
                        }

                        runtime+= numCycles;
                        System.out.println("out");
                        Instruction next = new Instruction("OUT", numCycles);
                        instructions.add(next);
                    }

                    parse = reader.nextLine();
                }
                process.setInstructions(instructions);
                process.setRuntime(runtime);
                System.out.println(runtime);
                waitingQueue.add(process);
                index++;
            }
numFiles--;

}
            processes = memCheck(processes);
            getDispatcher(processes, 0);
        }



    public static String promptForFileName(){
        Scanner in = new Scanner(System.in);
        System.out.println("Enter an input file name: ");
        String inputFileName = in.next();
        return inputFileName;
    }

    private static File getFile(String fileName){
        boolean isFile = false;
        File myFile = new File(fileName);

        if(myFile.exists()){
            isFile = true;
        }

        if(!isFile){
            System.out.println(fileName + " was not found.");
            System.out.println();
        }

        while(!isFile){
            try{
                fileName = promptForFileName();
                myFile = new File(fileName);

                if(myFile.exists()){
                    isFile = true;
                }
                else{
                    throw new FileNotFoundException();
                }
            }
            catch(FileNotFoundException e){
                System.out.println();
                System.out.println(fileName + " was not found.");
                System.out.println();
            }
        }

        return myFile;
    }

    public static void getDispatcher(ArrayList<Process> processes, int position){

        if(processes.size() == 0){
            System.exit(0);
        }

        if(position == processes.size()){
            position = 0;
        }

    for (int i = 0; i < processes.size(); i++) {
      if(processes.get(i).getState() == 3 || processes.get(i).getState() == 5){
        position++;
      }
    }

        Dispatcher.dispatch(processes, position);
    }

    public static ArrayList<Process> memCheck(ArrayList<Process> processes){
        processes = MMU.checkLimit(processes);
        return  Scheduler.scheduling(processes);
    }
}
