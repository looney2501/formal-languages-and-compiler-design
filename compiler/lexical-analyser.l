%{
#include <stdio.h>
#include <string.h>
#include "attrib.h"
#include "syntactic-analyser.tab.h"
%}

%option noyywrap
%option yylineno

int_type        (int)
open_cond       (\()
close_cond      (\))
open_block      (\{)
close_block     (\})
instr_delim     (;)
assign_op       (=)
add_op          (\+)
sub_op          (\-)
mul_op          (\*)
div_op          (\/)
left_shift      (<<)
right_shift     (>>)
identifier      (([_a-zA-Z])([_a-zA-Z0-9]){0,7})
constant        (([+\-]?([1-9]?[0-9]*[lL]?)(\.[0-9]+)?)[fF]?)
white_space     [ \t\r\n]+

%%
{white_space}
(#include)                  {
                                return INCLUDE;
                            }
(<iostream>)                {
                                return IOSTREAM;
                            }
using                       {
                                return USING;
                            }
namespace                   {
                                return NAMESPACE;
                            }
std                         {
                                return STD;
                            }
main                        {
                                return MAIN;
                            }
return                      {
                                return RETURN;
                            }
cin                         {
                                return CIN;
                            }
cout                        {
                                return COUT;
                            }
{open_cond}                 {
                                return OPEN_COND;
                            }
{close_cond}                {
                                return CLOSE_COND;
                            }
{open_block}                {
                                return OPEN_BLOCK;
                            }
{close_block}               {
                                return CLOSE_BLOCK;
                            }
{instr_delim}               {
                                return INSTR_DELIM;
                            }
{assign_op}                 {
                                return ASSIGN_OP;
                            }
{add_op}                    {
                                return ADD;
                            }
{sub_op}                    {
                                return SUB;
                            }
{mul_op}                    {
                                return MUL;
                            }
{div_op}                    {
                                return DIV;
                            }
{left_shift}                {
                                return LEFT_SHIFT;
                            }
{right_shift}               {
                                return RIGHT_SHIFT;
                            }
{int_type}                  {
                                return INT_TYPE;
                            }
{identifier}                {
                                strcpy(yylval.varname, yytext);
                                return ID;
                            }
{constant}                  {
                                strcpy(yylval.varname, yytext);
                                return CONST;
                            }
.                           {
                                yyerror("lexical error");
                            }
%%

void yyerror(char *s)
{
    fprintf(stderr, "error: %s:%d\n", s, yylineno);
    exit(-1);
}
