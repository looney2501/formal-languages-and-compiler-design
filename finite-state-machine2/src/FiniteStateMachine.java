import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FiniteStateMachine {
    public static FiniteStateMachine readFromFile(String filename) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {
            String[] states = bufferedReader.readLine().split(" ");
            String[] alphabetStringArray = bufferedReader.readLine().split(" ");
            Character[] alphabet  = new Character[alphabetStringArray.length];
            for (int i = 0; i < alphabetStringArray.length; i++) {
                alphabet[i] = alphabetStringArray[i].toCharArray()[0];
            }
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Edge {


        private String destinationState;
        private Character symbol;
        public Edge(String destinationState, Character symbol) {
            this.destinationState = destinationState;
            this.symbol = symbol;
        }

    }
    public static class PrefixSequence {

        private String sequence;
        private boolean isAccepted;
        public PrefixSequence(String sequence, boolean isAccepted) {
            this.sequence = sequence;
            this.isAccepted = isAccepted;
        }

        public String getSequence() {
            return sequence;
        }

        public boolean isAccepted() {
            return isAccepted;
        }

    }

    private String[] states;
    private Character[] alphabet;
    private String initialState;
    private String[] finalStates;
    private Map<String, List<Edge>> adjList;
    private List<String[]> transitions;
    private boolean isDeterministic;
    private boolean isEpsilonAccepted;

    public FiniteStateMachine(String[] states,
                              Character[] alphabet,
                              String initialState,
                              String[] finalStates,
                              List<String[]> transitions) {
        this.states = states;
        this.alphabet = alphabet;
        this.initialState = initialState;
        this.finalStates = finalStates;
        this.transitions = transitions;
        this.isDeterministic = true;
        this.isEpsilonAccepted = false;
        this.buildGraph();
    }

    private void buildGraph() {
        this.adjList = new HashMap<>();
        for (var transition : transitions) {
            String sourceState = transition[0];
            Character symbol = transition[1].toCharArray()[0];
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
        this.isEpsilonAccepted = Arrays.asList(finalStates).contains(initialState);
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

    public boolean acceptsSequence(String symbolsSequence){
        String node = this.initialState;
        for(Character symbol: symbolsSequence.toCharArray()) {
            List<Edge> destinations = adjList.get(node);
            if(destinations == null) {
                return false;
            }
            boolean canContinue = false;
            for(Edge edge: destinations) {
                if(edge.symbol.equals(symbol)) {
                    node = edge.destinationState;
                    canContinue = true;
                    break;
                }
            }
            if(!canContinue)
                return false;
        }
        return isFinalState(node);
    }

    public PrefixSequence longestPrefixAccepted(String symbolsSequence){
        String lastAcceptedSequence = "";
        boolean isAccepted;
        StringBuilder currentSequence = new StringBuilder();
        String node = this.initialState;
        for(Character symbol: symbolsSequence.toCharArray()) {
            if (isFinalState(node)) {
                lastAcceptedSequence = currentSequence.toString();
            }
            List<Edge> destinations = adjList.get(node);
            if(destinations == null) {
                if (lastAcceptedSequence.equals("")) {
                    isAccepted = isEpsilonAccepted;
                } else {
                    isAccepted = true;
                }
                return new PrefixSequence(lastAcceptedSequence, isAccepted);
            }
            boolean canContinue = false;
            for(Edge edge: destinations) {
                if(edge.symbol.equals(symbol)) {
                    node = edge.destinationState;
                    currentSequence.append(edge.symbol);
                    canContinue = true;
                    break;
                }
            }
            if(!canContinue) {
                if (lastAcceptedSequence.equals("")) {
                    isAccepted = isEpsilonAccepted;
                } else {
                    isAccepted = true;
                }
                return new PrefixSequence(lastAcceptedSequence, isAccepted);
            }
        }
        if (isFinalState(node)) {
            lastAcceptedSequence = currentSequence.toString();
        }
        if (lastAcceptedSequence.equals("")) {
            isAccepted = isEpsilonAccepted;
        } else {
            isAccepted = true;
        }
        return new PrefixSequence(lastAcceptedSequence, isAccepted);
    }

    private boolean isFinalState(String node) {
        return Arrays.asList(finalStates).contains(node);
    }

    public String[] getStates() {
        return states;
    }

    public Character[] getAlphabet() {
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

    public boolean isDeterministic() {
        return this.isDeterministic;
    }
}
