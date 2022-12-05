public class Main {
    public static void main(String[] args) {
        Grammar grammar = SyntacticAnalyser.parseGrammar("resources\\syntactic-analyser-input\\production-rules.in");
        System.out.println("Simbol start: " + grammar.startSymbol);
        System.out.println("Nonterminals:");
        grammar.nonterminals.forEach(System.out::println);
        System.out.println("Terminals:");
        grammar.terminals.forEach(System.out::println);
        System.out.println("Production rules:");
        grammar.productionRules.forEach((x) -> {
            StringBuilder leftHand = new StringBuilder();
            x.leftHandSymbols.forEach(leftHand::append);
            StringBuilder rightHand = new StringBuilder();
            x.rightHandSymbols.forEach(rightHand::append);
            System.out.println(leftHand + " -> " + rightHand);
        });
    }
}
