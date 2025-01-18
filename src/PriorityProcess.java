// The PriorityProcess class represents a process in a priority-based scheduling algorithm
public class PriorityProcess {
    
    // Process ID (e.g., "A", "B", "C", etc.)
    String processID;
    
    // Arrival time of the process (the time the process enters the ready queue)
    int arrivalTime;
    
    // Burst time (CPU time required for the process to complete)
    int burstTime;
    
    // Priority of the process (used for priority scheduling)
    // Lower values typically represent higher priority
    int priority;
    
    // Remaining time of the process (used for preemptive scheduling to track execution)
    int remainingTime;
    
    // Start time of the process (the time when the process begins execution)
    // Initialized to -1 to indicate it hasn't started yet
    int startTime = -1;
    
    // Completion time (the time when the process finishes execution)
    int completionTime;
    
    // Turnaround time (the total time the process spends in the system)
    // Turnaround Time = Completion Time - Arrival Time
    int turnAroundTime;
    
    // Waiting time (the total time the process spends waiting in the ready queue)
    // Waiting Time = Turnaround Time - Burst Time
    int waitingTime;

    // Constructor to initialize a new PriorityProcess with given values
    public PriorityProcess(String processID, int priority, int arrivalTime, int burstTime) {
        this.processID = processID;    // Set the process ID
        this.priority = priority;      // Set the priority of the process
        this.arrivalTime = arrivalTime; // Set the arrival time
        this.burstTime = burstTime;     // Set the burst time
        this.remainingTime = burstTime; // Initialize remaining time to burst time (for preemptive scheduling)
    }
}
