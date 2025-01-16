import java.util.*;

public class Scheduling {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean continueProgram = true;

        while (continueProgram) {
            System.out.println("\n========= CPU Scheduling Algorithms =========");
            System.out.println("1. First Come First Serve (FCFS)");
            System.out.println("2. Shortest Job First (SJF)");
            System.out.println("3. Shortest Remaining Time First (SRTF)");
            System.out.println("4. Priority Scheduling (Non-Preemptive)");
            System.out.println("5. Priority Scheduling (Preemptive)");
            System.out.println("6. Round Robin");
            System.out.println("0. Exit Program");
            System.out.println("==========================================");

            System.out.print("\nEnter your choice (0-6): ");
            int choice = getValidChoice(0, 6);

            if (choice == 0) {
                System.out.println("\nThank you for using the CPU Scheduling Program!");
                continueProgram = false;
                continue;
            }

            // Get number of processes
            System.out.print("\nEnter number of processes (1-10): ");
            int n = getValidChoice(1, 10);

            // Arrays to store process details
            String[] processID = new String[n];  // To store auto-incremented process IDs
            int[] arrivalTime = new int[n];
            int[] burstTime = new int[n];
            int[] priority = new int[n];

            // Automatically assign process names (A, B, C, ...)
            for (int i = 0; i < n; i++) {
                processID[i] = String.valueOf((char) ('A' + i)); // Assigning 'A', 'B', 'C', ...
                System.out.println("\nProcess " + processID[i] + ":");
                System.out.print("Arrival Time: ");
                arrivalTime[i] = getValidNonNegativeInput();
                System.out.print("Burst Time: ");
                burstTime[i] = getValidPositiveInput();

                if (choice == 4 || choice == 5) {
                    System.out.print("Priority: ");
                    priority[i] = getValidNonNegativeInput();
                }
            }

            System.out.println();

            // process the selected algorithm
            try {
                switch (choice) {
                    case 1:
                        // FCFS d2
                        FCFS fcfs = new FCFS();

                        for (int i = 0; i < n; i++) {
                            fcfs.addProcess(arrivalTime[i], burstTime[i]);
                        }

                        fcfs.execute();
                        break;
                    case 2:
                        SJFClass sjf = new SJFClass();

                        // Add processes with the collected arrival times and burst times
                        for (int i = 0; i < n; i++) {
                            sjf.addProcess(processID[i], arrivalTime[i], burstTime[i]);
                        }

                        // Execute the scheduling algorithm
                        sjf.execute();
                        break;
                    case 3:
                        // srtf
                        SRTF srtf = new SRTF();

                        for (int i = 0; i < n; i++) {
                            srtf.addProcess(arrivalTime[i], burstTime[i]);
                        }
                        srtf.execute();
                        break;
                    case 4:
                        System.out.println(""); // priority (non-preemptive) d2
                        break;
                    case 5:
                        // Priority (Preemptive) Scheduling
                        PreemptivePriority pp = new PreemptivePriority();

                        // Add processes to PreemptivePriority
                        for (int i = 0; i < n; i++) {
                            pp.addProcess(arrivalTime[i], burstTime[i], priority[i]);
                        }

                        // Execute the scheduling algorithm
                        pp.execute();
                        break;
                    case 6:
                        System.out.print("\nEnter Time Quantum (positive integer): ");
                        int timeQuantum = getValidPositiveInput();
                        RoundRobinClass rr = new RoundRobinClass(timeQuantum);

                        // add processes with the collected arrival times and burst times
                        for (int i = 0; i < n; i++) {
                            rr.addProcess(arrivalTime[i], burstTime[i]);
                        }

                        // call the correct method name
                        rr.schedule();
                        break;
                }
            } catch (Exception e) {
                System.out.println("\nAn error occurred: " + e.getMessage());
            }

            // Ask if user wants to continue
            System.out.print("\nDo you want to try another scheduling algorithm? (y/n): ");
            String response = scanner.next().trim().toLowerCase();
            if (response.equals("n")) {
                System.out.println("\nThank you for using the CPU Scheduling Program!");
                continueProgram = false;
            }
        }
        scanner.close();
    }

    // Helper method to get valid choice within a range
    private static int getValidChoice(int min, int max) {
        int choice;
        while (true) {
            try {
                choice = scanner.nextInt();
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.print("Invalid input. Please enter a number between " + min + " and " + max + ": ");
                scanner.next(); // Clear invalid input
            }
        }
    }

    // Helper method to get valid non-negative input
    private static int getValidNonNegativeInput() {
        int input;
        while (true) {
            try {
                input = scanner.nextInt();
                if (input >= 0) {
                    return input;
                } else {
                    System.out.print("Please enter a non-negative number: ");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.print("Invalid input. Please enter a non-negative number: ");
                scanner.next(); // Clear invalid input
            }
        }
    }

    // Helper method to get valid positive input
    private static int getValidPositiveInput() {
        int input;
        while (true) {
            try {
                input = scanner.nextInt();
                if (input > 0) {
                    return input;
                } else {
                    System.out.print("Please enter a positive number: ");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.print("Invalid input. Please enter a positive number: ");
                scanner.next(); // Clear invalid input
            }
        }
    }
}
