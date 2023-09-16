package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.ParserSym;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;
import lyc.compiler.files.SymbolTableGenerator;

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
  private Symbol symbol(int type) {
      return new Symbol(type, yyline, yycolumn);
  }

  // Comentado para que el parser reconozca el valor de los simbolos
  /*private Symbol symbol(int type, String texto) {
    //System.out.println("Token: " + texto);

    if(type == ParserSym.ID) {
      SymbolTableGenerator.addVariable(texto);
    } else if(type == ParserSym.CTE) {
      SymbolTableGenerator.addConstant(texto);
    } else if(type == ParserSym.STRING_LITERAL) {
      SymbolTableGenerator.addStringLiteral(texto);
    }

    return new Symbol(type, yyline, yycolumn);
  }*/

  private Symbol symbol(int type, Object value) {
    if(type == ParserSym.ID) {
      SymbolTableGenerator.addVariable(value.toString());
    } else if(type == ParserSym.CTE) {
      SymbolTableGenerator.addConstant(value.toString());
    } else if(type == ParserSym.STRING_LITERAL) {
      SymbolTableGenerator.addStringLiteral(value.toString());
    }

    return new Symbol(type, yyline, yycolumn, value);
  }
%}


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
ABRE_LISTA = "["
CIERRA_LISTA = "]"

COND_IF = "if"
COND_ELSE = "else"
CICLO = "while"
TD_INT = "int"
TD_FLOAT = "float"
TD_STRING = "string"
INIC_VARS = "vars"
LEER_TECLADO = "read"
MOSTRAR_PANTALLA = "write"
FUNC_INDICE = "FirstIndexOf"
FUNC_CONCAT = "ConcatenarConRecorte"

COMA = ","
DOS_PUNTOS = ":"
PUNTO_COMA = ";"

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Identation =  [ \t\f]

MultilineComment = "#/" [^\\] ~"\#" | "#/" "\\"+ "#"
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}?
Comment = {MultilineComment} | {EndOfLineComment}

Letra = [a-zA-Z]
Digito = [0-9]

WhiteSpace = {LineTerminator} | {Identation}
ID = {Letra}({Letra}|{Digito})*
StringLiteral = \"(.[^\"]*)\"
Integer = [1-9][0-9]*
Float = (\.)?{Integer}(\.[0-9]*)?


%%


<YYINITIAL> {
  /* operators */
  {OP_MAS}                                 { return symbol(ParserSym.OP_MAS, "OP_MAS"); }
  {OP_MENOS}                               { return symbol(ParserSym.OP_MENOS, "OP_MENOS"); }
  {OP_MULT}                                { return symbol(ParserSym.OP_MULT, "OP_MULT"); }
  {OP_DIV}                                 { return symbol(ParserSym.OP_DIV, "OP_DIV"); }
  {OP_ASIG}                                { return symbol(ParserSym.OP_ASIG, "OP_ASIG"); }
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
  {ABRE_LISTA}                             { return symbol(ParserSym.ABRE_LISTA, "ABRE_LISTA"); }
  {CIERRA_LISTA}                           { return symbol(ParserSym.CIERRA_LISTA, "ABRE_LISTA"); }

  {COND_IF}                                { return symbol(ParserSym.COND_IF, "COND_IF"); }
  {COND_ELSE}                              { return symbol(ParserSym.COND_ELSE, "COND_ELSE"); }
  {CICLO}                                  { return symbol(ParserSym.CICLO, "CICLO"); }

  {TD_INT}                                 { return symbol(ParserSym.TD_INT, "int"); }
  {TD_FLOAT}                               { return symbol(ParserSym.TD_FLOAT, "float"); }
  {TD_STRING}                              { return symbol(ParserSym.TD_STRING, "string"); }

  {INIC_VARS}                              { return symbol(ParserSym.INIC_VARS, "INIC_VARS"); }
  {COMA}                                   { return symbol(ParserSym.COMA, "COMA"); }
  {DOS_PUNTOS}                             { return symbol(ParserSym.DOS_PUNTOS, "DOS_PUNTOS"); }
  {PUNTO_COMA}                             { return symbol(ParserSym.PUNTO_COMA, "PUNTO_COMA"); }

  {LEER_TECLADO}                           { return symbol(ParserSym.LEER_TECLADO, "LEER_TECLADO"); }
  {MOSTRAR_PANTALLA}                       { return symbol(ParserSym.MOSTRAR_PANTALLA, "MOSTRAR_PANTALLA"); }
  {FUNC_INDICE}                            { return symbol(ParserSym.FUNC_INDICE, "FUNC_INDICE"); }
  {FUNC_CONCAT}                            { return symbol(ParserSym.FUNC_CONCAT, "FUNC_CONCAT"); }


  {ID}                                     { if (yylength() <= STRING_MAX_LENGTH) return symbol(ParserSym.ID, yytext()); 
                                             else throw new InvalidLengthException("Longitud del identificador supera el tamaño maximo."); }
  {Integer}                                { if (yylength() <= INT_MAX_LENGTH) return symbol(ParserSym.CTE, yytext()); 
                                             else throw new InvalidIntegerException("Supera el valor maximo de un numero int."); }
  {Float}                                  { if (yylength() <= FLOAT_MAX_LENGTH) return symbol(ParserSym.CTE, yytext()); 
                                             else throw new InvalidFloatException("Supera el valor maximo de un numero float."); }
  {StringLiteral}                          { if (yylength() <= STRING_MAX_LENGTH) return symbol(ParserSym.STRING_LITERAL, yytext());
                                             else throw new InvalidLengthException("String literal supera el tamaño maximo."); }


  {Comment}                      { /* ignore */ }
  {WhiteSpace}                   { /* ignore */ }
}

/* error fallback */
[^]                              { throw new UnknownCharacterException(yytext()); }
