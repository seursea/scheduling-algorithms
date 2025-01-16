import java.util.*;

class Process {
    String pid;
    int arrivalTime;
    int burstTime;
    int remainingTime; // Field to track remaining time
    int priority;      // Field to store priority
    int startTime = -1;
    int completionTime;
    int turnaroundTime;
    int waitingTime;

    Process(String pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime; // Initialize remaining time to burst time
        this.priority = priority;      // Initialize priority
    }
}

class PreemptivePriority {
    private List<Process> processes;

    public PreemptivePriority() {
        this.processes = new ArrayList<>();
    }

    public void addProcess(String processID, int arrivalTime, int burstTime, int priority) {
        processes.add(new Process(processID, arrivalTime, burstTime, priority));
    }

    public void addProcess(int arrivalTime, int burstTime, int priority) {
        String processID = "P" + (processes.size() + 1);
        addProcess(processID, arrivalTime, burstTime, priority);
    }

    public void execute() {
        List<Process> sortedProcesses = new ArrayList<>(processes);
        sortedProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int n = sortedProcesses.size();
        int currentTime = 0;
        int completed = 0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;
        int totalBurstTime = 0;
        int idleTime = 0;

        StringBuilder ganttChartTop = new StringBuilder("+");
        StringBuilder ganttChartBottom = new StringBuilder("|");
        StringBuilder ganttChartTime = new StringBuilder("0");

        for (Process p : processes) {
            totalBurstTime += p.burstTime;
        }

        Process currentProcess = null;

        while (completed < n) {
            Process highestPriorityProcess = null;

            // Find the process with the highest priority that is ready
            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.remainingTime > 0) {
                    if (highestPriorityProcess == null || p.priority < highestPriorityProcess.priority) {
                        highestPriorityProcess = p;
                    }
                }
            }

            if (highestPriorityProcess != null) {
                if (currentProcess != highestPriorityProcess) {
                    if (currentProcess != null) {
                        ganttChartTop.append("-----+");
                        ganttChartBottom.append(String.format("  %-3s|", currentProcess.pid));
                        ganttChartTime.append(String.format("%6d", currentTime));
                    }
                    currentProcess = highestPriorityProcess;
                }

                if (currentProcess.startTime == -1) {
                    currentProcess.startTime = currentTime;
                }

                currentProcess.remainingTime--;
                currentTime++;

                if (currentProcess.remainingTime == 0) {
                    currentProcess.completionTime = currentTime;
                    currentProcess.turnaroundTime = currentProcess.completionTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;

                    totalTurnaroundTime += currentProcess.turnaroundTime;
                    totalWaitingTime += currentProcess.waitingTime;

                    completed++;
                }
            } else {
                ganttChartTop.append("-----+");
                ganttChartBottom.append("IDLE |");
                ganttChartTime.append(String.format("%6d", currentTime + 1));
                idleTime++;
                currentTime++;
                currentProcess = null;
            }
        }

        if (currentProcess != null) {
            ganttChartTop.append("-----+");
            ganttChartBottom.append(String.format("  %-3s|", currentProcess.pid));
            ganttChartTime.append(String.format("%6d", currentTime));
        }

        displayResults(processes, totalTurnaroundTime, totalWaitingTime, idleTime, currentTime, ganttChartTop, ganttChartBottom, ganttChartTime);
    }

    private static void displayResults(
            List<Process> processes,
            int totalTurnaroundTime,
            int totalWaitingTime,
            int idleTime,
            int currentTime,
            StringBuilder ganttChartTop,
            StringBuilder ganttChartBottom,
            StringBuilder ganttChartTime) {

        double avgTurnaroundTime = (double) totalTurnaroundTime / processes.size();
        double avgWaitingTime = (double) totalWaitingTime / processes.size();
        double cpuUtilization = ((double) (currentTime - idleTime) / currentTime) * 100;

        System.out.println("\nProcess Table:");
        System.out.printf("%-12s %-10s %-14s %-10s %-16s %-18s %-14s\n",
                "Process ID", "Priority", "Arrival Time", "Burst Time",
                "Completion Time", "Turnaround Time", "Waiting Time");

        for (Process p : processes) {
            System.out.printf("%-12s %-10d %-14d %-10d %-16d %-18d %-14d\n",
                    p.pid, p.priority, p.arrivalTime, p.burstTime, p.completionTime, p.turnaroundTime, p.waitingTime);
        }

        System.out.println("\nGantt Chart:");
        System.out.println(ganttChartTop);
        System.out.println(ganttChartBottom);
        System.out.println(ganttChartTop);
        System.out.println(ganttChartTime);

        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f ms\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }
}
