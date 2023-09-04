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
TD_INT = "int"
TD_FLOW = "flow"
TD_STRING = "string"
INIC_VARS = "vars"
COMA = ","
DOS_PUNTOS = ":"
PUNTO_COMA = ";"
ABRE_COM = "#/"
CIERRA_COM = "\#"
LEER_TECLADO = "read"
MOSTRAR_PANTALLA = "write"
CICLO = "while"
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
  {OP_MAS}                                    { return symbol(ParserSym.PLUS, "OP_MAS"); }
  {OP_MENOS}                                     { return symbol(ParserSym.SUB, "OP_MENOS"); }
  {OP_MULT}                                    { return symbol(ParserSym.MULT, "OP_MULT"); }
  {OP_DIV}                                     { return symbol(ParserSym.DIV, "OP_DIV"); }
  {OP_ASIG}                                   { return symbol(ParserSym.ASSIG, "OP_ASIG"); }
  {ABRE_PAR}                             { return symbol(ParserSym.OPEN_BRACKET, "ABRE_PAR"); }
  {CIERRA_PAR}                            { return symbol(ParserSym.CLOSE_BRACKET, "CIERRA_PAR"); }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
}


/* error fallback */
[^]                              { throw new UnknownCharacterException(yytext()); }
