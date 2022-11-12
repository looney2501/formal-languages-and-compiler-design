public class LexicalError extends Exception {
    private int line;

    public LexicalError(int line) {
        this.line = line;
    }

    @Override
    public String getMessage() {
        return "Lexical error at line " + line + ".";
    }
}
