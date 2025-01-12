import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

class Process {
    String pid;
    int arrivalTime;
    int burstTime;
    int startTime;
    int completionTime;
    int turnaroundTime;
    int waitingTime;

    Process(String pid, int arrivalTime, int burstTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }
}

public class SJFClass {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of processes: ");
        int n = scanner.nextInt();
        ArrayList<Process> processes = new ArrayList<>();
        ArrayList<Process> sortedProcesses = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            String pid = String.valueOf((char) ('A' + i));
            System.out.print("Enter arrival time of process " + pid + ": ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Enter burst time of process " + pid + ": ");
            int burstTime = scanner.nextInt();
            Process process = new Process(pid, arrivalTime, burstTime);
            processes.add(process);
            sortedProcesses.add(process);
        }

        // Sort processes by arrival time
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
                shortest.turnaroundTime = shortest.completionTime - shortest.arrivalTime;
                shortest.waitingTime = shortest.turnaroundTime - shortest.burstTime;

                totalTurnaroundTime += shortest.turnaroundTime;
                totalWaitingTime += shortest.waitingTime;

                ganttChartTop.append("+-----");
                ganttChartBottom.append("|  " + shortest.pid + "  ");
                ganttChartTime.append(String.format("%6d", shortest.completionTime));

                currentTime = shortest.completionTime;
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

        System.out.println("\nProcess Table:");
        System.out.println("PID\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");
        for (Process p : processes) {
            System.out.println(p.pid + "\t" + p.arrivalTime + "\t\t" + p.burstTime + "\t\t" + p.completionTime + "\t\t" + p.turnaroundTime + "\t\t" + p.waitingTime);
        }

        System.out.println("\nGantt Chart:");
        System.out.println(ganttChartTop.toString());
        System.out.println(ganttChartBottom.toString());
        System.out.println(ganttChartTop.toString());
        System.out.println(ganttChartTime.toString());

        double avgTurnaroundTime = (double) totalTurnaroundTime / n;
        double avgWaitingTime = (double) totalWaitingTime / n;
        double cpuUtilization = ((currentTime - idleTime) / (double) currentTime) * 100;

        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f ms\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);

        scanner.close();
}
