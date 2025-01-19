// Coded by: Jossel John Dumaop

import java.util.ArrayList;
import java.util.Comparator;

public class PreemptivePriorityClass {
    // List of processes, ready queue, and completed processes
    private ArrayList<PriorityProcess> processes = new ArrayList<>();
    private ArrayList<PriorityProcess> readyQueue = new ArrayList<>();
    private ArrayList<PriorityProcess> completedProcesses = new ArrayList<>();

    // Method to add a process with a specified ID
    public void addProcess(String processID, int priority, int arrivalTime, int burstTime) {
        PriorityProcess process = new PriorityProcess(processID, priority, arrivalTime, burstTime);
        processes.add(process);
    }

    // Method to add a process with an auto-generated ID
    public void addProcess(int priority, int arrivalTime, int burstTime) {
        String processID = String.valueOf((char) ('A' + processes.size())); // Generate ID from A, B, C...
        addProcess(processID, priority, arrivalTime, burstTime);
    }

    // Main execution method for the preemptive priority scheduling
    public void execute() {
        // Save original process order for the results table
        ArrayList<PriorityProcess> originalProcesses = new ArrayList<>(processes);

        // Sort processes by arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0; // Current time in the simulation
        int totalTurnaroundTime = 0; // Accumulates turnaround time for all processes
        int totalWaitingTime = 0; // Accumulates waiting time for all processes
        int idleTime = 0; // Tracks CPU idle time

        // Gantt chart components
        StringBuilder ganttChartTop = new StringBuilder();
        StringBuilder ganttChartBottom = new StringBuilder();
        StringBuilder ganttChartTime = new StringBuilder();

        String lastProcessID = null; // Tracks the last process ID for Gantt chart continuity

        // Initialize Gantt chart with start time
        ganttChartTime.append(String.format("%5d ", currentTime));

        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            // Move processes that have arrived by currentTime into the ready queue
            while (!processes.isEmpty() && processes.get(0).arrivalTime <= currentTime) {
                readyQueue.add(processes.remove(0));
            }

            // Sort ready queue by priority, breaking ties by process ID
            readyQueue.sort(Comparator.comparingInt((PriorityProcess p) -> p.priority)
                    .thenComparingInt(p -> p.processID.charAt(0)));

            if (!readyQueue.isEmpty()) {
                PriorityProcess currentProcess = readyQueue.remove(0);

                // Set start time if the process is being executed for the first time
                if (currentProcess.startTime == -1) {
                    currentProcess.startTime = currentTime;
                }

                // Calculate execution time for the process
                int nextArrivalTime = processes.isEmpty() ? Integer.MAX_VALUE : processes.get(0).arrivalTime;
                int executionTime = Math.min(currentProcess.remainingTime, nextArrivalTime - currentTime);

                // Execute the process for the determined execution time
                currentProcess.remainingTime -= executionTime;
                currentTime += executionTime;

                // Update Gantt chart if the process ID has changed
                if (!currentProcess.processID.equals(lastProcessID)) {
                    ganttChartTop.append("+-----");
                    ganttChartBottom.append("|  ").append(currentProcess.processID).append("  ");

                    // Ensure the current time is appended only once
                    if (!ganttChartTime.toString().endsWith(String.format("%5d ", currentTime - executionTime))) {
                        ganttChartTime.append(String.format("%5d ", currentTime - executionTime));
                    }

                    lastProcessID = currentProcess.processID;
                }

                // Process completion logic
                if (currentProcess.remainingTime == 0) {
                    currentProcess.completionTime = currentTime;
                    currentProcess.turnAroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnAroundTime - currentProcess.burstTime;

                    totalTurnaroundTime += currentProcess.turnAroundTime;
                    totalWaitingTime += currentProcess.waitingTime;

                    completedProcesses.add(currentProcess);
                } else {
                    // Re-add process to the ready queue if it is not yet completed
                    readyQueue.add(currentProcess);
                }
            } else {
                // Handle CPU idle time
                ganttChartTop.append("+-----");
                ganttChartBottom.append("| /// ");

                // Append idle time start to Gantt chart
                if (!ganttChartTime.toString().endsWith(String.format("%5d ", currentTime))) {
                    ganttChartTime.append(String.format("%5d ", currentTime));
                }

                idleTime += processes.get(0).arrivalTime - currentTime;
                currentTime = processes.get(0).arrivalTime; // Advance time to the next process arrival
                lastProcessID = null; // Reset process tracking for Gantt chart
            }
        }

        // Append final time to Gantt chart
        if (!ganttChartTime.toString().endsWith(String.format("%5d ", currentTime))) {
            ganttChartTime.append(String.format("%5d ", currentTime));
        }
        ganttChartTop.append("+");
        ganttChartBottom.append("|");

        // Display results in a table and the Gantt chart
        displayResults(totalTurnaroundTime, totalWaitingTime, idleTime, currentTime, ganttChartTop, ganttChartBottom, ganttChartTime, originalProcesses);
    }

    // Displays the results including process table, Gantt chart, and statistics
    private void displayResults(int totalTurnaroundTime, int totalWaitingTime, int idleTime, int currentTime, StringBuilder ganttChartTop, StringBuilder ganttChartBottom, StringBuilder ganttChartTime, ArrayList<PriorityProcess> originalProcesses) {
        System.out.println("\nProcess Table:");
        System.out.println("Process\tPriority\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");

        // Iterate through original process list for table display
        for (PriorityProcess p : originalProcesses) {
            // Locate and display completed process details
            for (PriorityProcess completedProcess : completedProcesses) {
                if (completedProcess.processID.equals(p.processID)) {
                    System.out.println(p.processID + "\t" + p.priority + "\t\t" + p.arrivalTime + "\t\t" + p.burstTime + "\t\t" + completedProcess.completionTime + "\t\t" + completedProcess.turnAroundTime + "\t\t" + completedProcess.waitingTime);
                    break;
                }
            }
        }

        // Print Gantt chart
        System.out.println("\nGantt Chart:");
        System.out.println(ganttChartTop.toString());
        System.out.println(ganttChartBottom.toString());
        System.out.println(ganttChartTop.toString());
        System.out.println(ganttChartTime.toString().trim());

        // Calculate and display averages and CPU utilization
        double avgTurnaroundTime = (double) totalTurnaroundTime / completedProcesses.size();
        double avgWaitingTime = (double) totalWaitingTime / completedProcesses.size();
        double cpuUtilization = ((currentTime - idleTime) / (double) currentTime) * 100;

        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f ms\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }
}