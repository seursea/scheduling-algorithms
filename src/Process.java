// Source code is decompiled from a .class file using FernFlower decompiler.
class Process {
    String processID;
    int arrivalTime;
    int burstTime;
    int completionTime;
    int turnAroundTime;
    int waitingTime;
 
    public Process(String processID, int arrivalTime, int burstTime) {
       this.processID = processID;
       this.arrivalTime = arrivalTime;
       this.burstTime = burstTime;
    }
 }
 