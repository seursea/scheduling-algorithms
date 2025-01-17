import java.util.ArrayList;
import java.util.Comparator;

public class NonPreemptivePriorityClass {
    private ArrayList<PriorityProcess> processes = new ArrayList<>();
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
        ArrayList<PriorityProcess> inputOrderProcesses = new ArrayList<>(processes); // To retain input order for printing

        int currentTime = 0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;
        int idleTime = 0;

        StringBuilder ganttChartTop = new StringBuilder();
        StringBuilder ganttChartBottom = new StringBuilder();
        StringBuilder ganttChartTime = new StringBuilder("0");

        while (!processes.isEmpty()) {
            // Select the highest priority process available at current time
            final int currentTimeFinal = currentTime;
            PriorityProcess currentProcess = processes.stream()
                .filter(p -> p.arrivalTime <= currentTimeFinal)
                .min(Comparator.comparingInt((PriorityProcess p) -> p.priority)
                    .thenComparingInt(p -> p.arrivalTime)) // Tie-breaking by arrival time
                .orElse(null);

            if (currentProcess != null) {
                processes.remove(currentProcess);

                // Start executing the process
                if (currentTime < currentProcess.arrivalTime) {
                    idleTime += currentProcess.arrivalTime - currentTime;
                    currentTime = currentProcess.arrivalTime;
                }

                currentProcess.startTime = currentTime;
                currentTime += currentProcess.burstTime;
                currentProcess.completionTime = currentTime;

                // Calculate turnaround time and waiting time
                currentProcess.turnAroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnAroundTime - currentProcess.burstTime;

                totalTurnaroundTime += currentProcess.turnAroundTime;
                totalWaitingTime += currentProcess.waitingTime;

                completedProcesses.add(currentProcess);

                // Update Gantt chart
                ganttChartTop.append("+-----");
                ganttChartBottom.append("|  ").append(currentProcess.processID).append("  ");
                ganttChartTime.append(String.format("%6d", currentTime));
            } else {
                // Idle time if no process is ready
                ganttChartTop.append("+-----");
                ganttChartBottom.append("| /// ");
                ganttChartTime.append(String.format("%6d", currentTime + 1));
                idleTime++;
                currentTime++;
            }
        }

        ganttChartTop.append("+");
        ganttChartBottom.append("|");

        // Display results
        displayResults(inputOrderProcesses, totalTurnaroundTime, totalWaitingTime, idleTime, currentTime, ganttChartTop, ganttChartBottom, ganttChartTime);
    }

    private void displayResults(ArrayList<PriorityProcess> inputOrderProcesses, int totalTurnaroundTime, int totalWaitingTime, int idleTime, int currentTime, StringBuilder ganttChartTop, StringBuilder ganttChartBottom, StringBuilder ganttChartTime) {
        System.out.println("\nProcess Table:");
        System.out.println("Process\tPriority\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");

        for (PriorityProcess p : inputOrderProcesses) {
            PriorityProcess completedProcess = completedProcesses.stream()
                .filter(cp -> cp.processID.equals(p.processID))
                .findFirst()
                .orElse(null);
            if (completedProcess != null) {
                System.out.println(completedProcess.processID + "\t" + completedProcess.priority + "\t\t" + completedProcess.arrivalTime + "\t\t" + completedProcess.burstTime + "\t\t" + completedProcess.completionTime + "\t\t" + completedProcess.turnAroundTime + "\t\t" + completedProcess.waitingTime);
            }
        }

        System.out.println("\nGantt Chart:");
        System.out.println(ganttChartTop);
        System.out.println(ganttChartBottom);
        System.out.println(ganttChartTop);
        System.out.println(ganttChartTime);

        double avgTurnaroundTime = (double) totalTurnaroundTime / completedProcesses.size();
        double avgWaitingTime = (double) totalWaitingTime / completedProcesses.size();
        double cpuUtilization = ((currentTime - idleTime) / (double) currentTime) * 100;

        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f ms\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }

}