package src.com.example.mmu;


import src.com.example.process.Process;

import java.util.ArrayList;

import static src.com.example.osdriver.OSDriver.waitingQueue;

public class MMU {
    public static int memUsed = 0;
    private static int memLimit = 199;

    public static ArrayList<Process> checkLimit(ArrayList<Process> processes) {
        if(processes.size() == 0 && waitingQueue.size() == 0){
            System.exit(0);
        }
        while(!waitingQueue.isEmpty()){
            Process p = waitingQueue.get(0);
            if (memUsed + p.getMemory() <= memLimit) {
                if(p.getState() != 4) {
                    memUsed += p.getMemory();
                    p.setState(4);
                    processes.add(p);
                    waitingQueue.remove(0);
                    System.out.println("Added to memory!");
                }
            }
            else {
              /*  for(int i = 0; i < count; i++){
                    waitingQueue.remove(0);
                } */
                System.out.println("Waiting for memory!");
                for(Process pro : waitingQueue){
                    System.out.println("Process waiting: " + pro.getName());
                }
                break;
            }
        }

        return processes;
    }
}
