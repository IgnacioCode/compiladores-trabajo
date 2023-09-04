package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.ParserSym;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;
import java.lang.System;

%%

%public
%class Lexer
%unicode
%cup
%line
%column
%throws CompilerException
%eofval{
  return symbol(ParserSym.EOF);
%eofval}


%{
  private Symbol symbol(int type, String texto) {
      System.out.println(texto);
      return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}


LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Identation =  [ \t\f]

OP_MAS = "+"
OP_MULT = "*"
OP_MENOS = "-"
OP_DIV = "/"
OP_ASIG = "="
OP_AND = "and"
OP_OR = "or"
OP_NOT = "not"
COMP_EQ = "=="
COMP_DIST = "!="
COMP_MAYOR = ">"
COMP_MENOR = "<"
COMP_MAYOR_EQ = ">="
COMP_MENOR_EQ = "<="
ABRE_PAR = "("
CIERRA_PAR = ")"
ABRE_LLAVE = "{"
CIERRA_LLAVE = "}"
COND_IF = "if"
COND_ELSE = "else"
CICLO = "while"
TD_INT = "int"
TD_FLOAT = "float"
TD_STRING = "string"
INIC_VARS = "vars"
COMA = ","
DOS_PUNTOS = ":"
PUNTO_COMA = ";"
ABRE_COM = "#/"
CIERRA_COM = "\#"
LEER_TECLADO = "read"
MOSTRAR_PANTALLA = "write"
FUNC_INDICE = "FirstIndexOf"
FUNC_CONCAT = "ConcatenarConRecorte"


Letra = [a-zA-Z]
Digito = [0-9]

WhiteSpace = {LineTerminator} | {Identation}
Identifier = {Letra} ({Letra}|{Digito})*
IntegerConstant = {Digito}+

%%


/* keywords */

<YYINITIAL> {
  /* identifiers */
  {Identifier}                             { return symbol(ParserSym.IDENTIFIER, yytext()); }
  /* Constants */
  {IntegerConstant}                        { return symbol(ParserSym.INTEGER_CONSTANT, yytext()); }

  /* operators */

  {OP_MAS}                                 { return symbol(ParserSym.PLUS, "OP_MAS"); }
  {OP_MENOS}                               { return symbol(ParserSym.SUB, "OP_MENOS"); }
  {OP_MULT}                                { return symbol(ParserSym.MULT, "OP_MULT"); }
  {OP_DIV}                                 { return symbol(ParserSym.DIV, "OP_DIV"); }
  {OP_ASIG}                                { return symbol(ParserSym.ASSIG, "OP_ASIG"); }
  {OP_AND}                                 { return symbol(ParserSym.OP_AND, "OP_AND"); }
  {OP_OR}                                  { return symbol(ParserSym.OP_OR, "OP_OR"); }
  {OP_NOT}                                 { return symbol(ParserSym.OP_NOT, "OP_NOT"); }

  {COMP_EQ}                                { return symbol(ParserSym.COMP_EQ, "COMP_EQ"); }
  {COMP_DIST}                              { return symbol(ParserSym.COMP_DIST, "COMP_DIST"); }
  {COMP_MAYOR}                             { return symbol(ParserSym.COMP_MAYOR, "COMP_MAYOR"); }
  {COMP_MENOR}                             { return symbol(ParserSym.COMP_MENOR, "COMP_MENOR"); }
  {COMP_MAYOR_EQ}                          { return symbol(ParserSym.COMP_MAYOR_EQ, "COMP_MAYOR_EQ"); }
  {COMP_MENOR_EQ}                          { return symbol(ParserSym.COMP_MENOR_EQ, "COMP_MENOR_EQ"); }

  {ABRE_PAR}                               { return symbol(ParserSym.ABRE_PAR, "ABRE_PAR"); }
  {CIERRA_PAR}                             { return symbol(ParserSym.CIERRA_PAR, "CIERRA_PAR"); }
  {ABRE_LLAVE}                             { return symbol(ParserSym.ABRE_LLAVE, "ABRE_LLAVE"); }
  {CIERRA_LLAVE}                           { return symbol(ParserSym.CIERRA_LLAVE, "CIERRA_LLAVE"); }

  {COND_IF}                                { return symbol(ParserSym.COND_IF, "COND_IF"); }
  {COND_ELSE}                              { return symbol(ParserSym.COND_ELSE, "COND_ELSE"); }
  {CICLO}                                  { return symbol(ParserSym.CICLO, "CICLO"); }

  {TD_INT}                                 { return symbol(ParserSym.TD_INT, "TD_INT"); }
  {TD_FLOAT}                               { return symbol(ParserSym.TD_FLOAT, "TD_FLOAT"); }
  {TD_STRING}                              { return symbol(ParserSym.TD_STRING, "TD_STRING"); }

  {INIC_VARS}                              { return symbol(ParserSym.INIC_VARS, "INIC_VARS"); }
  {COMA}                                   { return symbol(ParserSym.COMA, "COMA"); }
  {DOS_PUNTOS}                             { return symbol(ParserSym.DOS_PUNTOS, "DOS_PUNTOS"); }
  {PUNTO_COMA}                             { return symbol(ParserSym.PUNTO_COMA, "PUNTO_COMA"); }
  {ABRE_COM}                               { return symbol(ParserSym.ABRE_COM, "ABRE_COM"); }
  {CIERRA_COM}                             { return symbol(ParserSym.CIERRA_COM, "CIERRA_COM"); }
  {LEER_TECLADO}                           { return symbol(ParserSym.LEER_TECLADO, "LEER_TECLADO"); }
  {MOSTRAR_PANTALLA}                       { return symbol(ParserSym.MOSTRAR_PANTALLA, "MOSTRAR_PANTALLA"); }
  
  {FUNC_INDICE}                            { return symbol(ParserSym.FUNC_INDICE, "FUNC_INDICE"); }
  {FUNC_CONCAT}                            { return symbol(ParserSym.FUNC_CONCAT, "FUNC_CONCAT"); }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
}


/* error fallback */
[^]                              { throw new UnknownCharacterException(yytext()); }
