import java.util.ArrayList;
import java.util.Comparator;

public class SJFClass {

    // Inner class representing a process with its attributes
    public class Process {
        String processID;  // Process ID (e.g., A, B, C)
        int arrivalTime;   // Arrival time of the process
        int burstTime;     // Burst time (CPU time required for execution)
        int startTime;     // Start time of the process
        int completionTime; // Completion time when the process finishes
        int turnAroundTime; // Turnaround time (completionTime - arrivalTime)
        int waitingTime;   // Waiting time (turnaroundTime - burstTime)

        // Constructor to initialize the process
        public Process(String processID, int arrivalTime, int burstTime) {
            this.processID = processID;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
        }
    }

    // List to hold all processes and the sorted processes
    private ArrayList<Process> processes = new ArrayList<>();
    private ArrayList<Process> sortedProcesses = new ArrayList<>();

    // Method to add a process using process ID, arrival time, and burst time
    public void addProcess(String processID, int arrivalTime, int burstTime) {
        Process process = new Process(processID, arrivalTime, burstTime);
        processes.add(process);
        sortedProcesses.add(process);  // Initially adding to sorted list too
    }

    // Overloaded method to add process by just arrival time and burst time
    public void addProcess(int arrivalTime, int burstTime) {
        String processID = String.valueOf((char) ('A' + processes.size()));  // Auto generate process ID (A, B, C, ...)
        addProcess(processID, arrivalTime, burstTime);
    }

    // Method to execute the Shortest Job First (SJF) algorithm
    public void execute() {
        // Sort processes based on their arrival time
        sortedProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0; // Keeps track of the current time in the scheduler
        int totalTurnaroundTime = 0; // To accumulate total turnaround time
        int totalWaitingTime = 0; // To accumulate total waiting time
        int idleTime = 0; // To accumulate total idle time

        // StringBuilder for Gantt chart representation
        StringBuilder ganttChartTop = new StringBuilder();
        StringBuilder ganttChartBottom = new StringBuilder();
        StringBuilder ganttChartTime = new StringBuilder("0");

        // Main scheduling loop
        while (!sortedProcesses.isEmpty()) {
            Process shortest = null;

            // Find the process with the shortest burst time among the available processes
            for (Process p : sortedProcesses) {
                if (p.arrivalTime <= currentTime) { // Process has arrived
                    if (shortest == null || p.burstTime < shortest.burstTime) {
                        shortest = p;  // Select the shortest burst time process
                    }
                }
            }

            // If a process is found to execute
            if (shortest != null) {
                sortedProcesses.remove(shortest); // Remove the selected process

                // Update the process execution times
                shortest.startTime = currentTime;
                shortest.completionTime = currentTime + shortest.burstTime;
                shortest.turnAroundTime = shortest.completionTime - shortest.arrivalTime;
                shortest.waitingTime = shortest.turnAroundTime - shortest.burstTime;

                // Accumulate times for the results
                totalTurnaroundTime += shortest.turnAroundTime;
                totalWaitingTime += shortest.waitingTime;

                // Update Gantt chart visuals
                ganttChartTop.append("+-----");
                ganttChartBottom.append("|  ").append(shortest.processID).append("  ");
                ganttChartTime.append(String.format("%5d", shortest.completionTime));

                // Move the current time forward
                currentTime = shortest.completionTime;

            } else {
                // If no process is ready, update idle time and wait for the next process to arrive
                ganttChartTop.append("+-----");
                ganttChartBottom.append("| /// ");
                ganttChartTime.append(String.format("%5d", sortedProcesses.get(0).arrivalTime));
                idleTime += sortedProcesses.get(0).arrivalTime - currentTime;
                currentTime = sortedProcesses.get(0).arrivalTime;
            }
        }

        // Append the final symbols for the Gantt chart
        ganttChartTop.append("+");
        ganttChartBottom.append("|");

        // Display results (process table and Gantt chart)
        displayResults(totalTurnaroundTime, totalWaitingTime, idleTime, currentTime, ganttChartTop, ganttChartBottom,
                ganttChartTime);
    }

    // Method to display the results including process details and Gantt chart
    private void displayResults(int totalTurnaroundTime, int totalWaitingTime, int idleTime, int currentTime,
            StringBuilder ganttChartTop, StringBuilder ganttChartBottom, StringBuilder ganttChartTime) {
        
        // Process table header
        System.out.println("\nProcess Table:");
        System.out.println("Process\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");
        
        // Display details for each process
        for (Process p : processes) {
            System.out.println(p.processID + "\t" + p.arrivalTime + "\t\t" + p.burstTime + "\t\t" + p.completionTime
                    + "\t\t" + p.turnAroundTime + "\t\t" + p.waitingTime);
        }

        // Print Gantt chart
        System.out.println("\nGantt Chart:");
        System.out.println(ganttChartTop);
        System.out.println(ganttChartBottom);
        System.out.println(ganttChartTop);
        System.out.println(ganttChartTime);

        // Calculate average times and CPU utilization
        double avgTurnaroundTime = (double) totalTurnaroundTime / processes.size();
        double avgWaitingTime = (double) totalWaitingTime / processes.size();
        double cpuUtilization = ((currentTime - idleTime) / (double) currentTime) * 100;

        // Display calculated averages and CPU utilization
        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f ms\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }
}
