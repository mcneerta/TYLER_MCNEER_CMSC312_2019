package src.com.example.processList;

import src.com.example.cpu.CPU;
import src.com.example.process.Process;

import java.util.ArrayList;

public class ProcessList extends Thread{
    private static ArrayList<Process> processes = new ArrayList<Process>();
    private static int totTime;

    public ProcessList(ArrayList<Process> processesList, int totalTime){
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
            CPU.processor(ProcessList.processes);
            //sleep(100);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}


