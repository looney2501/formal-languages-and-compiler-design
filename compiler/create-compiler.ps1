bison -d .\syntactic-analyser.y
flex .\lexical-analyser.l
gcc .\lex.yy.c .\syntactic-analyser.tab.c -o compiler
