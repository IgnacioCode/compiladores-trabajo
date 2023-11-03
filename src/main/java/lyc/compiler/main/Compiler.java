package lyc.compiler.main;

import lyc.compiler.Parser;
import lyc.compiler.factories.FileFactory;
import lyc.compiler.factories.ParserFactory;
import lyc.compiler.files.AsmCodeGenerator;
import lyc.compiler.files.FileOutputWriter;
import lyc.compiler.files.IntermediateCodeGenerator;
import lyc.compiler.files.SymbolHashTableGenerator;

import java.io.IOException;
import java.io.Reader;

import java_cup.runtime.Symbol;

public final class Compiler {

    private Compiler() {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Filename must be provided as argument.");
            System.exit(1);
        }

        try (Reader reader = FileFactory.create(args[0])) {
            Parser parser = ParserFactory.create(reader);
            Symbol sym = parser.parse();
            if ((int) sym.value > 0) {
                throw new Exception(sym.value + " error(s) detected.");
            }

            FileOutputWriter.writeOutput("symbol-table.txt", new SymbolHashTableGenerator());
            FileOutputWriter.writeOutput("intermediate-code.txt", new IntermediateCodeGenerator());
            FileOutputWriter.writeOutput("final.asm", new AsmCodeGenerator());
        } catch (IOException e) {
            System.err.println("There was an error trying to read input file " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Compilation error: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("Compilation Successful");
    }
}
