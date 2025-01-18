import java.util.ArrayList;
import java.util.Comparator;

public class NonPreemptivePriorityClass {
    private ArrayList<PriorityProcess> processes = new ArrayList<>(); // List to hold all processes
    private ArrayList<PriorityProcess> completedProcesses = new ArrayList<>(); // List to hold completed processes

    // Method to add a process with a specific ID, priority, arrival time, and burst
    // time
    public void addProcess(String processID, int priority, int arrivalTime, int burstTime) {
        PriorityProcess process = new PriorityProcess(processID, priority, arrivalTime, burstTime);
        processes.add(process); // Add process to the list of processes
    }

    // Overloaded method to add a process with automatically assigned ID, priority,
    // arrival time, and burst time
    public void addProcess(int priority, int arrivalTime, int burstTime) {
        String processID = String.valueOf((char) ('A' + processes.size())); // Generate process ID (A, B, C, etc.)
        addProcess(processID, priority, arrivalTime, burstTime); // Call the other addProcess method
    }

    // Method to execute the processes based on non-preemptive priority scheduling
    public void execute() {
        ArrayList<PriorityProcess> inputOrderProcesses = new ArrayList<>(processes); // Copy to retain input order for
                                                                                     // printing

        int currentTime = 0; // Keeps track of the current time in the scheduling
        int totalTurnaroundTime = 0; // Total turnaround time for all processes
        int totalWaitingTime = 0; // Total waiting time for all processes
        int idleTime = 0; // Tracks idle time when no process is running

        // StringBuilders for Gantt chart visualization
        StringBuilder ganttChartTop = new StringBuilder();
        StringBuilder ganttChartBottom = new StringBuilder();
        StringBuilder ganttChartTime = new StringBuilder("0");

        // Main scheduling loop: continues until all processes are completed
        while (!processes.isEmpty()) {
            // Find the highest priority process available at the current time
            final int currentTimeFinal = currentTime;
            PriorityProcess currentProcess = processes.stream()
                    .filter(p -> p.arrivalTime <= currentTimeFinal) // Filter by arrival time <= currentTime
                    .min(Comparator.comparingInt((PriorityProcess p) -> p.priority) // Sort by priority (highest first)
                            .thenComparingInt(p -> p.arrivalTime)) // Tie-breaking by arrival time
                    .orElse(null); // Return null if no process found

            if (currentProcess != null) {
                processes.remove(currentProcess); // Remove the selected process from the list

                // If current time is less than process arrival time, idle for the difference
                if (currentTime < currentProcess.arrivalTime) {
                    idleTime += currentProcess.arrivalTime - currentTime; // Account for idle time
                    ganttChartTop.append("+-----".repeat(currentProcess.arrivalTime - currentTime));
                    ganttChartBottom.append("| /// ".repeat(currentProcess.arrivalTime - currentTime));
                    ganttChartTime.append(String.format("%6d", currentProcess.arrivalTime));
                    currentTime = currentProcess.arrivalTime; // Set current time to process arrival time
                }

                currentProcess.startTime = currentTime; // Record the start time of the process
                currentTime += currentProcess.burstTime; // Increment current time by process burst time
                currentProcess.completionTime = currentTime; // Record the completion time of the process

                // Calculate turnaround and waiting times for the process
                currentProcess.turnAroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnAroundTime - currentProcess.burstTime;

                totalTurnaroundTime += currentProcess.turnAroundTime; // Accumulate total turnaround time
                totalWaitingTime += currentProcess.waitingTime; // Accumulate total waiting time

                completedProcesses.add(currentProcess); // Add the completed process to the list

                // Update the Gantt chart with the current process
                ganttChartTop.append("+-----");
                ganttChartBottom.append("|  ").append(currentProcess.processID).append("  ");
                ganttChartTime.append(String.format("%6d", currentTime)); // Format the time labels
            } else {
                // If no process is ready to execute, simulate idle time
                int nextArrival = processes.stream().mapToInt(p -> p.arrivalTime).min().orElse(currentTime + 1); // Find next arrival time                                                                                                 // time
                ganttChartTop.append("+-----");
                ganttChartBottom.append("| /// ");
                ganttChartTime.append(String.format("%6d", nextArrival));
                idleTime += nextArrival - currentTime; // Increment idle time
                currentTime = nextArrival; // Jump to the next arrival time
            }
        }

        // Finalize the Gantt chart
        ganttChartTop.append("+");
        ganttChartBottom.append("|");

        // Display the results of the scheduling
        displayResults(inputOrderProcesses, totalTurnaroundTime, totalWaitingTime, idleTime, currentTime, ganttChartTop,
                ganttChartBottom, ganttChartTime);
    }

    // Method to display the results of the scheduling, including a process table
    // and Gantt chart
    private void displayResults(ArrayList<PriorityProcess> inputOrderProcesses, int totalTurnaroundTime,
            int totalWaitingTime, int idleTime, int currentTime, StringBuilder ganttChartTop,
            StringBuilder ganttChartBottom, StringBuilder ganttChartTime) {
        System.out.println("\nProcess Table:");
        System.out
                .println("Process\tPriority\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");

        // Print details of each process in the order they were added
        for (PriorityProcess p : inputOrderProcesses) {
            // Find the completed process by matching its process ID
            PriorityProcess completedProcess = completedProcesses.stream()
                    .filter(cp -> cp.processID.equals(p.processID))
                    .findFirst()
                    .orElse(null);
            if (completedProcess != null) {
                // Print the completed process's details
                System.out.println(completedProcess.processID + "\t" + completedProcess.priority + "\t\t"
                        + completedProcess.arrivalTime + "\t\t" + completedProcess.burstTime + "\t\t"
                        + completedProcess.completionTime + "\t\t" + completedProcess.turnAroundTime + "\t\t"
                        + completedProcess.waitingTime);
            }
        }

        // Print the Gantt chart
        System.out.println("\nGantt Chart:");
        System.out.println(ganttChartTop);
        System.out.println(ganttChartBottom);
        System.out.println(ganttChartTop);
        System.out.println(ganttChartTime);

        // Calculate and print the average turnaround time, waiting time, and CPU
        // utilization
        double avgTurnaroundTime = (double) totalTurnaroundTime / completedProcesses.size();
        double avgWaitingTime = (double) totalWaitingTime / completedProcesses.size();
        double cpuUtilization = ((currentTime - idleTime) / (double) currentTime) * 100;

        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f ms\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }
}