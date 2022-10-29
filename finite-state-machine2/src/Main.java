import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String fileName = "resources\\finite-state-machine-input\\input1.in";
        try {
            FiniteStateMachine finiteStateMachine = FiniteStateMachine.readFromFile(fileName);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
