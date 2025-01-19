import java.util.*;

public class RoundRobinClass {
    private List<Process> processes;
    private final int timeQuantum;

    public RoundRobinClass(int timeQuantum) {
        this.processes = new ArrayList<>();
        this.timeQuantum = timeQuantum;
    }

    public void addProcess(int arrivalTime, int burstTime) {
        String processID = String.valueOf((char)('A' + processes.size())); // Generate names like A, B, C
        addProcess(processID, arrivalTime, burstTime);
    }

    public void addProcess(String processID, int arrivalTime, int burstTime) {
        processes.add(new Process(processID, arrivalTime, burstTime));
    }

    public void execute() {
        if (processes.isEmpty()) {
            System.out.println("No processes to schedule!");
            return;
        }

        // Create copies of processes for scheduling
        List<Process> remainingProcesses = new ArrayList<>();
        for (Process p : processes) {
            remainingProcesses.add(new Process(p.processID, p.arrivalTime, p.burstTime));
        }
        
        // Sort by arrival time
        remainingProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));

        Queue<Process> readyQueue = new LinkedList<>();
        List<String> ganttChart = new ArrayList<>();
        List<Integer> timeMarkers = new ArrayList<>();
        
        int currentTime = 0;
        int[] remainingBurstTime = new int[processes.size()];
        boolean[] completed = new boolean[processes.size()];
        
        // Initialize remaining burst times
        for (int i = 0; i < processes.size(); i++) {
            remainingBurstTime[i] = processes.get(i).burstTime;
        }

        timeMarkers.add(currentTime);
        
        while (true) {
            boolean allCompleted = true;
            
            // Check for newly arrived processes
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).arrivalTime <= currentTime) {
                Process arrived = remainingProcesses.remove(0);
                readyQueue.offer(arrived);
            }
            
            // If ready queue is empty but processes haven't arrived yet
            if (readyQueue.isEmpty() && !remainingProcesses.isEmpty()) {
                ganttChart.add("//");
                currentTime = remainingProcesses.get(0).arrivalTime;
                timeMarkers.add(currentTime);
                continue;
            }
            
            // Process the ready queue
            if (!readyQueue.isEmpty()) {
                Process current = readyQueue.poll();
                int index = getProcessIndex(current.processID);
                
                // Set start time if not set
                if (current.startTime == 0 && !completed[index]) {
                    current.startTime = currentTime;
                }
                
                int executeTime = Math.min(timeQuantum, remainingBurstTime[index]);
                remainingBurstTime[index] -= executeTime;
                currentTime += executeTime;
                
                ganttChart.add(current.processID);
                timeMarkers.add(currentTime);
                
                // Check if process is completed
                if (remainingBurstTime[index] == 0 && !completed[index]) {
                    completed[index] = true;
                    processes.get(index).completionTime = currentTime;
                    processes.get(index).turnAroundTime = currentTime - processes.get(index).arrivalTime;
                    processes.get(index).waitingTime = processes.get(index).turnAroundTime - processes.get(index).burstTime;
                } else if (remainingBurstTime[index] > 0) {
                    // If process is not completed, add back to ready queue
                    readyQueue.offer(current);
                }
            }
            
            // Check if all processes are completed
            for (boolean isCompleted : completed) {
                if (!isCompleted) {
                    allCompleted = false;
                    break;
                }
            }
            
            if (allCompleted) break;
        }
        
        displayResults(ganttChart, timeMarkers);
    }
    
    private int getProcessIndex(String processID) {
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).processID.equals(processID)) {
                return i;
            }
        }
        return -1;
    }

    private void displayResults(List<String> ganttChart, List<Integer> timeMarkers) {
        System.out.println("\nProcess Table:");
        System.out.println("Process\tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");
        
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;
        int totalBurstTime = 0;
        
        for (Process p : processes) {
            System.out.println(p.processID + "\t" + p.arrivalTime + "\t\t" + 
                             p.burstTime + "\t\t" + p.completionTime + "\t\t" + 
                             p.turnAroundTime + "\t\t" + p.waitingTime);
            
            totalTurnaroundTime += p.turnAroundTime;
            totalWaitingTime += p.waitingTime;
            totalBurstTime += p.burstTime;
        }
        
        // Display Gantt chart
        System.out.println("\nGantt Chart:");
        
        // Top border
        System.out.print("+");
        for (int i = 0; i < ganttChart.size(); i++) {
            System.out.print("--------+");
        }
        System.out.println();
        
        // Process IDs
        System.out.print("|");
        for (String process : ganttChart) {
            System.out.printf(" %-6s |", process);
        }
        System.out.println();
        
        // Bottom border
        System.out.print("+");
        for (int i = 0; i < ganttChart.size(); i++) {
            System.out.print("--------+");
        }
        System.out.println();
        
        // Time markers
        for (Integer time : timeMarkers) {
            System.out.printf("%-9d", time);
        }
        System.out.println();

        // Calculate and display averages
        double avgTurnaroundTime = (double) totalTurnaroundTime / processes.size();
        double avgWaitingTime = (double) totalWaitingTime / processes.size();
        
        // Calculate CPU utilization (excluding idle time)
        int totalTime = timeMarkers.get(timeMarkers.size() - 1);
        double cpuUtilization = ((double) totalBurstTime / totalTime) * 100;

        System.out.printf("\nAverage Turnaround Time: %.2f ms\n", avgTurnaroundTime);
        System.out.printf("Average Waiting Time: %.2f ms\n", avgWaitingTime);
        System.out.printf("CPU Utilization: %.2f%%\n", cpuUtilization);
    }
}