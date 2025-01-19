import java.util.ArrayList;
import java.util.Comparator;

public class SJFClass {

    // List to store all processes and their sorted versions
    private ArrayList<Process> processes = new ArrayList<>();
    private ArrayList<Process> sortedProcesses = new ArrayList<>();

    // Method to add a new process by providing its process ID, arrival time, and burst time
    public void addProcess(String processID, int arrivalTime, int burstTime) {
        Process process = new Process(processID, arrivalTime, burstTime);
        processes.add(process);    // Add the process to the main list
        sortedProcesses.add(process); // Add it to the sorted list for sorting
    }

    // Overloaded method to add a process by just providing the arrival time and burst time
    public void addProcess(int arrivalTime, int burstTime) {
        // Assign process names dynamically as A, B, C, etc.
        String processID = String.valueOf((char)('A' + processes.size())); // Generate names like A, B, C
        addProcess(processID, arrivalTime, burstTime); // Add process with generated ID
    }

    // Method to execute the Shortest Job First (SJF) algorithm
    public void execute() {
        // Sort processes based on their arrival time
        sortedProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0; // Keeps track of the current time in the scheduling process
        int totalTurnaroundTime = 0; // To accumulate total turnaround time
        int totalWaitingTime = 0; // To accumulate total waiting time
        int idleTime = 0; // To accumulate idle time when the CPU is waiting for the next process

        // StringBuilder objects for creating the Gantt chart visuals
        StringBuilder ganttChartTop = new StringBuilder();
        StringBuilder ganttChartBottom = new StringBuilder();
        StringBuilder ganttChartTime = new StringBuilder("0");

        // Main scheduling loop for the SJF algorithm
        while (!sortedProcesses.isEmpty()) {
            Process shortest = null;

            // Find the process with the shortest burst time among the available processes
            for (Process p : sortedProcesses) {
                if (p.arrivalTime <= currentTime) { // Process has arrived
                    if (shortest == null || p.burstTime < shortest.burstTime) {
                        shortest = p; // Select the process with the shortest burst time
                    }
                }
            }

            // If a process is found to execute
            if (shortest != null) {
                sortedProcesses.remove(shortest); // Remove the selected process from the list

                // Update the process execution times
                shortest.startTime = currentTime;
                shortest.completionTime = currentTime + shortest.burstTime;
                shortest.turnAroundTime = shortest.completionTime - shortest.arrivalTime;
                shortest.waitingTime = shortest.turnAroundTime - shortest.burstTime;

                // Accumulate times for final results
                totalTurnaroundTime += shortest.turnAroundTime;
                totalWaitingTime += shortest.waitingTime;

                // Update Gantt chart visuals
                ganttChartTop.append("+-----");
                ganttChartBottom.append("|  " + shortest.processID + "  ");
                ganttChartTime.append(String.format("%6d", shortest.completionTime));

                // Move the current time forward
                currentTime = shortest.completionTime;

                // Print the process details for debugging or display
                System.out.println("Process ID: " + shortest.processID);
                System.out.println("Arrival Time: " + shortest.arrivalTime);
                System.out.println("Burst Time: " + shortest.burstTime);
                System.out.println("Start Time: " + shortest.startTime);
                System.out.println("Completion Time: " + shortest.completionTime);
                System.out.println("Turnaround Time: " + shortest.turnAroundTime);
                System.out.println("Waiting Time: " + shortest.waitingTime);
                System.out.println("-------------------------");

            } else {
                // If no process is ready, update idle time and wait for the next process to arrive
                ganttChartTop.append("+-----");
                ganttChartBottom.append("| /// ");
                ganttChartTime.append(String.format("%6d", sortedProcesses.get(0).arrivalTime));
                idleTime += sortedProcesses.get(0).arrivalTime - currentTime;
                currentTime = sortedProcesses.get(0).arrivalTime;
            }
        }

        // Append final symbols for the Gantt chart
        ganttChartTop.append("+");
        ganttChartBottom.append("|");

        // Display final results, including process details and Gantt chart
        displayResults(totalTurnaroundTime, totalWaitingTime, idleTime, currentTime, ganttChartTop, ganttChartBottom, ganttChartTime);
    }

    // Method to display the results, including the process table and Gantt chart
    private void displayResults(int totalTurnaroundTime, int totalWaitingTime, int idleTime, int currentTime, StringBuilder ganttChartTop, StringBuilder ganttChartBottom, StringBuilder ganttChartTime) {
        // Display process table header
        System.out.println("\nProcess Table:");
        System.out.println("Process\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");

        // Display details for each process
        for (Process p : processes) {
            System.out.println(p.processID + "\t" + p.arrivalTime + "\t\t" + p.burstTime + "\t\t" + p.completionTime + "\t\t" + p.turnAroundTime + "\t\t" + p.waitingTime);
        }

        // Print Gantt chart visualization
        System.out.println("\nGantt Chart:");
        System.out.println(ganttChartTop.toString());
        System.out.println(ganttChartBottom.toString());
        System.out.println(ganttChartTop.toString());
        System.out.println(ganttChartTime.toString());

        // Calculate and display average times and CPU utilization
        double avgTurnaroundTime = (double) totalTurnaroundTime / processes.size();
        double avgWaitingTime = (double) totalWaitingTime / processes.size();
        double cpuUtilization = ((currentTime - idleTime) / (double) currentTime) * 100;
        
        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f ms\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }
}