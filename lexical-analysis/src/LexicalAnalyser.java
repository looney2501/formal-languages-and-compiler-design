import Utils.CustomTable;
import Utils.FIPTable;
import Utils.ItemType;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class LexicalAnalyser {
    //tables
    private static final List<Map.Entry<String, String>> tokens = new ArrayList<>();
    private static final CustomTable constantsTable = new CustomTable();
    private static final CustomTable identifiersTable = new CustomTable();
    private static final HashMap<String, Integer> atomsTable = new HashMap<>();
    private static final FIPTable FIPTable = new FIPTable();

    //finite state machines
    private static final FiniteStateMachine delimiterFSM =
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\delimiters-spec.in");
    private static final List<FiniteStateMachine> keywordFSMs = List.of(
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-include-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-iostream-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-using-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-namespace-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-std-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-main-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-return-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-typedef-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-struct-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-int-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-double-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-if-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-while-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-cin-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-cout-spec.in"),
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\keyword-endl-spec.in")
    );
    private static final FiniteStateMachine identifierFSM =
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\identifiers-spec.in");
    private static final FiniteStateMachine integerConstantFSM =
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\cpp-integers-spec.in");
    private static final FiniteStateMachine realConstantFSM =
            FiniteStateMachine.readFromFile("resources\\finite-state-machine-input\\cpp-real-numbers-spec.in");

    public static void analyse(String path) {
        try {
            parseLines(new File(path));
            createTables();
            writeToOutput();
        } catch (LexicalError e) {
            System.err.println(e.getMessage());
        }
    }

    private static void parseLines(File file) throws LexicalError {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNo = 1;
            while ((line = reader.readLine()) != null) {
                parseLine(line, lineNo);
                lineNo++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void parseLine(String line, int lineNo) throws LexicalError {
        while (!line.isEmpty()) {
            line = line.strip();
            Map.Entry<String, String> tokenType = getNextToken(line);
            if (tokenType == null) {
                throw new LexicalError(lineNo);
            } else {
                String token = tokenType.getKey();
                tokens.add(tokenType);
                line = line.replaceFirst(Pattern.quote(token), "");
            }
        }
    }

    private static Map.Entry<String, String> getNextToken(String line) {
        FiniteStateMachine.PrefixSequence prefixSequence;

        if ((prefixSequence = getNextDelimiter(line)).isAccepted()) {
            return new AbstractMap.SimpleEntry<>(prefixSequence.getSequence(), "DELIMITER");
        } else if ((prefixSequence = getNextKeyword(line)).isAccepted()) {
            return new AbstractMap.SimpleEntry<>(prefixSequence.getSequence(), "KEYWORD");
        } else if ((prefixSequence = getNextIdentifier(line)).isAccepted()) {
            return new AbstractMap.SimpleEntry<>(prefixSequence.getSequence(), "ID");
        } else if ((prefixSequence = getNextDoubleConstant(line)).isAccepted()) {
            return new AbstractMap.SimpleEntry<>(prefixSequence.getSequence(), "DOUBLE");
        } else if ((prefixSequence = getNextIntegerConstant(line)).isAccepted()) {
            return new AbstractMap.SimpleEntry<>(prefixSequence.getSequence(), "INTEGER");
        }

        return null;
    }

    private static FiniteStateMachine.PrefixSequence getNextDelimiter(String line) {
        return delimiterFSM.longestPrefixAccepted(line);
    }

    private static FiniteStateMachine.PrefixSequence getNextKeyword(String line) {
        var optionalPrefix = keywordFSMs.stream()
                .map(x -> x.longestPrefixAccepted(line))
                .filter(FiniteStateMachine.PrefixSequence::isAccepted)
                .max(Comparator.comparing(FiniteStateMachine.PrefixSequence::getSequence));
        return optionalPrefix.orElseGet(() -> new FiniteStateMachine.PrefixSequence("", false));
    }

    private static FiniteStateMachine.PrefixSequence getNextIdentifier(String line) {
        return identifierFSM.longestPrefixAccepted(line);
    }

    private static FiniteStateMachine.PrefixSequence getNextDoubleConstant(String line) {
        return realConstantFSM.longestPrefixAccepted(line);
    }

    private static FiniteStateMachine.PrefixSequence getNextIntegerConstant(String line) {
        return integerConstantFSM.longestPrefixAccepted(line);
    }

    private static void createTables() {
        createConstantsTable();
        createIdentifiersTable();
        createAtomsTable();
        createFIPTable();
    }

    private static void createConstantsTable() {
        Map<String, ItemType> constants = new HashMap<>();
        tokens.forEach(entry -> {
            if (!constants.containsKey(entry.getKey()) && (entry.getValue().equals("DOUBLE") || entry.getValue().equals("INTEGER"))) {
                constants.put(entry.getKey(), ItemType.valueOf(entry.getValue()));
            }
        });
        List<CustomTable.TableRow> tableRows = new ArrayList<>();
        constants.forEach((k, v) -> tableRows.add(new CustomTable.TableRow(k, v)));
        constantsTable.addFromList(tableRows);
    }

    private static void createIdentifiersTable() {
        Map<String, ItemType> identifiers = new HashMap<>();
        tokens.forEach(entry -> {
            if (!identifiers.containsKey(entry.getKey()) && entry.getValue().equals("ID")) {
                identifiers.put(entry.getKey(), ItemType.ID);
            }
        });
        List<CustomTable.TableRow> tableRows = new ArrayList<>();
        identifiers.forEach((k, v) -> tableRows.add(new CustomTable.TableRow(k, v)));
        identifiersTable.addFromList(tableRows);
    }

    private static void createAtomsTable() {
        atomsTable.put("ID", 0);
        atomsTable.put("CONST", 1);
        atomsTable.put("#include", 2);
        atomsTable.put("iostream", 3);
        atomsTable.put("using", 4);
        atomsTable.put("namespace", 5);
        atomsTable.put("std", 6);
        atomsTable.put("main", 7);
        atomsTable.put("return", 8);
        atomsTable.put("typedef", 9);
        atomsTable.put("struct", 10);
        atomsTable.put("int", 11);
        atomsTable.put("double", 12);
        atomsTable.put("if", 13);
        atomsTable.put("while", 14);
        atomsTable.put("cin", 15);
        atomsTable.put("cout", 16);
        atomsTable.put("endl", 17);
        atomsTable.put(";", 18);
        atomsTable.put("<", 19);
        atomsTable.put(">", 20);
        atomsTable.put("!", 21);
        atomsTable.put("+", 22);
        atomsTable.put("-", 23);
        atomsTable.put("*", 24);
        atomsTable.put("/", 25);
        atomsTable.put("%", 26);
        atomsTable.put("=", 27);
        atomsTable.put("(", 28);
        atomsTable.put(")", 29);
        atomsTable.put("{", 30);
        atomsTable.put("}", 31);
        atomsTable.put(".", 32);
        atomsTable.put("<<", 33);
        atomsTable.put(">>", 34);
        atomsTable.put("<=", 35);
        atomsTable.put(">=", 36);
        atomsTable.put("==", 37);
        atomsTable.put("!=", 38);
    }

    private static void createFIPTable() {
        tokens.forEach(entry -> {
            String key = entry.getKey();
            Integer atomCode = null;
            Integer symbolCode = null;
            if (entry.getValue().equals("ID")) {
                atomCode = atomsTable.get("ID");
                symbolCode = identifiersTable.get(key).getCode();
            } else if (entry.getValue().equals("INTEGER") || entry.getValue().equals("DOUBLE")) {
                atomCode = atomsTable.get("CONST");
                symbolCode = constantsTable.get(key).getCode();
            } else {
                atomCode = atomsTable.get(key);
            }
            FIPTable.addRow(key, atomCode, symbolCode);
        });
    }

    private static void writeToOutput() {
        writeConstantsToOutput();
        writeIdentifiersToOutput();
        writeAtomsToOutput();
        writeFIPToOutput();
    }

    private static void writeConstantsToOutput() {
        try (FileWriter writer = new FileWriter(new File("resources/analyser-output/constants-table.csv"))) {
            writer.write("CONSTANT,CODTS,TYPE\n");
            for (var tableRow : constantsTable.getAll()) {
                writer.write(tableRow.getKey() + ',' + tableRow.getCode() + ',' + tableRow.getType() + '\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeIdentifiersToOutput() {
        try (FileWriter writer = new FileWriter(new File("resources/analyser-output/identifiers-table.csv"))) {
            writer.write("ID,CODTS\n");
            for (var tableRow : identifiersTable.getAll()) {
                writer.write(tableRow.getKey() + ',' + tableRow.getCode() + '\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeAtomsToOutput() {
        try (FileWriter writer = new FileWriter(new File("resources/analyser-output/atoms-table.csv"))) {
            writer.write("ATOM,CODATOM\n");
            atomsTable.forEach((k, v) -> {
                try {
                    writer.write(k + ',' + v + '\n');
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeFIPToOutput() {
        try (FileWriter writer = new FileWriter(new File("resources/analyser-output/fip-table.csv"))) {
            writer.write("ATOM,CODATOM,CODTS\n");
            FIPTable.getAll().forEach(tr -> {
                try {
                    writer.write(tr.getAtom() + ',' + tr.getAtomCode() + ',' + tr.getSymbolCode() + '\n');
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
