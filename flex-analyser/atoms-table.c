#include <string.h>

char atomsTable[39][20] = {
        "ID",
        "CONST",
        "#include",
        "iostream",
        "using",
        "namespace",
        "std",
        "main",
        "return",
        "typedef",
        "struct",
        "int",
        "double",
        "if",
        "while",
        "cin",
        "cout",
        "endl",
        ";",
        "<",
        ">",
        "!",
        "+",
        "-",
        "*",
        "/",
        "%",
        "=",
        "(",
        ")",
        "{",
        "}",
        ".",
        "<<",
        ">>",
        "<=",
        ">=",
        "==",
        "!="
};

int getAtomCode(char* atom) {
    for (int i = 2; i < 39; i++) {
        if (strcmp(atomsTable[i], atom) == 0) return i;
    }
    return -1;
}