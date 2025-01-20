public class Process {
    String processID;      // Unique identifier for the process
    int arrivalTime;       // Time the process arrives in the system
    int burstTime;         // Duration required by the process for execution
    int completionTime;    // Time when the process completes execution
    int turnAroundTime;    // Time from process arrival to completion
    int waitingTime;       // Time the process spends waiting in the ready queue
    int startTime;         // Time when the process starts execution

    // Constructor to initialize a process with its ID, arrival time, and burst time
    public Process(String processID, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }
}
