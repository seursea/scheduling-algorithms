// Coded by: Michelle Eunice Opeña
import java.util.*;

public class RoundRobinClass {
    private List<Process> processes;  // List of processes
    private final int timeQuantum;    // Time quantum for round-robin scheduling

    // Constructor to initialize the RoundRobinClass with a given time quantum
    public RoundRobinClass(int timeQuantum) {
        this.processes = new ArrayList<>();
        this.timeQuantum = timeQuantum;
    }

    // Add a process by automatically generating a process ID (A, B, C, etc.)
    public void addProcess(int arrivalTime, int burstTime) {
        String processID = String.valueOf((char)('A' + processes.size())); // Generate names like A, B, C
        addProcess(processID, arrivalTime, burstTime);
    }

    // Add a process with the specified ID, arrival time, and burst time
    public void addProcess(String processID, int arrivalTime, int burstTime) {
        processes.add(new Process(processID, arrivalTime, burstTime));
    }

    // Execute the round-robin scheduling algorithm
    public void execute() {
        if (processes.isEmpty()) {
            System.out.println("No processes to schedule!");
            return;
        }

        // Create copies of processes for scheduling without modifying original list
        List<Process> remainingProcesses = new ArrayList<>();
        for (Process p : processes) {
            remainingProcesses.add(new Process(p.processID, p.arrivalTime, p.burstTime));
        }

        // Sort processes by arrival time to ensure correct scheduling order
        remainingProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));

        Queue<Process> readyQueue = new LinkedList<>();  // Ready queue for scheduling
        List<String> ganttChart = new ArrayList<>();     // Gantt chart to track execution
        List<Integer> timeMarkers = new ArrayList<>();   // Track time markers for Gantt chart
        
        int currentTime = 0;  // Track the current time in the scheduling process
        int[] remainingBurstTime = new int[processes.size()];  // Track remaining burst times for each process
        boolean[] completed = new boolean[processes.size()];    // Track if processes are completed
        
        // Initialize remaining burst times to the initial burst times of the processes
        for (int i = 0; i < processes.size(); i++) {
            remainingBurstTime[i] = processes.get(i).burstTime;
        }

        timeMarkers.add(currentTime); // Add initial time to time markers
        
        while (true) {
            boolean allCompleted = true;
            
            // Check for newly arrived processes and add them to the ready queue
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).arrivalTime <= currentTime) {
                Process arrived = remainingProcesses.remove(0);
                readyQueue.offer(arrived);
            }

            // If no processes in ready queue, skip to the time when the next process arrives
            if (readyQueue.isEmpty() && !remainingProcesses.isEmpty()) {
                ganttChart.add("//");  // Represent idle time
                currentTime = remainingProcesses.get(0).arrivalTime;
                timeMarkers.add(currentTime);
                continue;
            }

            // Process the ready queue if there are processes to execute
            if (!readyQueue.isEmpty()) {
                Process current = readyQueue.poll();  // Dequeue the next process
                int index = getProcessIndex(current.processID);  // Find index of current process
                
                // Set start time if the process hasn't started yet
                if (current.startTime == 0 && !completed[index]) {
                    current.startTime = currentTime;
                }

                // Execute the process for a duration of time quantum or remaining burst time
                int executeTime = Math.min(timeQuantum, remainingBurstTime[index]);
                remainingBurstTime[index] -= executeTime;
                currentTime += executeTime;

                ganttChart.add(current.processID);  // Add the process to the Gantt chart
                timeMarkers.add(currentTime);      // Add current time to time markers

                // Check if the process has completed
                if (remainingBurstTime[index] == 0 && !completed[index]) {
                    completed[index] = true;  // Mark as completed
                    processes.get(index).completionTime = currentTime;
                    processes.get(index).turnAroundTime = currentTime - processes.get(index).arrivalTime;
                    processes.get(index).waitingTime = processes.get(index).turnAroundTime - processes.get(index).burstTime;
                } else if (remainingBurstTime[index] > 0) {
                    // If process is not completed, add back to the ready queue
                    readyQueue.offer(current);
                }
            }

            // Check if all processes are completed
            for (boolean isCompleted : completed) {
                if (!isCompleted) {
                    allCompleted = false;
                    break;
                }
            }
            
            if (allCompleted) break;  // Exit if all processes are completed
        }
        
        // Display final results: process table and Gantt chart
        displayResults(ganttChart, timeMarkers);
    }
    
    // Helper method to find the index of a process by its ID
    private int getProcessIndex(String processID) {
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).processID.equals(processID)) {
                return i;
            }
        }
        return -1;  // Return -1 if process ID is not found
    }

    // Display the results: Process table, Gantt chart, and averages
    private void displayResults(List<String> ganttChart, List<Integer> timeMarkers) {
        // Print the process table
        System.out.println("\nProcess Table:");
        System.out.println("Process\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");
        
        int totalTurnaroundTime = 0, totalWaitingTime = 0, totalBurstTime = 0;

        // Calculate total turnaround time, waiting time, and burst time
        for (Process p : processes) {
            System.out.println(p.processID + "\t" + p.arrivalTime + "\t\t" + 
                             p.burstTime + "\t\t" + p.completionTime + "\t\t" + 
                             p.turnAroundTime + "\t\t" + p.waitingTime);
            
            totalTurnaroundTime += p.turnAroundTime;
            totalWaitingTime += p.waitingTime;
            totalBurstTime += p.burstTime;
        }

        // Display Gantt chart
        System.out.println("\nGantt Chart:");
        
        // Display Gantt chart borders
        System.out.print("+");
        for (int i = 0; i < ganttChart.size(); i++) {
            System.out.print("--------+");
        }
        System.out.println();
        
        // Display process IDs in Gantt chart
        System.out.print("|");
        for (String process : ganttChart) {
            System.out.printf(" %-6s |", process);
        }
        System.out.println();
        
        // Display bottom border of the Gantt chart
        System.out.print("+");
        for (int i = 0; i < ganttChart.size(); i++) {
            System.out.print("--------+");
        }
        System.out.println();
        
        // Display time markers
        for (Integer time : timeMarkers) {
            System.out.printf("%-9d", time);
        }
        System.out.println();

        // Calculate and display averages
        double avgTurnaroundTime = (double) totalTurnaroundTime / processes.size();
        double avgWaitingTime = (double) totalWaitingTime / processes.size();
    
        // Calculate CPU utilization
        int totalTime = timeMarkers.get(timeMarkers.size() - 1);
        double cpuUtilization = ((double) totalBurstTime / totalTime) * 100;

        // Print averages and CPU utilization
        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f ms\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }
}