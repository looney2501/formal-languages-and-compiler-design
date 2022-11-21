#include <string.h>

char atomsTable[42][20] = {
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
        "throw",
        "catch",
        "try",
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
    for (int i = 2; i < 42; i++) {
        if (strcmp(atomsTable[i], atom) == 0) return i;
    }
    return -1;
}