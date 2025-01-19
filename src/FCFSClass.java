import java.util.*;

class Process {
    String processID;
    int arrivalTime;
    int burstTime;
    int completionTime;
    int turnAroundTime;
    int waitingTime;
    public int startTime;

    public Process(String processID, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }
}

public class FCFSClass {
    private List<Process> processes;

    public FCFSClass() {
        this.processes = new ArrayList<>();
    }

    public void addProcess(String processID, int arrivalTime, int burstTime) {
        processes.add(new Process(processID, arrivalTime, burstTime));
    }

    public void addProcess(int arrivalTime, int burstTime) {
        // Convert process number to letter (A, B, C, ...)
        char processLetter = (char)('A' + processes.size());
        addProcess(String.valueOf(processLetter), arrivalTime, burstTime);
    }

    public void execute() {
        // Create a copy of processes for sorting and scheduling
        List<Process> sortedProcesses = new ArrayList<>(processes);
        sortedProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        int totalWaitTime = 0, totalTurnAroundTime = 0;

        List<String> ganttChart = new ArrayList<>();
        List<Integer> timeMarkers = new ArrayList<>();
        timeMarkers.add(currentTime); // Initialize first time marker

        for (Process p : sortedProcesses) {
            if (currentTime < p.arrivalTime) {
                // Add idle time
                ganttChart.add("//");
                timeMarkers.add(p.arrivalTime);
                currentTime = p.arrivalTime;
            }
                
            ganttChart.add(p.processID);
            p.completionTime = currentTime + p.burstTime;
            p.turnAroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnAroundTime - p.burstTime;

            totalWaitTime += p.waitingTime;
            totalTurnAroundTime += p.turnAroundTime;

            currentTime = p.completionTime;
            timeMarkers.add(currentTime);
        }

        double avgWaitTime = (double) totalWaitTime / processes.size();
        double avgTurnaroundTime = (double) totalTurnAroundTime / processes.size();
        
        // Modified CPU utilization calculation
        int totalBurstTime = processes.stream()
                                    .mapToInt(p -> p.burstTime)
                                    .sum();
        double cpuUtilization = ((double) totalBurstTime / currentTime) * 100;

        displayResults(avgWaitTime, avgTurnaroundTime, cpuUtilization, ganttChart, timeMarkers);
    }

    private void displayResults(double avgWaitTime, double avgTurnaroundTime, double cpuUtilization,
            List<String> ganttChart, List<Integer> timeMarkers) {
        // Display process table
        System.out.println("\nProcess Table:");
        System.out.printf("%-12s %-14s %-10s %-16s %-18s %-14s\n",
                "Process ID", "Arrival Time", "Burst Time",
                "Completion Time", "Turnaround Time", "Waiting Time");

        for (Process p : processes) { // Use the original order
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

        // Top border
        System.out.print("+");
        for (int i = 0; i < ganttChart.size(); i++) {
            System.out.print("--------+");
        }
        System.out.println();

        // Process IDs
        System.out.print("|");
        for (String process : ganttChart) {
            System.out.printf(" %-6s |", process);
        }
        System.out.println();

        // Bottom border
        System.out.print("+");
        for (int i = 0; i < ganttChart.size(); i++) {
            System.out.print("--------+");
        }
        System.out.println();

        // Time markers
        for (Integer time : timeMarkers) {
            System.out.printf("%-9d", time);
        }
        System.out.println();
    }
}