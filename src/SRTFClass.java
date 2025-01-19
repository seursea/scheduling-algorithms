// Coded by: Jan Patrice Pasacsac
import java.util.*;

public class SRTFClass {
    private List<Process> processes;

    public SRTFClass() {
        this.processes = new ArrayList<>();
    }

    // Method to add a process using process ID, arrival time, and burst time
    public void addProcess(String processID, int arrivalTime, int burstTime) {
        processes.add(new Process(processID, arrivalTime, burstTime));
    }

    // Method to add a process using only arrival time and burst time
    public void addProcess(int arrivalTime, int burstTime) {
        // Convert process number to letter (1->A, 2->B, etc.)
        char processLetter = (char)('A' + processes.size());
        addProcess(String.valueOf(processLetter), arrivalTime, burstTime);
    }

    // Executes the Shortest Remaining Time First (SRTF) algorithm
    public void execute() {
        // Sort processes by their arrival times
        List<Process> sortedProcesses = new ArrayList<>(processes);
        sortedProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int n = sortedProcesses.size();
        int[] remainingTime = new int[n];
        for (int i = 0; i < n; i++) {
            remainingTime[i] = sortedProcesses.get(i).burstTime;
        }

        // Initialize variables to keep track of the current state
        int complete = 0, currentTime = 0, minTime = Integer.MAX_VALUE;
        int shortest = 0, finishTime;
        boolean check = false;

        List<String> ganttChart = new ArrayList<>();
        List<Integer> timeMarkers = new ArrayList<>();
        boolean isIdle = true;

        // Main loop to schedule the processes
        while (complete != n) {
            // Check if a new process has arrived and if it has the shortest remaining time
            for (int j = 0; j < n; j++) {
                if ((sortedProcesses.get(j).arrivalTime <= currentTime) &&
                        (remainingTime[j] < minTime) && remainingTime[j] > 0) {
                    minTime = remainingTime[j];
                    shortest = j;
                    check = true;
                }
            }

            // If no process is ready, increment the current time
            if (!check) {
                if (isIdle) {
                    ganttChart.add("//");
                    timeMarkers.add(currentTime);
                    isIdle = false;
                }
                currentTime++;
                continue;
            }

            // Record the first process execution time
            if (!isIdle) {
                isIdle = true;
                timeMarkers.add(currentTime);
            }

            // Decrease the remaining time for the shortest process
            remainingTime[shortest]--;
            minTime = remainingTime[shortest];
            if (minTime == 0) {
                minTime = Integer.MAX_VALUE;
            }

            // If process is completed, calculate its times and mark it as complete
            if (remainingTime[shortest] == 0) {
                complete++;
                check = false;

                finishTime = currentTime + 1;
                sortedProcesses.get(shortest).completionTime = finishTime;
                sortedProcesses.get(shortest).turnAroundTime = finishTime - sortedProcesses.get(shortest).arrivalTime;
                sortedProcesses.get(shortest).waitingTime = sortedProcesses.get(shortest).turnAroundTime
                        - sortedProcesses.get(shortest).burstTime;
            }

            // Update the Gantt chart with the current process ID
            if (ganttChart.isEmpty()
                    || !ganttChart.get(ganttChart.size() - 1).equals(sortedProcesses.get(shortest).processID)) {
                ganttChart.add(sortedProcesses.get(shortest).processID);
                if (timeMarkers.isEmpty() || timeMarkers.get(timeMarkers.size() - 1) != currentTime) {
                    timeMarkers.add(currentTime);
                }
            }

            currentTime++;
        }

        // Add the final time marker
        timeMarkers.add(currentTime);

        // Calculate total times for all processes
        int totalWaitTime = 0, totalTurnAroundTime = 0, totalBurstTime = 0;
        for (Process p : sortedProcesses) {
            totalWaitTime += p.waitingTime;
            totalTurnAroundTime += p.turnAroundTime;
            totalBurstTime += p.burstTime;
        }

        // Display the results
        displayResults(totalWaitTime, totalTurnAroundTime, totalBurstTime, currentTime,
                sortedProcesses, ganttChart, timeMarkers);
    }

    // Method to display the results
    private void displayResults(int totalWaitTime, int totalTurnAroundTime, int totalBurstTime,
            int currentTime, List<Process> sortedProcesses,
            List<String> ganttChart, List<Integer> timeMarkers) {

        // Calculate averages and CPU utilization
        double avgWaitTime = (double) totalWaitTime / processes.size();
        double avgTurnaroundTime = (double) totalTurnAroundTime / processes.size();
        double cpuUtilization = ((double) totalBurstTime / currentTime) * 100;

        // Print process table
        System.out.println("\nProcess Table:");
        System.out.printf("%-12s %-14s %-10s %-16s %-18s %-14s\n",
                "Process ID", "Arrival Time", "Burst Time",
                "Completion Time", "Turnaround Time", "Waiting Time");

        for (Process p : processes) {
            System.out.printf("%-12s %-14d %-10d %-16d %-18d %-14d\n",
                    p.processID, p.arrivalTime, p.burstTime,
                    p.completionTime, p.turnAroundTime, p.waitingTime);
        }

        // Print average times and CPU utilization
        System.out.printf("\nAverage Waiting Time: %.2f ms\n", avgWaitTime);
        System.out.printf("Average Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);

        // Print Gantt chart
        System.out.println("\nGantt Chart:");
        System.out.print("+");
        for (int i = 0; i < ganttChart.size(); i++) {
            System.out.print("--------+");
        }
        System.out.println();

        System.out.print("|");
        for (String process : ganttChart) {
            System.out.printf(" %-6s |", process);
        }
        System.out.println();

        System.out.print("+");
        for (int i = 0; i < ganttChart.size(); i++) {
            System.out.print("--------+");
        }
        System.out.println();

        for (Integer time : timeMarkers) {
            System.out.printf("%-9d", time);
        }
        System.out.println();
    }
}