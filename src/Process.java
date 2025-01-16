public class Process {
   String processID;
     int arrivalTime;
     int burstTime;
     int startTime;
     int completionTime;
     int turnAroundTime;
     int waitingTime;

   public Process(String processID, int arrivalTime, int burstTime) {
      this.processID = processID;
      this.arrivalTime = arrivalTime;
      this.burstTime = burstTime;
   }
}