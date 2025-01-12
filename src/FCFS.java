import java.util.*;

class Process {
    String processID;
    int arrivalTime;
    int burstTime;
    int completionTime;
    int turnAroundTime;
    int waitingTime;

    public Process(String processID, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }
}

class FCFS {
    private List<Process> processes;

    public FCFS() {
        this.processes = new ArrayList<>();
    }

    public void addProcess(String processID, int arrivalTime, int burstTime) {
        processes.add(new Process(processID, arrivalTime, burstTime));
    }

    public void addProcess(int arrivalTime, int burstTime) {
        String processID = "P" + (processes.size() + 1);
        addProcess(processID, arrivalTime, burstTime);
    }

    public void execute() {
        List<Process> sortedProcesses = new ArrayList<>(processes);

        sortedProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0, totalWaitTime = 0, totalTurnAroundTime = 0, totalBurstTime = 0;
        for (Process p : sortedProcesses) {
            if (currentTime < p.arrivalTime) {
                currentTime = p.arrivalTime;
            }
            totalBurstTime += p.burstTime;
            p.completionTime = currentTime + p.burstTime;
            p.turnAroundTime = p.completionTime - p.arrivalTime;
            p.waitingTime = p.turnAroundTime - p.burstTime;

            currentTime = p.completionTime;
            totalWaitTime += p.waitingTime;
            totalTurnAroundTime += p.turnAroundTime;
        }

        displayResults(totalWaitTime, totalTurnAroundTime, totalBurstTime, currentTime, sortedProcesses);
    }

    private void displayResults(int totalWaitTime, int totalTurnAroundTime, int totalBurstTime, int currentTime,
            List<Process> sortedProcesses) {
        double avgWaitTime = (double) totalWaitTime / processes.size();
        double avgTurnaroundTime = (double) totalTurnAroundTime / processes.size();
        double cpuUtilization = ((double) totalBurstTime / currentTime) * 100;

        System.out.println("\nProcess Table:");
        System.out.printf("%-12s %-14s %-10s %-16s %-18s %-14s\n",
                "Process ID", "Arrival Time", "Burst Time",
                "Completion Time", "Turnaround Time", "Waiting Time");
        for (Process p : processes) {
            System.out.printf("%-12s %-14d %-10d %-16d %-18d %-14d\n",
                    p.processID, p.arrivalTime, p.burstTime,
                    p.completionTime, p.turnAroundTime, p.waitingTime);
        }

        System.out.printf("\nAverage Waiting Time: %.2f ms\n", avgWaitTime);
        System.out.printf("Average Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);

        System.out.println("\nGantt Chart:");
        StringBuilder ganttChart = new StringBuilder();
        StringBuilder timeMarkers = new StringBuilder("0");

        int prevCompletionTime = 0;
        for (Process p : sortedProcesses) {
            if (prevCompletionTime < p.arrivalTime) {
                ganttChart.append(String.format("| %-2s ", "//"));
                timeMarkers.append(String.format("%5d", p.arrivalTime));
                prevCompletionTime = p.arrivalTime;
            }
            ganttChart.append(String.format("| %-2s ", p.processID));
            timeMarkers.append(String.format("%5d", p.completionTime));
            prevCompletionTime = p.completionTime;
        }
        ganttChart.append("|");

        System.out.println(ganttChart);
        System.out.println(timeMarkers);
    }
}
