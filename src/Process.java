// The Process class represents a process with various time attributes used in scheduling algorithms
public class Process {
   
   // Process ID (e.g., "A", "B", "C", etc.)
   public String processID;
   
   // Arrival time of the process (the time the process enters the ready queue)
   public int arrivalTime;

   // Burst time (CPU time required for the process to complete)
   public int burstTime;
   
   // Start time (the time when the process begins execution)
   public int startTime;
   
   // Completion time (the time when the process finishes execution)
   public int completionTime;
   
   // Turnaround time (the total time the process spends in the system)
   // Turnaround Time = Completion Time - Arrival Time
   public int turnAroundTime;
   
   // Waiting time (the total time the process spends waiting in the ready queue)
   // Waiting Time = Turnaround Time - Burst Time
   public int waitingTime;
   
   // Constructor to initialize a new Process with given values
   public Process(String processID, int arrivalTime, int burstTime) {
      this.processID = processID;    // Set the process ID
      this.arrivalTime = arrivalTime; // Set the arrival time
      this.burstTime = burstTime;     // Set the burst time
   }
}