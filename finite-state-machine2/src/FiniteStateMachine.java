import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FiniteStateMachine {
    public static FiniteStateMachine readFromFile(String filename) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String[] states = bufferedReader.readLine().split(" ");
            String[] alphabet = bufferedReader.readLine().split(" ");
            String initialState = bufferedReader.readLine().split(" ")[0];
            String[] finalStates = bufferedReader.readLine().split(" ");

            List<String[]> transitions = new ArrayList<>();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] transition = line.split(" ");
                transitions.add(transition);
            }

            return new FiniteStateMachine(states, alphabet, initialState, finalStates, transitions);
        }
    }
    private String[] states;
    private String[] alphabet;
    private String initialState;
    private String[] finalStates;

    private List<String[]> transitions;

    public FiniteStateMachine(String[] states,
                              String[] alphabet,
                              String initialState,
                              String[] finalStates,
                              List<String[]> transitions) {
        this.states = states;
        this.alphabet = alphabet;
        this.initialState = initialState;
        this.finalStates = finalStates;
        this.transitions = transitions;
    }

    public String[] getStates() {
        return states;
    }

    public String[] getAlphabet() {
        return alphabet;
    }

    public String getInitialState() {
        return initialState;
    }

    public String[] getFinalStates() {
        return finalStates;
    }

    public List<String[]> getTransitions() {
        return transitions;
    }
}
