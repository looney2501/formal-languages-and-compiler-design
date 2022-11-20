#include <string.h>
#include "symbols-table.c"

typedef struct FIPRow {
    char atom[30];
    int atomCode;
    int symbolCode;
} FIPRow;

FIPRow fipTable[300];
int fipTableLen = 0;

void fipTableAdd(char* atom) {
    strcpy(fipTable[fipTableLen].atom, atom);
    fipTable[fipTableLen].atomCode = getAtomCode(atom);
    fipTable[fipTableLen].symbolCode = -1;
    fipTableLen++;
}

void fipTableAddId(char* atom) {
    strcpy(fipTable[fipTableLen].atom, atom);
    fipTable[fipTableLen].atomCode = 0;
    fipTable[fipTableLen].symbolCode = -1;
    fipTableLen++;
}

void fipTableAddConst(char* atom) {
    strcpy(fipTable[fipTableLen].atom, atom);
    fipTable[fipTableLen].atomCode = 1;
    fipTable[fipTableLen].symbolCode = -1;
    fipTableLen++;
}

void updateSymbolCodes() {
    for (int i = 0; i < fipTableLen; i++) {
        if (fipTable[i].atomCode == 0) {
            fipTable[i].symbolCode = idTableGetCode(fipTable[i].atom);
        }
        if (fipTable[i].atomCode == 1) {
            fipTable[i].symbolCode = constTableGetCode(fipTable[i].atom);
        }
    }
}
