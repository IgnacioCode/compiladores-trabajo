package lyc.compiler.internals;

import java.util.ArrayList;
import java.util.List;

import lyc.compiler.files.IntermediateCodeGenerator;
import lyc.compiler.files.SymbolHashTableGenerator;
import lyc.compiler.files.IntermediateCodeGenerator.Cell;
import lyc.compiler.files.SymbolHashTableGenerator.Symbol;

public class Arguments {

    protected static List<Symbol> argumentsList = new ArrayList<Symbol>();
    protected static List<Symbol> arguments = new ArrayList<Symbol>();
    protected static int totalArgs = 1;

    public static void add(Symbol arg) {
        Symbol sym = new Symbol("@args" + totalArgs++, arg.type);
        IntermediateCodeGenerator.insert("=", sym.name);
        arguments.add(sym);
        SymbolHashTableGenerator.addVariable(sym.name, arg.type);
    }

    public static Cell remove(Symbol arg) {
        totalArgs--;
        SymbolHashTableGenerator.remove(arg.name);
        IntermediateCodeGenerator.unstack(); // @argX
        IntermediateCodeGenerator.unstack(); // =
        return IntermediateCodeGenerator.unstack(); // id
    }

    public static void addList(Symbol arg) {
        argumentsList.add(arg);
        IntermediateCodeGenerator.unstack();
    }

    public static String get(int offset) {
        return arguments.get(offset).name;
    }

    public static void clear() {
        arguments = new ArrayList<Symbol>();
        if (!argumentsList.isEmpty()) {
            argumentsList = new ArrayList<Symbol>();
        }
    }

    protected static String listToString() {
        StringBuilder str = new StringBuilder();

        for (Symbol sym : argumentsList) {
            str.append(sym.name + ",");
        }
        str.setLength(str.length() - 1);
        return str.toString();
    }

}
