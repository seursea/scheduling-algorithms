public class PriorityProcess {
    String processID;
    int arrivalTime;
    int burstTime;
    int priority;
    int remainingTime;
    int startTime = -1;
    int completionTime;
    int turnAroundTime;
    int waitingTime;

    public PriorityProcess(String processID, int priority, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }
}
