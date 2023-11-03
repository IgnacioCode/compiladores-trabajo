package lyc.compiler.internals;

import lyc.compiler.files.IntermediateCodeGenerator;
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
        for (Symbol sym : arguments) {
            IntermediateCodeGenerator.insert("write", sym.name);
        }
        IntermediateCodeGenerator.insert("newline");
        clear();
    }

    public static void read() {
        IntermediateCodeGenerator.insert("read", arguments.get(0).name);
        clear();
    }

    public static void findIndex() {
        // Inicializamos @idx en 1, cargamos la lista y ponemos la etiqueta de la
        // funcion (con un numero para evitar pisar llamados previos).
        IntermediateCodeGenerator.insert("_1", "=", "@idx", "offset", "@list",
                ".FIND_IDX_" + IntermediateCodeGenerator.getCurrentID());
        // La id del loop por la cual vamos a iterar hasta encontrar el indice.
        String loop_id = String.valueOf(IntermediateCodeGenerator.getCurrentID());
        // Verificamos si no terminamos la array.
        IntermediateCodeGenerator.insert("@idx", "@cant", "CMP", "BGE");
        IntermediateCodeGenerator.moveAndStack();
        // Comparamos el pivot con el elemento de la lista actual.
        IntermediateCodeGenerator.insert(arguments.get(0).name, "[@list]", "CMP", "BEQ");
        IntermediateCodeGenerator.moveAndStack();
        // Incrementamos el contador y marcamos el salto al loop
        IntermediateCodeGenerator.insert("@idx", "_1", "+", "=", "@idx", "BI", loop_id);
        // Marcamos el salto hasta el final del loop, si es que se encontro el elemento.
        IntermediateCodeGenerator.updateStacked(1);
        // Marcamos para guardar el indice, la asignacion se genera en la regla
        // correspondiente.
        IntermediateCodeGenerator.insert("@idx");
        // Si no se encontro saltamos 3 celdas para evitar la asignacion.
        IntermediateCodeGenerator.updateStacked(3);

        clear(); // Limpiamos la lista de argumentos

        // @list: Array con todos los elementos.
        // @cant: Tamaño de la array.
        // @idx: Contador.
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
        IntermediateCodeGenerator.insert("strlen", arg1, "=", "@size", arg3, "@size", "CMP",
                "BGT");
        IntermediateCodeGenerator.moveAndStack();
        // Calculamos el largo de arg2, lo guardamos en @size y comparamos con la
        // posicion de recorte.
        IntermediateCodeGenerator.insert("strlen", arg2, "=", "@size", arg3, "@size", "CMP",
                "BGT");
        IntermediateCodeGenerator.moveAndStack();
        // Cortamos arg1 y arg2 hasta la posicion de recorte, los volvemos a guardar y
        // concatenamos.
        IntermediateCodeGenerator.insert("strcut", arg1, arg3, "=", arg1, "strcut", arg2, arg3, "=", arg2, "strcat",
                arg1, arg2, "BI");
        // Si la posicion no esta en un rango valido entonces saltamos al mensaje de
        // error.
        IntermediateCodeGenerator.updateStacked(2);
        IntermediateCodeGenerator.updateStacked(2);
        IntermediateCodeGenerator.moveAndStack();
        IntermediateCodeGenerator.insert("write", "@error_msg", "BI");
        // y volvemos a saltar para evitar la asignacion, saltamos 4 celdas por: Celda
        // actual + 3 de asignacion.
        String jump_id = String.valueOf(IntermediateCodeGenerator.getCurrentID() + 4);
        IntermediateCodeGenerator.insert(jump_id);
        // Pero si la posicion si esta en el rango valido, saltamos hasta la asignacion.
        IntermediateCodeGenerator.updateStacked(1);

        clear(); // Limpiamos la lista de argumentos

        // @size: Variable en donde se guarda el tamaño de la cadena con strlen.
        // strlen str: Funcion que devuelve el tamaño de una cadena.
        // strcut str, offset: Corta una cadena hasta la posicion X.
        // strcat str, str: Concatena dos cadenas.
    }

}
