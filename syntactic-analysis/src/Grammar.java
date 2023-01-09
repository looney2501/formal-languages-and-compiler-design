import java.util.ArrayList;
import java.util.List;

public class Grammar {
    String startSymbol;
    List<String> terminals = new ArrayList<>();
    List<String> nonterminals = new ArrayList<>();
    List<ProductionRule> productionRules = new ArrayList<>();

    public static class ProductionRule {
        List<String> leftHandSymbols;
        List<String> rightHandSymbols;

        public ProductionRule(List<String> leftHandSymbols, List<String> rightHandSymbols) {
            this.leftHandSymbols = leftHandSymbols;
            this.rightHandSymbols = rightHandSymbols;
        }
    }
}
