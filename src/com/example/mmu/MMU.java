package src.com.example.mmu;


import src.com.example.osdriver.OSDriver;
import src.com.example.process.Process;

import java.util.ArrayList;

import static src.com.example.osdriver.OSDriver.compareProcesses;
import static src.com.example.osdriver.OSDriver.waitingQueue;

/*
** MMU simulates allowing processes to access memory
 */
public class MMU {

    /*
    ** memUsed is a variable for tracking the amount memory being used by processes
    ** memLimit is the max amount of memory the processes can use
     */
    public static int memUsed = 0;
    private static int memLimit = 250;

    /*
    ** checkLimit admits as many processes as possible into memory and returns an arrayList of the admitted processes
     */
    public static ArrayList<Process> checkLimit(ArrayList<Process> processes) {
        if(processes.size() == 0 && waitingQueue.size() == 0 && compareProcesses.size() == 0){
            System.exit(0);
        }

        else if(processes.size() == 0 && waitingQueue.size() == 0){
            OSDriver.getDispatcher(processes);
        }

        while(!waitingQueue.isEmpty()){
            Process p = waitingQueue.get(0);

            // Checks if the memory used + the memory of the admitted process is under the limit
            if (memUsed + p.getMemory() <= memLimit) {

                // If a process has not been admitted to memory it is added and removed from the waitingQueue
                if(p.getState() != 4) {
                    memUsed += p.getMemory();
                    p.setState(4);
                    processes.add(p);
                    waitingQueue.remove(0);
                    System.out.println("Added to memory!");
                }
            }
            else {
                System.out.println("Waiting for memory!");
                for(Process pro : waitingQueue){
                    System.out.println("Process waiting: " + pro.getName());
                }
                break;
            }
        }

        return processes;
    }

    public static ArrayList<Process> checkLimitRR(ArrayList<Process> compareProcesses){
        int count = compareProcesses.size();
        for(int i = 0; i < count; i++) {
            waitingQueue.add(compareProcesses.get(0));
            compareProcesses.remove(0);
        }

        if(compareProcesses.size() == 0 && waitingQueue.size() == 0){
            System.exit(0);
        }


        while(!waitingQueue.isEmpty()){
            Process p = waitingQueue.get(0);

            // Checks if the memory used + the memory of the admitted process is under the limit
            if (memUsed + p.getMemory() <= memLimit) {

                // If a process has not been admitted to memory it is added and removed from the waitingQueue
                if(p.getState() != 4) {
                    memUsed += p.getMemory();
                    p.setState(4);
                    p.setIndex(0);
                    compareProcesses.add(p);
                    waitingQueue.remove(0);
                    System.out.println("Added to memory!");
                }
            }
            else {
                System.out.println("Waiting for memory!");
                for(Process pro : waitingQueue){
                    System.out.println("Process waiting: " + pro.getName());
                }
                break;
            }
        }

        return compareProcesses;

    }
}
