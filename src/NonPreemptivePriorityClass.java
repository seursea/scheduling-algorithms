// Coded by: Trizia Lorenz Ambagan
import java.util.ArrayList;
import java.util.Comparator;

public class NonPreemptivePriorityClass {
    private ArrayList<PriorityProcess> processes = new ArrayList<>(); // List of processes to be scheduled
    private ArrayList<PriorityProcess> completedProcesses = new ArrayList<>(); // List of completed processes

    // Method to add a process using specific attributes
    public void addProcess(String processID, int priority, int arrivalTime, int burstTime) {
        PriorityProcess process = new PriorityProcess(processID, priority, arrivalTime, burstTime);
        processes.add(process); // Add the process to the list
    }

    // Method to add a process with an automatically generated process ID
    public void addProcess(int priority, int arrivalTime, int burstTime) {
        String processID = String.valueOf((char) ('A' + processes.size())); // Generate a process ID based on the order
        addProcess(processID, priority, arrivalTime, burstTime); // Delegate to the other addProcess method
    }

    // Main scheduling execution method
    public void execute() {
        ArrayList<PriorityProcess> inputOrderProcesses = new ArrayList<>(processes); // Preserve the input order of processes

        int currentTime = 0; // Tracks the current time in the scheduling process
        int totalTurnaroundTime = 0; // Cumulative turnaround time
        int totalWaitingTime = 0; // Cumulative waiting time
        int idleTime = 0; // Total CPU idle time

        // Strings to construct the Gantt chart
        StringBuilder ganttChartTop = new StringBuilder();
        StringBuilder ganttChartBottom = new StringBuilder();
        StringBuilder ganttChartTime = new StringBuilder("0");

        while (!processes.isEmpty()) {
            final int currentTimeFinal = currentTime;
            // Find the highest-priority process available at the current time
            PriorityProcess currentProcess = processes.stream()
                    .filter(p -> p.arrivalTime <= currentTimeFinal) // Only consider processes that have arrived
                    .min(Comparator.comparingInt((PriorityProcess p) -> p.priority) // Select based on priority
                            .thenComparingInt(p -> p.arrivalTime)) // Break ties using arrival time
                    .orElse(null);

            if (currentProcess != null) {
                processes.remove(currentProcess); // Remove the selected process from the queue

                // Handle CPU idle time if the process arrives after the current time
                if (currentTime < currentProcess.arrivalTime) {
                    idleTime += currentProcess.arrivalTime - currentTime; // Add to idle time
                    ganttChartTop.append("+-----".repeat(currentProcess.arrivalTime - currentTime));
                    ganttChartBottom.append("| /// ".repeat(currentProcess.arrivalTime - currentTime));
                    ganttChartTime.append(String.format("%6d", currentProcess.arrivalTime));
                    currentTime = currentProcess.arrivalTime; // Advance current time
                }

                // Update process timings
                currentProcess.startTime = currentTime; 
                currentTime += currentProcess.burstTime; 
                currentProcess.completionTime = currentTime; 

                // Calculate and update turnaround and waiting times
                currentProcess.turnAroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnAroundTime - currentProcess.burstTime;

                totalTurnaroundTime += currentProcess.turnAroundTime;
                totalWaitingTime += currentProcess.waitingTime; 

                completedProcesses.add(currentProcess); // Add the process to the completed list

                // Append the process to the Gantt chart
                ganttChartTop.append("+-----");
                ganttChartBottom.append("|  ").append(currentProcess.processID).append("  ");
                ganttChartTime.append(String.format("%6d", currentTime)); 
            } else {
                // Handle cases where no process is ready to execute
                int nextArrival = processes.stream().mapToInt(p -> p.arrivalTime).min().orElse(currentTime + 1);
                ganttChartTop.append("+-----");
                ganttChartBottom.append("| /// ");
                ganttChartTime.append(String.format("%6d", nextArrival));
                idleTime += nextArrival - currentTime; 
                currentTime = nextArrival; 
            }
        }

        // Finalize the Gantt chart visuals
        ganttChartTop.append("+");
        ganttChartBottom.append("|");

        // Display all results after scheduling
        displayResults(inputOrderProcesses, totalTurnaroundTime, totalWaitingTime, idleTime, currentTime, ganttChartTop,
                ganttChartBottom, ganttChartTime);
    }

    // Method to display results: process details and performance metrics
    private void displayResults(ArrayList<PriorityProcess> inputOrderProcesses, int totalTurnaroundTime,
            int totalWaitingTime, int idleTime, int currentTime, StringBuilder ganttChartTop,
            StringBuilder ganttChartBottom, StringBuilder ganttChartTime) {
        System.out.println("\nProcess Table:");
        System.out
                .println("Process\tPriority\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");

        // Output details of all processes in their input order
        for (PriorityProcess p : inputOrderProcesses) {
            PriorityProcess completedProcess = completedProcesses.stream()
                    .filter(cp -> cp.processID.equals(p.processID)) // Match based on process ID
                    .findFirst()
                    .orElse(null);
            if (completedProcess != null) {
                System.out.println(completedProcess.processID + "\t" + completedProcess.priority + "\t\t"
                        + completedProcess.arrivalTime + "\t\t" + completedProcess.burstTime + "\t\t"
                        + completedProcess.completionTime + "\t\t" + completedProcess.turnAroundTime + "\t\t"
                        + completedProcess.waitingTime);
            }
        }

        // Display the constructed Gantt chart
        System.out.println("\nGantt Chart:");
        System.out.println(ganttChartTop);
        System.out.println(ganttChartBottom);
        System.out.println(ganttChartTop);
        System.out.println(ganttChartTime);

        // Calculate and display performance metrics
        double avgTurnaroundTime = (double) totalTurnaroundTime / completedProcesses.size();
        double avgWaitingTime = (double) totalWaitingTime / completedProcesses.size();
        double cpuUtilization = ((currentTime - idleTime) / (double) currentTime) * 100;

        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f ms\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }
}