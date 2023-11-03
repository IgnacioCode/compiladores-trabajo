package lyc.compiler;

import lyc.compiler.factories.LexerFactory;
import lyc.compiler.model.CompilerException;
import lyc.compiler.model.InvalidFloatException;
import lyc.compiler.model.InvalidIntegerException;
import lyc.compiler.model.InvalidLengthException;
import lyc.compiler.model.UnknownCharacterException;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static lyc.compiler.constants.Constants.STRING_MAX_LENGTH;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LexerTest {

  private Lexer lexer;

  @Test
  public void comment() throws Exception {
    scan("#/This is a comment\\#");
    assertThat(nextToken()).isEqualTo(ParserSym.EOF);
  }

  @Test
  public void invalidStringConstantLength() {
    assertThrows(InvalidLengthException.class, () -> {
      scan("\"%s\"".formatted(getRandomString()));
      nextToken();
    });
  }

  @Test
  public void invalidIdLength() {
    assertThrows(InvalidLengthException.class, () -> {
      scan(getRandomString());
      nextToken();
    });
  }

  @Test
  public void invalidPositiveIntegerConstantValue() {
    assertThrows(InvalidIntegerException.class, () -> {
      scan("%d".formatted(9223372036854775807L));
      nextToken();
    });
  }

  @Test
  public void invalidNegativeIntegerConstantValue() {
    assertThrows(InvalidIntegerException.class, () -> {
      scan("%d".formatted(-9223372036854775807L));
      nextToken(); // OP_MENOS
      nextToken(); // Integer
    });
  }

  @Test
  public void invalidPositiveFloatConstantValue() {
    assertThrows(InvalidFloatException.class, () -> {
      scan("%f".formatted(37897897987897897989879879799223372036854775807999.789789798789789798987987979789789798789789798987987979));
      nextToken();
    });
  }

  @Test
  public void assignmentWithExpressions() throws Exception {
    scan("c=d*(e-21)/4");
    assertThat(nextToken()).isEqualTo(ParserSym.ID);
    assertThat(nextToken()).isEqualTo(ParserSym.OP_ASIG);
    assertThat(nextToken()).isEqualTo(ParserSym.ID);
    assertThat(nextToken()).isEqualTo(ParserSym.OP_MULT);
    assertThat(nextToken()).isEqualTo(ParserSym.ABRE_PAR);
    assertThat(nextToken()).isEqualTo(ParserSym.ID);
    assertThat(nextToken()).isEqualTo(ParserSym.OP_MENOS);
    assertThat(nextToken()).isEqualTo(ParserSym.CTE_INT);
    assertThat(nextToken()).isEqualTo(ParserSym.CIERRA_PAR);
    assertThat(nextToken()).isEqualTo(ParserSym.OP_DIV);
    assertThat(nextToken()).isEqualTo(ParserSym.CTE_INT);
    assertThat(nextToken()).isEqualTo(ParserSym.EOF);
  }

  @Test
  public void unknownCharacter() {
    assertThrows(UnknownCharacterException.class, () -> {
      scan("#");
      nextToken();
    });
  }

  @AfterEach
  public void resetLexer() {
    lexer = null;
  }

  private void scan(String input) {
    lexer = LexerFactory.create(input);
  }

  private int nextToken() throws IOException, CompilerException {
    return lexer.next_token().sym;
  }

  private static String getRandomString() {
    return new RandomStringGenerator.Builder()
        .filteredBy(CharacterPredicates.LETTERS)
        .withinRange('a', 'z')
        .build().generate(STRING_MAX_LENGTH * 2);
  }

}
