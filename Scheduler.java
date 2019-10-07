import java.util.*;

public class Scheduler{

  /*
  ** Shortest job first scheduler using
  ** insertion sort algorithm to sort processes
  */
  public static ArrayList<Process> scheduling(ArrayList<Process> processes){
    Collections.sort(processes, new RuntimeComparator());
    System.out.println("Number of processes: " + processes.size());
    return processes;
  }


  public static class RuntimeComparator implements Comparator{

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

}
