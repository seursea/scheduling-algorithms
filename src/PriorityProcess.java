public class PriorityProcess {

    // Process attributes
    String processID;      // Unique identifier for the process
    int arrivalTime;       // Time when the process arrives in the ready queue
    int burstTime;         // Total CPU time needed to complete the process
    int priority;          // Priority of the process (lower value means higher priority)
    int remainingTime;     // Remaining time to complete the process (used in preemptive scheduling)
    int startTime = -1;    // Start time of the process (default -1 means not yet started)
    int completionTime;    // Time when the process completes execution
    int turnAroundTime;    // Time spent in the system (completionTime - arrivalTime)
    int waitingTime;       // Time spent waiting in the ready queue (turnAroundTime - burstTime)

    // Constructor to initialize the process with its values
    public PriorityProcess(String processID, int priority, int arrivalTime, int burstTime) {
        this.processID = processID;        // Set the process identifier
        this.priority = priority;          // Set the priority level
        this.arrivalTime = arrivalTime;    // Set the arrival time
        this.burstTime = burstTime;        // Set the burst time
        this.remainingTime = burstTime;    // Initialize remaining time to burst time
    }
}