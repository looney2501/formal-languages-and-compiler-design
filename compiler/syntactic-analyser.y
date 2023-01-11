%{

#include <stdio.h>
#include <string.h>
#include "attrib.h"
#include "codeASM.h"

char data_segment_buffer[10000];
char code_segment_buffer[10000];
char temp[1000];

int tempnr = 1;
void newTempName(char* s){
  sprintf(s, "temp%d", tempnr);
  tempnr++;
}

%}

%union
{
    char varname[10];
    attributes pairAttrib;
}

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
%token <varname> ID
%token <varname> CONST
%type <pairAttrib> term
%type <pairAttrib> expr

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

declaration:    INT_TYPE ID INSTR_DELIM {
                                            sprintf(temp, DECLARE_INT_ASM_FORMAT, $2);
                                            strcat(data_segment_buffer, temp);
                                        }

assignment:     ID ASSIGN_OP expr INSTR_DELIM {
                                                char temp2[100];
                                                sprintf(temp2, "[%s]", $1);
                                                sprintf(temp, ASSIGN_ASM_FORMAT, $3.varn, temp2);
                                                strcat(code_segment_buffer, temp);
                                              }

expr:           term {
                        strcpy($$.code, $1.code);
                        strcpy($$.varn, $1.varn);
                     }
                | expr ADD expr {
                                    newTempName($$.varn);
                                    sprintf(temp, DECLARE_INT_ASM_FORMAT, $$.varn);
                                    strcat(data_segment_buffer, temp);
                                    sprintf(temp, "[%s]", $$.varn);
                                    strcpy($$.varn, temp);
                                    sprintf($$.code, "%s\n%s\n", $1.code, $3.code);
                                    sprintf(temp, ADD_ASM_FORMAT, $1.varn, $3.varn, $$.varn);
                                    strcat(code_segment_buffer, temp);
                                }
                | expr SUB expr {
                                    newTempName($$.varn);
                                    sprintf(temp, DECLARE_INT_ASM_FORMAT, $$.varn);
                                    strcat(data_segment_buffer, temp);
                                    sprintf(temp, "[%s]", $$.varn);
                                    strcpy($$.varn, temp);
                                    sprintf($$.code, "%s\n%s\n", $1.code, $3.code);
                                    sprintf(temp, SUB_ASM_FORMAT, $1.varn, $3.varn, $$.varn);
                                    strcat(code_segment_buffer, temp);
                                }
                | expr MUL expr {
                                    newTempName($$.varn);
                                    sprintf(temp, DECLARE_INT_ASM_FORMAT, $$.varn);
                                    strcat(data_segment_buffer, temp);
                                    sprintf(temp, "[%s]", $$.varn);
                                    strcpy($$.varn, temp);
                                    sprintf($$.code, "%s\n%s\n", $1.code, $3.code);
                                    sprintf(temp, MUL_ASM_FORMAT, $1.varn, $3.varn, $$.varn);
                                    strcat(code_segment_buffer, temp);
                                }
                | expr DIV expr {
                                    newTempName($$.varn);
                                    sprintf(temp, DECLARE_INT_ASM_FORMAT, $$.varn);
                                    strcat(data_segment_buffer, temp);
                                    sprintf(temp, "[%s]", $$.varn);
                                    strcpy($$.varn, temp);
                                    sprintf($$.code, "%s\n%s\n", $1.code, $3.code);
                                    sprintf(temp, DIV_ASM_FORMAT, $1.varn, $3.varn, $$.varn);
                                    strcat(code_segment_buffer, temp);
                                }

term:           CONST {
                        strcpy($$.code, "");
                        strcpy($$.varn, $1);
                      }
                | ID {
                        strcpy($$.code, "");
                        sprintf($$.varn, "[%s]", $1);
                     }

read:           CIN RIGHT_SHIFT ID INSTR_DELIM {
                                                   sprintf(temp, "[%s]", $3);
                                                   strcpy($3, temp);
                                                   sprintf(temp, READ_ASM_FORMAT, $3);
                                                   strcat(code_segment_buffer, temp);
                                               }

write:          COUT LEFT_SHIFT ID INSTR_DELIM {
                                                    sprintf(temp, "[%s]", $3);
                                                    strcpy($3, temp);
                                                    sprintf(temp, PRINT_ASM_FORMAT, $3);
                                                    strcat(code_segment_buffer, temp);
                                               }

%%

void print_asm_code()
{
    FILE *fptr;
    fptr = fopen("D:\\proiecte\\LFTC\\resources\\compiler-output\\program.asm", "w");
    fprintf(fptr, PROGRAM_HEADER_ASM_FORMAT);
    fprintf(fptr, DATA_SEGMENT, data_segment_buffer);
    fprintf(fptr, CODE_SEGMENT, code_segment_buffer);
    fclose(fptr);
}

int main(int argc, char **argv)
{
    yyparse();
    print_asm_code();
    return 0;
}
