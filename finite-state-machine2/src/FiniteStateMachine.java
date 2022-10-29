import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private static class Edge {

        private String destinationState;
        private String symbol;

        public Edge(String destinationState, String symbol) {
            this.destinationState = destinationState;
            this.symbol = symbol;
        }
    }

    private String[] states;
    private String[] alphabet;
    private String initialState;
    private String[] finalStates;
    private Map<String, List<Edge>> adjList;
    private List<String[]> transitions;
    private boolean isDeterministic;

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
        this.isDeterministic = true;
        this.buildGraph();
    }

    private void buildGraph() {
        this.adjList = new HashMap<>();
        for (var transition : transitions) {
            String sourceState = transition[0];
            String symbol = transition[1];
            String destinationState = transition[2];

            Edge edge = new Edge(destinationState, symbol);
            List<Edge> destinations = this.adjList.get(sourceState);

            if (destinations == null) {
                destinations = new ArrayList<>();
                destinations.add(edge);
                this.adjList.put(sourceState, destinations);
            } else {
                destinations.add(edge);
            }

            if (this.isDeterministic) {
                checkNedeterministic(destinations);
            }
        }
    }

    private void checkNedeterministic(List<Edge> destinations) {
        long sameSymbolTransitionsNumber = destinations.stream()
                .collect(Collectors.groupingBy(
                        edge -> edge.symbol,
                        Collectors.counting()))
                .entrySet()
                .stream()
                .filter(x -> x.getValue() > 1)
                .count();
        if (sameSymbolTransitionsNumber != 0) {
            this.isDeterministic = false;
        }
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
