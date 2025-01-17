public class Process {
   public String processID;
   public int arrivalTime;
   public int burstTime;
   public int startTime;
   public int completionTime;
   public int turnAroundTime;
   public int waitingTime;
   
   public Process(String processID, int arrivalTime, int burstTime) {
      this.processID = processID;
      this.arrivalTime = arrivalTime;
      this.burstTime = burstTime;
   }
}