// Coded by: Jan Patrice Pasacsac
import java.util.*;

// Implements the First-Come, First-Served (FCFS) scheduling algorithm
public class FCFSClass {
    private List<Process> processes; // List to store all processes

    // Constructor to initialize the process list
    public FCFSClass() {
        this.processes = new ArrayList<>();
    }

    // Adds a process with a specific process ID
    public void addProcess(String processID, int arrivalTime, int burstTime) {
        processes.add(new Process(processID, arrivalTime, burstTime));
    }

    // Adds a process with an auto-generated process ID (A, B, C, ...)
    public void addProcess(int arrivalTime, int burstTime) {
        char processLetter = (char) ('A' + processes.size()); // Generate process ID
        addProcess(String.valueOf(processLetter), arrivalTime, burstTime);
    }

    // Executes the FCFS scheduling algorithm
    public void execute() {
        // Sort processes based on their arrival time
        List<Process> sortedProcesses = new ArrayList<>(processes);
        sortedProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0; // Tracks the current time in the system
        int totalWaitTime = 0, totalTurnAroundTime = 0; // Accumulate metrics

        List<String> ganttChart = new ArrayList<>(); // Tracks process execution order
        List<Integer> timeMarkers = new ArrayList<>(); // Tracks time intervals
        timeMarkers.add(currentTime); // Start time marker

        for (Process p : sortedProcesses) {
            // Handle idle time if no process is available
            if (currentTime < p.arrivalTime) {
                ganttChart.add("//"); // Represents idle time
                timeMarkers.add(p.arrivalTime);
                currentTime = p.arrivalTime;
            }

            // Add the process to the Gantt chart
            ganttChart.add(p.processID);

            // Compute process metrics
            p.startTime = currentTime;
            p.completionTime = currentTime + p.burstTime;
            p.turnAroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnAroundTime - p.burstTime;

            // Update cumulative metrics
            totalWaitTime += p.waitingTime;
            totalTurnAroundTime += p.turnAroundTime;

            // Update current time and time markers
            currentTime = p.completionTime;
            timeMarkers.add(currentTime);
        }

        // Calculate average metrics and CPU utilization
        double avgWaitTime = (double) totalWaitTime / processes.size();
        double avgTurnaroundTime = (double) totalTurnAroundTime / processes.size();
        int totalBurstTime = processes.stream().mapToInt(p -> p.burstTime).sum();
        double cpuUtilization = ((double) totalBurstTime / currentTime) * 100;

        // Display the results
        displayResults(avgWaitTime, avgTurnaroundTime, cpuUtilization, ganttChart, timeMarkers);
    }

    // Displays the process table, Gantt chart, and computed metrics
    private void displayResults(double avgWaitTime, double avgTurnaroundTime, double cpuUtilization,
                                List<String> ganttChart, List<Integer> timeMarkers) {
        // Display process table header
        System.out.println("\nProcess Table:");
        System.out.printf("%-12s %-14s %-10s %-16s %-18s %-14s\n",
                "Process ID", "Arrival Time", "Burst Time",
                "Completion Time", "Turnaround Time", "Waiting Time");

        // Display each process's details
        for (Process p : processes) {
            System.out.printf("%-12s %-14d %-10d %-16d %-18d %-14d\n",
                    p.processID, p.arrivalTime, p.burstTime,
                    p.completionTime, p.turnAroundTime, p.waitingTime);
        }

        // Display metrics
        System.out.printf("\nAverage Waiting Time: %.2f ms\n", avgWaitTime);
        System.out.printf("Average Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);

        // Display Gantt chart
        System.out.println("\nGantt Chart:");

        // Display Gantt chart top border
        System.out.print("+");
        for (int i = 0; i < ganttChart.size(); i++) {
            System.out.print("--------+");
        }
        System.out.println();

        // Display process IDs in the Gantt chart
        System.out.print("|");
        for (String process : ganttChart) {
            System.out.printf(" %-6s |", process);
        }
        System.out.println();

        // Display Gantt chart bottom border
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
    }
}