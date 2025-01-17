import java.util.ArrayList;
import java.util.Comparator;

class PriorityProcess {
    String processID;
    int arrivalTime;
    int burstTime;
    int priority;
    int startTime = -1;
    int completionTime;
    int turnAroundTime;
    int waitingTime;

    public PriorityProcess(String processID, int arrivalTime, int burstTime, int priority) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }
}
public class NonPreemptivePriority {
    private ArrayList<PriorityProcess> processes = new ArrayList<>();
    private ArrayList<PriorityProcess> completedProcesses = new ArrayList<>();

    public void addProcess(String processID, int arrivalTime, int burstTime, int priority) {
        PriorityProcess process = new PriorityProcess(processID, arrivalTime, burstTime, priority);
        processes.add(process);
    }

    public void addProcess(int arrivalTime, int burstTime, int priority) {
        String processID = String.valueOf((char) ('A' + processes.size()));
        addProcess(processID, arrivalTime, burstTime, priority);
    }

    public void execute() {
        // Sort processes by arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;
        int idleTime = 0;

        StringBuilder ganttChartTop = new StringBuilder();
        StringBuilder ganttChartBottom = new StringBuilder();
        StringBuilder ganttChartTime = new StringBuilder("0");

        while (!processes.isEmpty()) {
            // Select the highest priority process from arrived processes
            PriorityProcess currentProcess = processes.stream()
                .filter(p -> p.arrivalTime <= currentTime)
                .min(Comparator.comparingInt((PriorityProcess p) -> p.priority)
                    .thenComparingInt(p -> p.processID.charAt(0)))
                .orElse(null);

            if (currentProcess != null) {
                processes.remove(currentProcess);

                // Start executing the process
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
                ganttChartTime.append(String.format("%5d", currentTime));
            } else {
                // Idle time if no process is ready
                ganttChartTop.append("+-----");
                ganttChartBottom.append("| /// ");
                ganttChartTime.append(String.format("%5d", currentTime + 1));
                idleTime++;
                currentTime++;
            }
        }

        ganttChartTop.append("+");
        ganttChartBottom.append("|");

        // Display results
        displayResults(totalTurnaroundTime, totalWaitingTime, idleTime, currentTime, ganttChartTop, ganttChartBottom, ganttChartTime);
    }

    private void displayResults(int totalTurnaroundTime, int totalWaitingTime, int idleTime, int currentTime, StringBuilder ganttChartTop, StringBuilder ganttChartBottom, StringBuilder ganttChartTime) {
        System.out.println("\nProcess Table:");
        System.out.println("Process\tArrival Time\tBurst Time\tPriority\tCompletion Time\tTurnaround Time\tWaiting Time");

        for (PriorityProcess p : completedProcesses) {
            System.out.println(p.processID + "\t" + p.arrivalTime + "\t\t" + p.burstTime + "\t\t" + p.priority + "\t\t" + p.completionTime + "\t\t" + p.turnAroundTime + "\t\t" + p.waitingTime);
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

