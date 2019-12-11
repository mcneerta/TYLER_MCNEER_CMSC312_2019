package src.com.example.scheduler;

import src.com.example.process.Process;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Scheduler{


    public static ArrayList<Process> scheduling(ArrayList<Process> processes){
        Collections.sort(processes, new SJF());
        System.out.println("Number of processes: " + processes.size());
        return processes;
    }

    public static ArrayList<Process> rrScheduling(ArrayList<Process> processes){
        Collections.sort(processes, new RR());
        System.out.println("Number of processes: " + processes.size());
        return processes;
    }


    public static class SJF implements Comparator{

        public int compare(Object obj1, Object obj2){
            Process p1 = (Process) obj1;
            Process p2 = (Process) obj2;

            if(p1.getRuntime() == p2.getRuntime()){
                return 0;
            }

            else if(p1.getRuntime() > p2.getRuntime()){
                return 1;
            }

            else{
                return -1;
            }

        }
    }

    public static class RR implements Comparator{

        public int compare(Object obj1, Object obj2){
            Process p1 = (Process) obj1;
            Process p2 = (Process) obj2;

            if(p1.getPriority() == p2.getPriority()){
                return 0;
            }

            else if(p1.getPriority() > p2.getPriority()){
                return 1;
            }

            else{
                return -1;
            }

        }
    }

}
