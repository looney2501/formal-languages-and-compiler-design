import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class LexicalAnalyser {
    private static List<Map.Entry<String, String>> tokens;
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
    private static final Pattern constantStringPattern = Pattern.compile("^(\"[^\" ]*\")$");

    private static final Pattern identifierPattern = Pattern.compile("^([_a-zA-Z])([_a-zA-Z0-9])*$");

    private static final Pattern keywordPattern = Pattern.compile(
            "^(#include|<iostream>|using|namespace|std|main|return|" +
                    "typedef|struct|int|double|if|while|cin|cout)$");

    public static void analyse(String path) {
        tokens = new ArrayList<>();
        try {
            parseLines(new File(path));
            writeToOutput(new File("resources/analyser-output.txt"));
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
                } else if (isConstant(token)) {
                    tokens.add(new AbstractMap.SimpleEntry<>(token, "Constant"));
                } else if (validIdentifier(token)) {
                    tokens.add(new AbstractMap.SimpleEntry<>(token, "ID"));
                } else {
                    throw new LexicalError(lineNo, right+columnNoDiff);
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

    private static boolean isConstant(String token) {
        return constantIntegerPattern.matcher(token).matches() ||
                constantRealPattern.matcher(token).matches() ||
                constantStringPattern.matcher(token).matches();
    }

    private static boolean validIdentifier(String token) {
        return identifierPattern.matcher(token).matches();
    }

    private static boolean isSpace(String c) {
        return c.equals(" ");
    }

    private static void writeToOutput(File output) {
        try (FileWriter writer = new FileWriter(output)) {
            for (Map.Entry<String, String> entry : tokens) {
                writer.write(entry.getKey() + " -> " + entry.getValue() + '\n');
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
