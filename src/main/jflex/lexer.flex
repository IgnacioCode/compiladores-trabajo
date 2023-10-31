package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;
import lyc.compiler.files.SymbolHashTableGenerator;

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

  private Symbol symbol(int type, Object value) {
    // Agregamos las constantes a la tabla de simbolos.
    switch (type) {
      case ParserSym.CTE_INT:
        SymbolHashTableGenerator.addConstant(value.toString(), SymbolHashTableGenerator.VariableTypes.INT);
        break;

      case ParserSym.CTE_FLOAT:
        SymbolHashTableGenerator.addConstant(value.toString(), SymbolHashTableGenerator.VariableTypes.FLOAT);
        break;

      case ParserSym.CTE_STRING:
        SymbolHashTableGenerator.addConstant(value.toString(), SymbolHashTableGenerator.VariableTypes.STRING);
        break;
    }

    return new Symbol(type, yyline, yycolumn, value);
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Identation =  [ \t\f]

WhiteSpace = {LineTerminator} | {Identation}

MultilineComment = "#/" [^\\] ~"\#" | "#/" "\\"+ "#"
EndOfLineComment = "//" {InputCharacter}* {LineTerminator}?
Comment = {MultilineComment} | {EndOfLineComment}

Letra = [a-zA-Z_]
Digito = [0-9]

ID = {Letra}({Letra}|{Digito})*
String = \"(.[^\"]*)\"
Integer = 0 | [1-9][0-9]*
Float =  \.{Digito}+ | {Digito}+\.{Digito}*

%%

<YYINITIAL> {
  /* operators */
  "+"                           { return symbol(ParserSym.OP_MAS);   }
  "-"                           { return symbol(ParserSym.OP_MENOS); }
  "*"                           { return symbol(ParserSym.OP_MULT);  }
  "/"                           { return symbol(ParserSym.OP_DIV);   }
  "="                           { return symbol(ParserSym.OP_ASIG);  }

  ","                           { return symbol(ParserSym.COMA);       }
  ":"                           { return symbol(ParserSym.DOS_PUNTOS); }
  ";"                           { return symbol(ParserSym.PUNTO_COMA); }

  "=="                          { return symbol(ParserSym.COMP_EQ);       }
  "!="                          { return symbol(ParserSym.COMP_DIST);     }
  ">"                           { return symbol(ParserSym.COMP_MAYOR);    }
  "<"                           { return symbol(ParserSym.COMP_MENOR);    }
  ">="                          { return symbol(ParserSym.COMP_MAYOR_EQ); }
  "<="                          { return symbol(ParserSym.COMP_MENOR_EQ); }

  "("                           { return symbol(ParserSym.ABRE_PAR);     }
  ")"                           { return symbol(ParserSym.CIERRA_PAR);   }
  "{"                           { return symbol(ParserSym.ABRE_LLAVE);   }
  "}"                           { return symbol(ParserSym.CIERRA_LLAVE); }
  "["                           { return symbol(ParserSym.ABRE_LISTA);   }
  "]"                           { return symbol(ParserSym.CIERRA_LISTA); }

  "and"                         { return symbol(ParserSym.OP_AND);    }
  "or"                          { return symbol(ParserSym.OP_OR);     }
  "not"                         { return symbol(ParserSym.OP_NOT);    }
  "if"                          { return symbol(ParserSym.COND_IF);   }
  "else"                        { return symbol(ParserSym.COND_ELSE); }
  "while"                       { return symbol(ParserSym.CICLO);     }
  "vars"                        { return symbol(ParserSym.INIC_VARS); }

  "int"|"float"|"string"        { return symbol(ParserSym.TIPO, SymbolHashTableGenerator.castType(yytext())); }

  "read"                        { return symbol(ParserSym.LEER_TECLADO);     }
  "write"                       { return symbol(ParserSym.MOSTRAR_PANTALLA); }
  "FirstIndexOf"                { return symbol(ParserSym.FUNC_INDICE);      }
  "ConcatenarConRecorte"        { return symbol(ParserSym.FUNC_CONCAT);      }

  {ID}                          {
                                  if (yylength() <= STRING_MAX_LENGTH)
                                    return symbol(ParserSym.ID, yytext());
                                  else
                                    throw new InvalidLengthException("Longitud del identificador supera el tamaño maximo.");
                                }
  {Integer}                     { 
                                  if (yylength() <= INT_MAX_LENGTH)
                                    return symbol(ParserSym.CTE_INT, yytext());
                                  else
                                    throw new InvalidIntegerException("Supera el valor maximo de un numero int.");
                                }
  {Float}                       { 
                                  if (yylength() <= FLOAT_MAX_LENGTH)
                                    return symbol(ParserSym.CTE_FLOAT, yytext());
                                  else
                                    throw new InvalidFloatException("Supera el valor maximo de un numero float.");
                                }
  {String}                      { 
                                  if (yylength() <= STRING_MAX_LENGTH)
                                    return symbol(ParserSym.CTE_STRING, yytext());
                                  else
                                    throw new InvalidLengthException("String literal supera el tamaño maximo.");
                                }

  {Comment} | {WhiteSpace}      { /* ignore */ }
}

/* error fallback */
[^]                             { throw new UnknownCharacterException(yytext()); }
