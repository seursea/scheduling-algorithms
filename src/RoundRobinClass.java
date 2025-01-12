import java.util.*;

class Process {
    private final int id;
    private final int arrivalTime;
    private final int burstTime;
    private int remainingTime;
    private int completionTime;
    private int turnaroundTime;
    private int waitingTime;

    public Process(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }

    // getters
    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
        calculateTimes();
    }

    private void calculateTimes() {
        this.turnaroundTime = this.completionTime - this.arrivalTime;
        this.waitingTime = this.turnaroundTime - this.burstTime;
    }

    public boolean isCompleted() {
        return remainingTime <= 0;
    }
}

public class RoundRobinClass {
    private final List<Process> processes;
    private final int timeQuantum;
    private final List<GanttChartEntry> ganttChart;

    private static class GanttChartEntry {
        String processId;
        int startTime;
        int endTime;

        GanttChartEntry(String processId, int startTime, int endTime) {
            this.processId = processId;
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    public RoundRobinClass(int timeQuantum) {
        this.processes = new ArrayList<>();
        this.timeQuantum = timeQuantum;
        this.ganttChart = new ArrayList<>();
    }

    public void addProcess(int arrivalTime, int burstTime) {
        processes.add(new Process(processes.size() + 1, arrivalTime, burstTime));
    }

    public void schedule() {
        if (processes.isEmpty()) {
            System.out.println("No processes to schedule!");
            return;
        }

        Queue<Process> readyQueue = new LinkedList<>();
        List<Process> notArrived = new ArrayList<>(processes);
        // sort by arrival time
        notArrived.sort(Comparator.comparingInt(Process::getArrivalTime));

        int currentTime = 0;
        int completedProcesses = 0;
        Process currentProcess = null;
        int remainingQuantum = 0;

        while (completedProcesses < processes.size()) {
            // check for newly arrived processes
            while (!notArrived.isEmpty() && notArrived.get(0).getArrivalTime() <= currentTime) {
                readyQueue.offer(notArrived.remove(0));
            }

            // if no current process, get next from ready queue
            if (currentProcess == null) {
                // if ready queue is empty but processes haven't arrived yet
                if (readyQueue.isEmpty()) {
                    if (!notArrived.isEmpty()) {
                        // add idle time in gantt chart
                        ganttChart.add(new GanttChartEntry("IDLE",
                                currentTime,
                                notArrived.get(0).getArrivalTime()));
                        currentTime = notArrived.get(0).getArrivalTime();
                        continue;
                    }
                } else {
                    currentProcess = readyQueue.poll();
                    remainingQuantum = timeQuantum;
                }
            }

            if (currentProcess != null) {
                int executeTime = Math.min(remainingQuantum, currentProcess.getRemainingTime());

                // add to gantt chart
                ganttChart.add(new GanttChartEntry(
                        "P" + currentProcess.getId(),
                        currentTime,
                        currentTime + executeTime));

                currentTime += executeTime;
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - executeTime);
                remainingQuantum -= executeTime;

                // check if process completed or quantum expired
                if (currentProcess.isCompleted()) {
                    currentProcess.setCompletionTime(currentTime);
                    completedProcesses++;
                    currentProcess = null;
                } else if (remainingQuantum == 0) {
                    // add back to ready queue if quantum expired
                    while (!notArrived.isEmpty() &&
                            notArrived.get(0).getArrivalTime() <= currentTime) {
                        readyQueue.offer(notArrived.remove(0));
                    }
                    readyQueue.offer(currentProcess);
                    currentProcess = null;
                }
            }
        }

        displayResults();
    }

    // display results
    private void displayResults() {
        System.out.println("\nProcess Details:");
        System.out.println("Process\tAT\tBT\tCT\tTAT\tWT");
        System.out.println("----------------------------------------");

        double totalTurnaround = 0;
        double totalWaiting = 0;

        List<Process> sortedProcesses = new ArrayList<>(processes);
        sortedProcesses.sort(Comparator.comparingInt(Process::getId));

        for (Process p : sortedProcesses) {
            System.out.printf("P%d\t%d\t%d\t%d\t%d\t%d\n",
                    p.getId(), p.getArrivalTime(), p.getBurstTime(),
                    p.getCompletionTime(), p.getTurnaroundTime(),
                    p.getWaitingTime());

            totalTurnaround += p.getTurnaroundTime();
            totalWaiting += p.getWaitingTime();
        }

        // display averages
        int n = processes.size();
        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", totalTurnaround / n);
        System.out.printf("Average Waiting Time: %.2f ms\n", totalWaiting / n);

        // display gantt chart
        System.out.println("\nGantt Chart:");
        System.out.print("|");
        for (GanttChartEntry entry : ganttChart) {
            System.out.printf(" %-4s |", entry.processId);
        }
        System.out.println("\n");

        // print timeline
        for (GanttChartEntry entry : ganttChart) {
            System.out.printf("%-6d", entry.startTime);
        }
        System.out.printf("%-6d\n", ganttChart.get(ganttChart.size() - 1).endTime);

        // calculate cpu utilization
        int totalTime = ganttChart.get(ganttChart.size() - 1).endTime;
        int idleTime = ganttChart.stream()
                .filter(entry -> entry.processId.equals("IDLE"))
                .mapToInt(entry -> entry.endTime - entry.startTime)
                .sum();

        double cpuUtilization = ((double) (totalTime - idleTime) / totalTime) * 100;
        System.out.printf("\nCPU Utilization: %.2f%%\n", cpuUtilization);
    }
}