import Utils.CustomTable;
import Utils.FIPTable;
import Utils.ItemType;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class LexicalAnalyser {
    private static final List<Map.Entry<String, String>> tokens = new ArrayList<>();
    private static final CustomTable constantsTable = new CustomTable();
    private static final CustomTable identifiersTable = new CustomTable();
    private static final HashMap<String, Integer> atomsTable = new HashMap<>();
    private static final FIPTable FIPTable = new FIPTable();
    private static final Pattern singleLogicalOperatorPattern = Pattern.compile("^([<>])$");
    private static final Pattern compoundLogicalOperatorPattern = Pattern.compile("^(==|!=)$");
    private static final Pattern arithmeticOperatorPattern = Pattern.compile("^([-+*/%])$");
    private static final Pattern attributionOperatorPattern = Pattern.compile("^(=)$");
    private static final Pattern IOOperatorPattern = Pattern.compile("^(<<|>>)$");
    private static final Pattern conditionOpenDelimiterPattern = Pattern.compile("^(\\()$");
    private static final Pattern conditionClosedDelimiterPattern = Pattern.compile("^(\\))$");
    private static final Pattern blockOpenDelimiterPattern = Pattern.compile("^(\\{)$");
    private static final Pattern blockClosedDelimiterPattern = Pattern.compile("^(})$");
    private static final Pattern instructionDelimiterPattern = Pattern.compile("^(;)$");
    private static final Pattern otherDelimiterPattern = Pattern.compile("^(!)$");
    private static final Pattern constantIntegerPattern = Pattern.compile("^([+-]?[0-9]+)$");
    private static final Pattern constantRealPattern = Pattern.compile("^([+-]?(([0-9]+\\.[0-9]+)|([0-9]+)))$");
    private static final Pattern identifierPattern = Pattern.compile("^(([_a-zA-Z])([_a-zA-Z0-9]){0,7})$");
    private static final Pattern keywordPattern = Pattern.compile(
            "^(#include|iostream|using|namespace|std|main|return|" +
                    "typedef|struct|int|double|if|while|cin|cout|endl)$");

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
            String strippedLine;
            int lineNo = 1;
            int columnNoDiff;
            while ((line = reader.readLine()) != null) {
                strippedLine = line.strip();
                columnNoDiff = line.indexOf(strippedLine);
                parseLine(line.strip(), lineNo, columnNoDiff);
                lineNo++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void parseLine(String line, int lineNo, int columnNoDiff) throws LexicalError {
        int left = 0, right = 0, len = line.length() - 1;

        while (right <= len && left <= right) {
            String delimiterType = getDelimiterType(line.substring(right, right + 1));
            if (!isDelimiter(delimiterType)) {
                right++;
            }
            if (isDelimiter(delimiterType) && left == right) {
                String delimiter;
                if (right + 2 <= len) {
                    delimiter = line.substring(right, right + 2);
                    if (isCompoundLogicalOperator(delimiter)) {
                        tokens.add(new AbstractMap.SimpleEntry<>(delimiter, "Logical operator"));
                        right += 2;
                    } else if (isIOOperator(delimiter)) {
                        tokens.add(new AbstractMap.SimpleEntry<>(delimiter, "IO operator"));
                        right += 2;
                    } else {
                        delimiter = line.substring(right, right + 1);
                        if (!isSpace(delimiter)) {
                            tokens.add(new AbstractMap.SimpleEntry<>(delimiter, delimiterType));
                        }
                        right++;
                    }
                }
                else {
                    delimiter = line.substring(right, right + 1);
                    if (!isSpace(delimiter)) {
                        tokens.add(new AbstractMap.SimpleEntry<>(delimiter, delimiterType));
                    }
                    right++;
                }
                left = right;
            } else if (isDelimiter(delimiterType) || right == len) {
                String token = line.substring(left, right);

                if (isKeyword(token)) {
                    tokens.add(new AbstractMap.SimpleEntry<>(token, "Keyword"));
                } else {
                    ItemType type;
                    if ((type = isConstant(token)) != null) {
                        tokens.add(new AbstractMap.SimpleEntry<>(token, type.toString()));
                    } else if (validIdentifier(token)) {
                        tokens.add(new AbstractMap.SimpleEntry<>(token, "ID"));
                    } else {
                        throw new LexicalError(lineNo, right+columnNoDiff);
                    }
                }
                left = right;
            }
        }
    }

    private static boolean isDelimiter(String delimiterType) {
        return delimiterType != null;
    }

    private static String getDelimiterType(String c) {
        if (isSingleLogicalOperator(c)) {
            return "Logical Operator";
        } else if (isArithmeticOperator(c)) {
            return "Arithmetic Operator";
        } else if (isAttributionOperator(c)) {
            return "Attribution Operator";
        } else if (isIOOperator(c)) {
            return "IO Operator";
        } else if (isInstructionDelimiter(c) ||
                isBlockClosedDelimiter(c) ||
                isBlockOpenDelimiter(c) ||
                isConditionClosedDelimiter(c) ||
                isConditionOpenDelimiter(c) ||
                isOtherDelimiter(c) ||
                isSpace(c)) {
            return "Delimiter";
        } else return null;
    }

    private static boolean isSingleLogicalOperator(String c) {
        return singleLogicalOperatorPattern.matcher(c).matches();
    }

    private static boolean isCompoundLogicalOperator(String c) {
        return compoundLogicalOperatorPattern.matcher(c).matches();
    }

    private static boolean isArithmeticOperator(String c) {
        return arithmeticOperatorPattern.matcher(c).matches();
    }

    private static boolean isAttributionOperator(String c) {
        return attributionOperatorPattern.matcher(c).matches();
    }

    private static boolean isIOOperator(String c) {
        return IOOperatorPattern.matcher(c).matches();
    }

    private static boolean isConditionOpenDelimiter(String c) {
        return conditionOpenDelimiterPattern.matcher(c).matches();
    }

    private static boolean isConditionClosedDelimiter(String c) {
        return conditionClosedDelimiterPattern.matcher(c).matches();
    }

    private static boolean isBlockOpenDelimiter(String c) {
        return blockOpenDelimiterPattern.matcher(c).matches();
    }

    private static boolean isBlockClosedDelimiter(String c) {
        return blockClosedDelimiterPattern.matcher(c).matches();
    }

    private static boolean isInstructionDelimiter(String c) {
        return instructionDelimiterPattern.matcher(c).matches();
    }

    private static boolean isOtherDelimiter(String c) {
        return otherDelimiterPattern.matcher(c).matches();
    }

    private static boolean isKeyword(String token) {
        return keywordPattern.matcher(token).matches();
    }

    private static ItemType isConstant(String token) {
        if (constantIntegerPattern.matcher(token).matches()) return ItemType.INTEGER;
        if (constantRealPattern.matcher(token).matches()) return ItemType.DOUBLE;
        return null;
    }

    private static boolean validIdentifier(String token) {
        return identifierPattern.matcher(token).matches();
    }

    private static boolean isSpace(String c) {
        return c.equals(" ");
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

    private static void createTables() {
        createConstantsTable();
        createIdentifiersTable();
        createAtomsTable();
        createFIPTable();
    }
    private static void createConstantsTable() {
        Map<String, ItemType> constants = new HashMap<>();
        tokens.forEach(entry -> {
            if (!constants.containsKey(entry.getKey()) && (entry.getValue().equals("DOUBLE") || entry.getValue().equals("INTEGER") )) {
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
        tokens.forEach(entry -> {
            if (!atomsTable.containsKey("ID") && entry.getValue().equals("ID")) {
                atomsTable.put("ID", atomsTable.size());
            }
            if (!atomsTable.containsKey("CONSTANT") && (entry.getValue().equals("INTEGER") || entry.getValue().equals("DOUBLE"))) {
                atomsTable.put("CONST", atomsTable.size());
            }
            if (!entry.getValue().equals("ID") && !entry.getValue().equals("INTEGER") && !entry.getValue().equals("DOUBLE")
                    && !atomsTable.containsKey(entry.getKey())) {
                atomsTable.put(entry.getKey(), atomsTable.size());
            }
        });
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
}
