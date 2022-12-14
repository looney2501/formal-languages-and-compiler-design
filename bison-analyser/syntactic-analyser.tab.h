
/* A Bison parser, made by GNU Bison 2.4.1.  */

/* Skeleton interface for Bison's Yacc-like parsers in C
   
      Copyright (C) 1984, 1989, 1990, 2000, 2001, 2002, 2003, 2004, 2005, 2006
   Free Software Foundation, Inc.
   
   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.
   
   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */


/* Tokens.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
   /* Put the tokens into the symbol table, so that GDB and other debuggers
      know about them.  */
   enum yytokentype {
     OPEN_BLOCK = 258,
     CLOSE_BLOCK = 259,
     OPEN_COND = 260,
     CLOSE_COND = 261,
     INSTR_DELIM = 262,
     INCLUDE = 263,
     IOSTREAM = 264,
     USING = 265,
     NAMESPACE = 266,
     STD = 267,
     MAIN = 268,
     RETURN = 269,
     IF = 270,
     WHILE = 271,
     DO = 272,
     TRY = 273,
     CATCH = 274,
     THROW = 275,
     CIN = 276,
     COUT = 277,
     ENDL = 278,
     DATA_TYPE = 279,
     TYPEDEF = 280,
     STRUCT = 281,
     ASSIGN_OP = 282,
     TERNARY_SELECTION = 283,
     TERNARY_ELSE = 284,
     ARITH_OP = 285,
     LOGICAL_OP = 286,
     LEFT_SHIFT = 287,
     RIGHT_SHIFT = 288,
     ID = 289,
     CONST = 290
   };
#endif



#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
typedef int YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define yystype YYSTYPE /* obsolescent; will be withdrawn */
# define YYSTYPE_IS_DECLARED 1
#endif

extern YYSTYPE yylval;


