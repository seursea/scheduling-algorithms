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

class SRTF {
    private List<Process> processes;

    public SRTF() {
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

        int n = sortedProcesses.size();
        int[] remainingTime = new int[n];
        for (int i = 0; i < n; i++) {
            remainingTime[i] = sortedProcesses.get(i).burstTime;
        }

        int complete = 0, currentTime = 0, minTime = Integer.MAX_VALUE;
        int shortest = 0, finishTime;
        boolean check = false;

        List<String> ganttChart = new ArrayList<>();
        List<Integer> timeMarkers = new ArrayList<>();
        boolean timeMarkerAdded = false; // To ensure 0 is added only once

        while (complete != n) {
            for (int j = 0; j < n; j++) {
                if ((sortedProcesses.get(j).arrivalTime <= currentTime) &&
                        (remainingTime[j] < minTime) && remainingTime[j] > 0) {
                    minTime = remainingTime[j];
                    shortest = j;
                    check = true;
                }
            }

            if (!check) {
                if (ganttChart.isEmpty() || !ganttChart.get(ganttChart.size() - 1).equals("Idle")) {
                    ganttChart.add("//");
                    if (!timeMarkerAdded) {
                        // timeMarkers.add(0); // Add 0 only once
                        timeMarkerAdded = true;
                    }
                    timeMarkers.add(currentTime);
                }
                currentTime++;
                continue;
            }

            remainingTime[shortest]--;
            minTime = remainingTime[shortest];
            if (minTime == 0) {
                minTime = Integer.MAX_VALUE;
            }

            if (remainingTime[shortest] == 0) {
                complete++;
                check = false;

                finishTime = currentTime + 1;
                sortedProcesses.get(shortest).completionTime = finishTime;
                sortedProcesses.get(shortest).turnAroundTime = finishTime - sortedProcesses.get(shortest).arrivalTime;
                sortedProcesses.get(shortest).waitingTime = sortedProcesses.get(shortest).turnAroundTime
                        - sortedProcesses.get(shortest).burstTime;
            }

            if (ganttChart.isEmpty()
                    || !ganttChart.get(ganttChart.size() - 1).equals(sortedProcesses.get(shortest).processID)) {
                ganttChart.add(sortedProcesses.get(shortest).processID);
                if (!timeMarkerAdded) {
                    // timeMarkers.add(0); // Add 0 only once
                    timeMarkerAdded = true;
                }
                timeMarkers.add(currentTime);
            }

            currentTime++;
        }
        timeMarkers.add(currentTime);

        int totalWaitTime = 0, totalTurnAroundTime = 0, totalBurstTime = 0;
        for (Process p : sortedProcesses) {
            totalWaitTime += p.waitingTime;
            totalTurnAroundTime += p.turnAroundTime;
            totalBurstTime += p.burstTime;
        }

        displayResults(totalWaitTime, totalTurnAroundTime, totalBurstTime, currentTime, sortedProcesses, ganttChart,
                timeMarkers);
    }

    private void displayResults(int totalWaitTime, int totalTurnAroundTime, int totalBurstTime, int currentTime,
            List<Process> sortedProcesses, List<String> ganttChart, List<Integer> timeMarkers) {
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
        StringBuilder ganttChartStr = new StringBuilder();
        StringBuilder timeMarkersStr = new StringBuilder();

        timeMarkersStr.append(String.format("%-5d", timeMarkers.get(0)));
        for (int i = 0; i < ganttChart.size(); i++) {
            ganttChartStr.append(String.format("| %-2s ", ganttChart.get(i)));
            timeMarkersStr.append(String.format("%-5d", timeMarkers.get(i + 1)));
        }
        ganttChartStr.append("|");

        System.out.println(ganttChartStr);
        System.out.println(timeMarkersStr);
    }
}
