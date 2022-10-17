public class LexicalError extends Exception {
    private int line, column;

    public LexicalError(int line, int column) {
        this.line = line;
        this.column = column;
    }

    @Override
    public String getMessage() {
        return "Lexical error at " + line + ":" + column + ".";
    }
}
