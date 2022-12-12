%{
#include <stdio.h>
%}

%token OPEN_BLOCK CLOSE_BLOCK
%token OPEN_COND CLOSE_COND
%token INSTR_DELIM
%token INCLUDE IOSTREAM USING NAMESPACE STD MAIN RETURN
%token IF WHILE DO
%token TRY CATCH THROW
%token CIN COUT ENDL
%token DATA_TYPE
%token TYPEDEF STRUCT
%token ASSIGN_OP
%left TERNARY_SELECTION
%left TERNARY_ELSE
%left ARITH_OP
%left LOGICAL_OP
%token LEFT_SHIFT RIGHT_SHIFT
%token ID
%token CONST

%%

program:        INCLUDE IOSTREAM
                USING NAMESPACE STD INSTR_DELIM
                DATA_TYPE MAIN OPEN_COND CLOSE_COND OPEN_BLOCK
                instr_list
                RETURN CONST INSTR_DELIM CLOSE_BLOCK                    { printf("GOOD FILE FORMAT\n"); }

instr_list:     instr |
                instr instr_list

instr:          simple_instr |
                error_block

simple_instr:   type_def |
                declaration |
                assignment |
                selection |
                cycle |
                read |
                write |
                cycle2 |
                throw_error

error_block:    TRY OPEN_BLOCK
                simple_instr CLOSE_BLOCK
                CATCH OPEN_COND DATA_TYPE ID CLOSE_COND OPEN_BLOCK
                simple_instr CLOSE_BLOCK

type_def:       TYPEDEF STRUCT ID OPEN_BLOCK
                decl_list CLOSE_BLOCK ID INSTR_DELIM

decl_list:      declaration |
                declaration decl_list

declaration:    DATA_TYPE ID INSTR_DELIM

assignment:     ID ASSIGN_OP expr INSTR_DELIM

expr:           ID |
                CONST |
                ternary |
                expr ARITH_OP expr

ternary:        expr LOGICAL_OP expr TERNARY_SELECTION
                expr TERNARY_ELSE expr

selection:      IF OPEN_COND condition CLOSE_COND OPEN_BLOCK
                instr_list CLOSE_BLOCK

condition:      expr LOGICAL_OP expr

cycle:          WHILE OPEN_COND condition CLOSE_COND OPEN_BLOCK
                instr_list CLOSE_BLOCK

cycle2:         DO OPEN_BLOCK
                instr_list
                CLOSE_BLOCK WHILE OPEN_COND condition CLOSE_COND INSTR_DELIM

read:           CIN RIGHT_SHIFT ID INSTR_DELIM

write:          COUT LEFT_SHIFT message INSTR_DELIM

message:        ID |
                ENDL

throw_error:    THROW ID INSTR_DELIM

%%

int main(int argc, char **argv)
{
    yyparse();
    return 0;
}
