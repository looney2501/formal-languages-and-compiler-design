import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    private static boolean finished;
    private static FiniteStateMachine finiteStateMachine;
    public static void main(String[] args) {
        run();
    }

    private static void printMenu() {
        String[] menu = {
                "Menu:",
                "1.States",
                "2.Alphabet",
                "3.Initial state",
                "4.Final states",
                "5.Transitions",
                "6.Verify if a sequence is accepted",
                "7.Longest accepted prefix",
                "0.Exit"
        };
        for (String s : menu) {
            System.out.println(s);
        }
    }

    private static void run() {
        String fileName = "resources\\finite-state-machine-input\\inclass1.in";
        try {
            finiteStateMachine = FiniteStateMachine.readFromFile(fileName);
            finished = false;
            while (!finished) {
                printMenu();
                Scanner scanner = new Scanner(System.in);
                String cmd = scanner.nextLine();
                if (Objects.equals(cmd, "0")) {
                    finished = true;
                } else if (Objects.equals(cmd, "1")) {
                    printStates();
                } else if (Objects.equals(cmd, "2")) {
                    printAlphabet();
                } else if (Objects.equals(cmd, "3")) {
                    printInitialState();
                } else if (Objects.equals(cmd, "4")) {
                    printFinalStates();
                } else if (Objects.equals(cmd, "5")) {
                    printTransitions();
                } else if (Objects.equals(cmd, "6")) {
                    checkSequence();
                } else if (Objects.equals(cmd, "7")) {
                    printLongestAcceptedPrefix();
                } else {
                    printWrongCommand();
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void printWrongCommand() {
        System.out.println("Invalid command!");
    }

    private static void printLongestAcceptedPrefix() {
        if (finiteStateMachine.isDeterministic()) {
            System.out.println("Enter a sequence to find its longest accepted prefix:");
            Scanner scanner = new Scanner(System.in);
            String sequence = scanner.nextLine().strip();
            FiniteStateMachine.PrefixSequence prefixSequence = finiteStateMachine.longestPrefixAccepted(sequence);
            System.out.println(prefixSequence.isAccepted() ? prefixSequence.getSequence() : "No valid prefix");
        } else {
            System.out.println("Not a deterministic finite state machine!");
        }
    }

    private static void checkSequence() {
        if (finiteStateMachine.isDeterministic()) {
            System.out.println("Enter a sequence to validate:");
            Scanner scanner = new Scanner(System.in);
            String sequence = scanner.nextLine().strip();
            System.out.println(finiteStateMachine.acceptsSequence(sequence) ? "Valid" : "Invalid");
        } else {
            System.out.println("Not a deterministic finite state machine!");
        }
    }

    private static void printTransitions() {
        System.out.println("Transitions:");
        finiteStateMachine.getTransitions()
                .forEach(tr -> System.out.println(tr[0] + "--" + tr[1] + "->" + tr[2]));
    }

    private static void printFinalStates() {
        System.out.print("Final states: ");
        Arrays.stream(finiteStateMachine.getFinalStates())
                .forEach(x -> System.out.print(x + " "));
        System.out.println();
    }

    private static void printInitialState() {
        System.out.println("Initial state: " + finiteStateMachine.getInitialState());
    }

    private static void printAlphabet() {
        System.out.print("Symbols alphabet: ");
        Arrays.stream(finiteStateMachine.getAlphabet())
                .forEach(x -> System.out.print(x + " "));
        System.out.println();
    }

    private static void printStates() {
        System.out.print("States alphabet: ");
        Arrays.stream(finiteStateMachine.getStates())
                .forEach(x -> System.out.print(x + " "));
        System.out.println();
    }
}
