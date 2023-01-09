import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class SyntacticAnalyser {
    public static Grammar parseGrammar(String filePath) {
        Grammar grammar = new Grammar();
        Pattern nonterminal = Pattern.compile("^[A-Z]$");
        Pattern terminal = Pattern.compile("^[^A-Z]$");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line == null) {
                return grammar;
            }
            String[] lineMembers = line.strip().split("->");
            String[] leftHandMembers = lineMembers[0].split("");
            String[] rightHandMembers = lineMembers[1].split("");
            grammar.startSymbol = leftHandMembers[0];
            grammar.nonterminals.add(grammar.startSymbol);

            for (String symbol: leftHandMembers) {
                if (symbol.equals("€")) {
                    continue;
                }
                if (symbol.toUpperCase().equals(symbol) && !grammar.nonterminals.contains(symbol)) {
                    grammar.nonterminals.add(symbol);
                } else if (!symbol.toUpperCase().equals(symbol) && !grammar.terminals.contains(symbol)) {
                    grammar.terminals.add(symbol);
                }
            }
            for (String symbol: rightHandMembers) {
                if (symbol.equals("€")) {
                    continue;
                }
                if (symbol.toUpperCase().equals(symbol) && !grammar.nonterminals.contains(symbol)) {
                    grammar.nonterminals.add(symbol);
                } else if (!symbol.toUpperCase().equals(symbol) && !grammar.terminals.contains(symbol)) {
                    grammar.terminals.add(symbol);
                }
            }
            grammar.productionRules.add(new Grammar.ProductionRule(List.of(leftHandMembers), List.of(rightHandMembers)));

            while((line = reader.readLine()) != null) {
                lineMembers = line.strip().split("->");
                leftHandMembers = lineMembers[0].split("");
                rightHandMembers = lineMembers[1].split("");

                for (String symbol: leftHandMembers) {
                    if (symbol.equals("€")) {
                        continue;
                    }
                    if (symbol.toUpperCase().equals(symbol) && !grammar.nonterminals.contains(symbol)) {
                        grammar.nonterminals.add(symbol);
                    } else if (!symbol.toUpperCase().equals(symbol) && !grammar.terminals.contains(symbol)) {
                        grammar.terminals.add(symbol);
                    }
                }
                for (String symbol: rightHandMembers) {
                    if (symbol.equals("€")) {
                        continue;
                    }
                    if (nonterminal.matcher(symbol).matches() && !grammar.nonterminals.contains(symbol)) {
                        grammar.nonterminals.add(symbol);
                    } else if (terminal.matcher(symbol).matches() && !grammar.terminals.contains(symbol)) {
                        grammar.terminals.add(symbol);
                    }
                }
                grammar.productionRules.add(new Grammar.ProductionRule(List.of(leftHandMembers), List.of(rightHandMembers)));
            }

            return grammar;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
