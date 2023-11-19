package lyc.compiler.internals;

import java.util.ArrayList;
import java.util.List;

import lyc.compiler.files.IntermediateCodeGenerator;
import lyc.compiler.files.SymbolHashTableGenerator;
import lyc.compiler.files.IntermediateCodeGenerator.Cell;
import lyc.compiler.files.SymbolHashTableGenerator.Symbol;
import lyc.compiler.files.SymbolHashTableGenerator.VariableTypes;

public class Functions extends Arguments {

    public static boolean indexValidate() {
        // Validamos que los tipos de datos sean iguales.
        VariableTypes type = arguments.get(0).type;
        for (Symbol sym : argumentsList) {
            if (type != sym.type) {
                return false;
            }
        }
        // Validamos que haya una lista con uno o mas elementos y que haya un solo
        // argumento antes.
        return arguments.size() == 1 && argumentsList.size() > 0;
    }

    public static boolean concatValidate() {
        // Validamos que solo tenga tres argumento y sean de los tipos correspondientes.
        return arguments.size() == 3 && arguments.get(0).type == VariableTypes.STRING
                && arguments.get(1).type == VariableTypes.STRING && arguments.get(2).type == VariableTypes.INT;
    }

    public static boolean readValidate() {
        // Validamos que solo tenga un argumento y no sea una constante (no empiece con
        // '_').
        return arguments.size() == 1 && !arguments.get(0).name.startsWith("_");
    }

    public static void write() {
        List<Cell> toWrite = new ArrayList<Cell>();

        for (Symbol sym : arguments) {
            Cell cell = remove(sym);
            toWrite.add(0, cell);
        }

        for (Cell cell : toWrite) {
            IntermediateCodeGenerator.insert("write", cell.value);
        }

        IntermediateCodeGenerator.insert("newline");
        clear();
    }

    public static void read() {
        Cell cell = remove(arguments.get(0));
        IntermediateCodeGenerator.insert("read", cell.value);
        clear();
    }

    public static void findIndex() {
        // @idx: Contador.
        SymbolHashTableGenerator.add(new Symbol("@idx", VariableTypes.INT));
        String arg1 = arguments.get(0).name;

        // Inicializamos @idx en 1, cargamos la lista.
        IntermediateCodeGenerator.insert("_0", "=", "@idx");
        // Verificamos si no terminamos la array.
        for (Symbol sym : argumentsList) {
            IntermediateCodeGenerator.insert("@idx", "_1", "+", "=", "@idx", arg1, sym.name, "CMP", "BEQ");
            IntermediateCodeGenerator.moveAndStack();
        }

        // Incrementamos el contador y marcamos el salto al loop
        String jump_id = String.valueOf(IntermediateCodeGenerator.getCurrentID() + 6);
        IntermediateCodeGenerator.insert("BI", jump_id);

        for (int i = 0; i < argumentsList.size(); i++) {
            // Marcamos el salto hasta el final del loop, si es que se encontro el elemento.
            IntermediateCodeGenerator.updateStacked(1);
        }

        IntermediateCodeGenerator.insert("@idx");

        clear(); // Limpiamos la lista de argumentos
    }

    public static void concatCut() {
        // Primera string
        String arg1 = arguments.get(0).name;
        // Segunda string
        String arg2 = arguments.get(1).name;
        // Posicion de recorte
        String arg3 = arguments.get(2).name;

        // Calculamos el largo de arg1, lo guardamos en @size y comparamos con la
        // posicion de recorte.
        IntermediateCodeGenerator.insert(arg3, "strlen", arg1, "@size", "CMP", "BGT");
        IntermediateCodeGenerator.moveAndStack();
        // Calculamos el largo de arg2, lo guardamos en @size y comparamos con la
        // posicion de recorte.
        IntermediateCodeGenerator.insert(arg3, "strlen", arg2, "@size", "CMP", "BGT");
        IntermediateCodeGenerator.moveAndStack();
        // Cortamos arg1 y arg2 hasta la posicion de recorte, los volvemos a guardar y
        // concatenamos.
        IntermediateCodeGenerator.insert("strcut", arg1, arg2, "@tmp_str", arg3, "BI");
        // Si la posicion no esta en un rango valido entonces saltamos al mensaje de
        // error.
        IntermediateCodeGenerator.updateStacked(2);
        IntermediateCodeGenerator.updateStacked(2);
        IntermediateCodeGenerator.moveAndStack();
        IntermediateCodeGenerator.insert("write", "@error_msg", "newline", "BI");
        // y volvemos a saltar para evitar la asignacion, saltamos 4 celdas por: Celda
        // actual + 3 de asignacion.
        String jump_id = String.valueOf(IntermediateCodeGenerator.getCurrentID() + 4);
        IntermediateCodeGenerator.insert(jump_id);
        // Pero si la posicion si esta en el rango valido, saltamos hasta la asignacion.
        IntermediateCodeGenerator.updateStacked(1);

        clear(); // Limpiamos la lista de argumentos

        // strlen str: Funcion que devuelve el tamaño de una cadena.
        // strcut str, offset: Corta una cadena hasta la posicion X.
        // strcat str, str: Concatena dos cadenas.

        SymbolHashTableGenerator.add(new Symbol("@tmp_str", VariableTypes.STRING));
        Symbol sym = new Symbol("@error_msg", VariableTypes.STRING,
                "\"[Error] - Posicion de recorte fuera del rango valido para ambas cadenas.\"", 72);
        SymbolHashTableGenerator.add(sym);
    }

}
