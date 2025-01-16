import java.util.ArrayList;
import java.util.Comparator;

public class SJFClass {
    private ArrayList<Process> processes = new ArrayList<>();
    private ArrayList<Process> sortedProcesses = new ArrayList<>();

    public void addProcess(String processID, int arrivalTime, int burstTime) {
        Process process = new Process(processID, arrivalTime, burstTime);
        processes.add(process);
        sortedProcesses.add(process);
    }

    public void addProcess(int arrivalTime, int burstTime) {
        // Assign process names as A, B, C, etc.
        String processID = String.valueOf((char)('A' + processes.size()));  // Generate A, B, C, etc.
        addProcess(processID, arrivalTime, burstTime);
    }

    public void execute() {
        sortedProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;
        int idleTime = 0;

        StringBuilder ganttChartTop = new StringBuilder();
        StringBuilder ganttChartBottom = new StringBuilder();
        StringBuilder ganttChartTime = new StringBuilder("0");

        while (!sortedProcesses.isEmpty()) {
            Process shortest = null;
            for (Process p : sortedProcesses) {
                if (p.arrivalTime <= currentTime) {
                    if (shortest == null || p.burstTime < shortest.burstTime) {
                        shortest = p;
                    }
                }
            }

            if (shortest != null) {
                sortedProcesses.remove(shortest);

                shortest.startTime = currentTime;
                shortest.completionTime = currentTime + shortest.burstTime;
                shortest.turnAroundTime = shortest.completionTime - shortest.arrivalTime;
                shortest.waitingTime = shortest.turnAroundTime - shortest.burstTime;

                totalTurnaroundTime += shortest.turnAroundTime;
                totalWaitingTime += shortest.waitingTime;

                ganttChartTop.append("+-----");
                ganttChartBottom.append("|  " + shortest.processID + "  ");
                ganttChartTime.append(String.format("%6d", shortest.completionTime));

                currentTime = shortest.completionTime;

                System.out.println("Process ID: " + shortest.processID);
                System.out.println("Arrival Time: " + shortest.arrivalTime);
                System.out.println("Burst Time: " + shortest.burstTime);
                System.out.println("Start Time: " + shortest.startTime);
                System.out.println("Completion Time: " + shortest.completionTime);
                System.out.println("Turnaround Time: " + shortest.turnAroundTime);
                System.out.println("Waiting Time: " + shortest.waitingTime);
                System.out.println("-------------------------");

            } else {
                ganttChartTop.append("+-----");
                ganttChartBottom.append("| /// ");
                ganttChartTime.append(String.format("%6d", sortedProcesses.get(0).arrivalTime));
                idleTime += sortedProcesses.get(0).arrivalTime - currentTime;
                currentTime = sortedProcesses.get(0).arrivalTime;
            }
        }

        ganttChartTop.append("+");
        ganttChartBottom.append("|");

        displayResults(totalTurnaroundTime, totalWaitingTime, idleTime, currentTime, ganttChartTop, ganttChartBottom, ganttChartTime);
    }

    private void displayResults(int totalTurnaroundTime, int totalWaitingTime, int idleTime, int currentTime, StringBuilder ganttChartTop, StringBuilder ganttChartBottom, StringBuilder ganttChartTime) {
        System.out.println("\nProcess Table:");
        System.out.println("Process\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");
        for (Process p : processes) {
            System.out.println(p.processID + "\t" + p.arrivalTime + "\t\t" + p.burstTime + "\t\t" + p.completionTime + "\t\t" + p.turnAroundTime + "\t\t" + p.waitingTime);
        }

        System.out.println("\nGantt Chart:");
        System.out.println(ganttChartTop.toString());
        System.out.println(ganttChartBottom.toString());
        System.out.println(ganttChartTop.toString());
        System.out.println(ganttChartTime.toString());

        double avgTurnaroundTime = (double) totalTurnaroundTime / processes.size();
        double avgWaitingTime = (double) totalWaitingTime / processes.size();
        double cpuUtilization = ((currentTime - idleTime) / (double) currentTime) * 100;

        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f ms\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }
}