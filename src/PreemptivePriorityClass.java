import java.util.ArrayList;
import java.util.Comparator;

public class PreemptivePriorityClass {
    private ArrayList<PriorityProcess> processes = new ArrayList<>();
    private ArrayList<PriorityProcess> readyQueue = new ArrayList<>();
    private ArrayList<PriorityProcess> completedProcesses = new ArrayList<>();

    public void addProcess(String processID, int priority, int arrivalTime, int burstTime) {
        PriorityProcess process = new PriorityProcess(processID, priority, arrivalTime, burstTime);
        processes.add(process);
    }

    public void addProcess(int priority, int arrivalTime, int burstTime) {
        String processID = String.valueOf((char) ('A' + processes.size()));
        addProcess(processID, priority, arrivalTime, burstTime);
    }

    public void execute() {
        // Make a copy of the processes in original order for later table display
        ArrayList<PriorityProcess> originalProcesses = new ArrayList<>(processes);
    
        // Sort processes for scheduling based on arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
    
        int currentTime = 0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;
        int idleTime = 0;
    
        StringBuilder ganttChartTop = new StringBuilder();
        StringBuilder ganttChartBottom = new StringBuilder();
        StringBuilder ganttChartTime = new StringBuilder();
    
        String lastProcessID = null; // Tracks the last process added to the Gantt chart
    
        // Initialize Gantt chart time with the start time (0)
        ganttChartTime.append(String.format("%5d ", currentTime));
    
        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            // Move all processes that have arrived by current time to the ready queue
            while (!processes.isEmpty() && processes.get(0).arrivalTime <= currentTime) {
                readyQueue.add(processes.remove(0));
            }
    
            // Sort the ready queue by priority, then process ID
            readyQueue.sort(Comparator.comparingInt((PriorityProcess p) -> p.priority)
                    .thenComparingInt(p -> p.processID.charAt(0)));
    
            if (!readyQueue.isEmpty()) {
                PriorityProcess currentProcess = readyQueue.remove(0);
    
                // Set start time if it's the first execution for this process
                if (currentProcess.startTime == -1) {
                    currentProcess.startTime = currentTime;
                }
    
                // Calculate execution time based on next arrival or remaining time
                int nextArrivalTime = processes.isEmpty() ? Integer.MAX_VALUE : processes.get(0).arrivalTime;
                int executionTime = Math.min(currentProcess.remainingTime, nextArrivalTime - currentTime);
    
                // Execute the process
                currentProcess.remainingTime -= executionTime;
                currentTime += executionTime;
    
                // Add to Gantt chart if the process ID has changed
                if (!currentProcess.processID.equals(lastProcessID)) {
                    ganttChartTop.append("+-----");
                    ganttChartBottom.append("|  ").append(currentProcess.processID).append("  ");
    
                    // Append the current time to the Gantt chart only once
                    if (!ganttChartTime.toString().endsWith(String.format("%5d ", currentTime - executionTime))) {
                        ganttChartTime.append(String.format("%5d ", currentTime - executionTime));
                    }
    
                    lastProcessID = currentProcess.processID;
                }
    
                // If the process is completed
                if (currentProcess.remainingTime == 0) {
                    currentProcess.completionTime = currentTime;
                    currentProcess.turnAroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnAroundTime - currentProcess.burstTime;
    
                    totalTurnaroundTime += currentProcess.turnAroundTime;
                    totalWaitingTime += currentProcess.waitingTime;
    
                    completedProcesses.add(currentProcess);
                } else {
                    // Re-add the process to the ready queue if not finished
                    readyQueue.add(currentProcess);
                }
            } else {
                // Idle time if no process is ready
                ganttChartTop.append("+-----");
                ganttChartBottom.append("| /// ");
    
                // Append idle time start only once
                if (!ganttChartTime.toString().endsWith(String.format("%5d ", currentTime))) {
                    ganttChartTime.append(String.format("%5d ", currentTime));
                }
    
                idleTime += processes.get(0).arrivalTime - currentTime;
                currentTime = processes.get(0).arrivalTime;
                lastProcessID = null;
            }
        }
    
        // Append the final completion time once at the end of the Gantt chart
        if (!ganttChartTime.toString().endsWith(String.format("%5d ", currentTime))) {
            ganttChartTime.append(String.format("%5d ", currentTime));
        }
        ganttChartTop.append("+");
        ganttChartBottom.append("|");
    
        // Ensure the display uses the original process order for the table
        displayResults(totalTurnaroundTime, totalWaitingTime, idleTime, currentTime, ganttChartTop, ganttChartBottom, ganttChartTime, originalProcesses);
    }
    
    private void displayResults(int totalTurnaroundTime, int totalWaitingTime, int idleTime, int currentTime, StringBuilder ganttChartTop, StringBuilder ganttChartBottom, StringBuilder ganttChartTime, ArrayList<PriorityProcess> originalProcesses) {
        System.out.println("\nProcess Table:");
        System.out.println("Process\tPriority\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");
    
        // Display in the order of the original process IDs, not sorted by arrival time
        for (PriorityProcess p : originalProcesses) {
            // Check if the process is completed and add its details to the table
            if (completedProcesses.contains(p)) {
                // Find the completed process in the completedProcesses list
                for (PriorityProcess completedProcess : completedProcesses) {
                    if (completedProcess.processID.equals(p.processID)) {
                        System.out.println(p.processID + "\t" + p.priority + "\t\t" + p.arrivalTime + "\t\t" + p.burstTime + "\t\t" + completedProcess.completionTime + "\t\t" + completedProcess.turnAroundTime + "\t\t" + completedProcess.waitingTime);
                        break;
                    }
                }
            }
        }
    
        System.out.println("\nGantt Chart:");
        System.out.println(ganttChartTop.toString());
        System.out.println(ganttChartBottom.toString());
        System.out.println(ganttChartTop.toString());
        System.out.println(ganttChartTime.toString().trim());  // Remove leading/trailing spaces in time output
    
        double avgTurnaroundTime = (double) totalTurnaroundTime / completedProcesses.size();
        double avgWaitingTime = (double) totalWaitingTime / completedProcesses.size();
        double cpuUtilization = ((currentTime - idleTime) / (double) currentTime) * 100;
    
        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f ms\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }
}    