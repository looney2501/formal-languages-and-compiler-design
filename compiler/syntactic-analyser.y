%{
#include <stdio.h>
int finishedDataSegment = 0;
%}

%token OPEN_BLOCK CLOSE_BLOCK
%token OPEN_COND CLOSE_COND
%token INSTR_DELIM
%token INCLUDE IOSTREAM USING NAMESPACE STD MAIN RETURN
%token CIN COUT
%token INT_TYPE
%token ASSIGN_OP
%token LEFT_SHIFT RIGHT_SHIFT
%left ADD SUB
%left MUL DIV
%token ID
%token CONST

%%

program:        INCLUDE IOSTREAM
                USING NAMESPACE STD INSTR_DELIM
                INT_TYPE MAIN OPEN_COND CLOSE_COND OPEN_BLOCK
                instr_list
                RETURN CONST INSTR_DELIM CLOSE_BLOCK                    { printf("GOOD FILE FORMAT\n"); }

instr_list:     instr |
                instr instr_list

instr:          declaration |
                assignment |
                read |
                write

declaration:    INT_TYPE ID INSTR_DELIM

assignment:     ID ASSIGN_OP expr INSTR_DELIM

expr:           ID |
                CONST |
                expr ADD expr { printf("adunare"); } |
                expr SUB expr { printf("scadere"); } |
                expr MUL expr { printf("inmultire"); } |
                expr DIV expr { printf("impartire"); }

read:           CIN RIGHT_SHIFT ID INSTR_DELIM

write:          COUT LEFT_SHIFT ID INSTR_DELIM

%%

void print_initial_code()
{
    printf("bits 32\nglobal start\nextern exit\nimport exit msvcrt.dll\nsegment data use32 class=data\n");
}

int main(int argc, char **argv)
{
    print_initial_code();
    yyparse();
    return 0;
}
