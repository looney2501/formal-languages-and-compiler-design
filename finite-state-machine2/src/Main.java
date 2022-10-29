import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String fileName = "resources\\finite-state-machine-input\\input1.in";
        try {
            FiniteStateMachine finiteStateMachine = FiniteStateMachine.readFromFile(fileName);
            finiteStateMachine.getStates();

            String goodSequence = "cd";
            String badSequence = "ca";
            System.out.println(finiteStateMachine.acceptsSequence(goodSequence));
            System.out.println(finiteStateMachine.acceptsSequence(badSequence));
            System.out.println(finiteStateMachine.acceptsSequence("d"));
            System.out.println(finiteStateMachine.acceptsSequence("abcd"));
            System.out.println(finiteStateMachine.acceptsSequence("aaa"));
            System.out.println(finiteStateMachine.acceptsSequence("cc"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
