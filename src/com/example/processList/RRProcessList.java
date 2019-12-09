package src.com.example.processList;

import src.com.example.cpu.CPU;
import src.com.example.process.Process;

import java.util.ArrayList;

public class RRProcessList extends Thread{
    private static ArrayList<Process> processes = new ArrayList<Process>();
    private static int totTime;

    public RRProcessList(ArrayList<Process> processesList, int totalTime){
        processes = processesList;
        totTime = totalTime;
    }

    public void setTime(int time){
        totTime = time;
    }

    public int getTime(){
        return totTime;
    }
    @Override
    synchronized public void run(){
        try{
            CPU.rrProcessor(RRProcessList.processes);
            //sleep(100);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}