package Utils;

import java.util.ArrayList;
import java.util.List;

public class FIPTable {
    public static class TableRow {
        private final String atom;
        private final Integer atomCode;
        private final Integer symbolCode;

        public TableRow(String atom, Integer atomCode, Integer symbolCode) {
            this.atom = atom;
            this.atomCode = atomCode;
            this.symbolCode = symbolCode;
        }

        public String getAtom() {
            return atom;
        }

        public Integer getAtomCode() {
            return atomCode;
        }

        public Integer getSymbolCode() {
            return symbolCode;
        }
    }

    private final List<TableRow> rows;

    public FIPTable() {
        this.rows = new ArrayList<>();
    }

    public TableRow addRow(String token, Integer atomCode, Integer symbolCode) {
        TableRow tr = new TableRow(token, atomCode, symbolCode);

        rows.add(tr);

        return tr;
    }

    public List<TableRow> getAll() {
        return rows;
    }
}
