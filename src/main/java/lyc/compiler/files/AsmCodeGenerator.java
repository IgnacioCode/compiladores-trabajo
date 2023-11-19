package lyc.compiler.files;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lyc.compiler.files.IntermediateCodeGenerator.Cell;
import lyc.compiler.files.SymbolHashTableGenerator.Symbol;

public class AsmCodeGenerator implements FileGenerator {

    private static BufferedWriter asmBuffer;
    private static Set<Integer> labelsSet = new HashSet<Integer>();

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        asmBuffer = new BufferedWriter(fileWriter);
        start();
        data();
        loop();
        end();
        asmBuffer.flush();
    }

    private void start() throws IOException {
        asmBuffer.write(
                "include number.asm\ninclude macros2.asm\n\n" +
                        ".MODEL LARGE ;Modelo de Memoria\n" +
                        ".386 ;Tipo de Procesador\n" +
                        ".STACK 200h ;Bytes en el Stack\n\n");
    }

    private void data() throws IOException {
        asmBuffer.write(".DATA\n");

        for (Symbol sym : SymbolHashTableGenerator.symbolTable.values()) {
            asmBuffer.write(sym.name + "\t\t");
            switch (sym.type) {
                case INT:
                    if (sym.value != null) {
                        asmBuffer.write("dd\t\t" + sym.value + ".0\n");
                    } else {
                        asmBuffer.write("dd\t\t" + "?\n");
                    }
                    break;

                case FLOAT:
                    String value = (sym.value != null) ? sym.value : "?";
                    asmBuffer.write("dd\t\t" + value + '\n');
                    break;

                case STRING:
                    if (sym.value != null) {
                        asmBuffer.write("db\t\t" + sym.value.replace('\"', '\'') + ",'$'," + sym.length + " dup (?)\n");
                    } else {
                        asmBuffer.write("db\t\t" + "?,'$',16 dup (?)\n");
                    }
                    break;
            }
        }
        // No encuentro otra forma de pasar el registro BX a una variable DWORD, por lo
        // tanto esta es la unica variable hardcodeada como WORD.
        asmBuffer.write("@size\tdw\t?\n");
        asmBuffer.write("\n\n");
    }

    private void loop() throws IOException {
        Iterator<Cell> iter = IntermediateCodeGenerator.get().descendingIterator();
        Boolean assign = false;
        Boolean write = false;
        Boolean read = false;
        Boolean si_reg = false;

        asmBuffer.write(".CODE\nmain:\n\tmov AX, @DATA\n\tmov DS, AX\n\tmov ES, AX\n\n");

        while (iter.hasNext()) {
            Cell current = iter.next();

            // Si la celda es un punto de salto, creamos la etiqueta.
            if (labelsSet.remove(current.id)) {
                asmBuffer.write("ETQ_" + current.id + ":\n");
            }

            // Si la celda es una variable, la cargamos en el stack.
            Symbol sym = SymbolHashTableGenerator.getVariable(current.value);
            if (sym != null) {
                if (assign) {
                    switch (sym.type) {
                        case INT -> asmBuffer.write("\tFSTP " + sym.name + '\n');
                        case FLOAT -> asmBuffer.write("\tFSTP " + sym.name + '\n');
                        case STRING -> {
                            if (si_reg) {
                                asmBuffer.write("\tmov DI, offset " + sym.name + '\n');
                                asmBuffer.write("\tmov CX, 0\n");
                                si_reg = false;
                            } else {
                                asmBuffer.write("\tmov SI, offset " + sym.name + '\n');
                                si_reg = true;
                            }
                            asmBuffer.write("\tSTRCPY 0\n");
                        }
                    }

                    assign = false;
                } else if (write) {
                    switch (sym.type) {
                        case INT -> asmBuffer.write("\tDisplayFloat " + sym.name + ", 2\n");
                        case FLOAT -> asmBuffer.write("\tDisplayFloat " + sym.name + ", 2\n");
                        case STRING -> asmBuffer.write("\tDisplayString " + sym.name + '\n');
                    }
                    write = false;
                } else if (read) {
                    switch (sym.type) {
                        case INT -> asmBuffer.write("\tGetFloat " + sym.name + '\n');
                        case FLOAT -> asmBuffer.write("\tGetFloat " + sym.name + '\n');
                        case STRING -> asmBuffer.write("\tGetString " + sym.name + '\n');
                    }
                    read = false;
                } else {
                    switch (sym.type) {
                        case INT -> asmBuffer.write("\tFLD " + sym.name + '\n');
                        case FLOAT -> asmBuffer.write("\tFLD " + sym.name + '\n');
                        case STRING -> {
                            if (si_reg) {
                                asmBuffer.write("\tmov DI, offset " + sym.name + '\n');
                                asmBuffer.write("\tmov CX, 0\n");
                                si_reg = false;
                            } else {
                                asmBuffer.write("\tmov SI, offset " + sym.name + '\n');
                                si_reg = true;
                            }
                        }
                    }
                }
            } else {
                // Probamos si la celda es una etiqueta.
                try {
                    labelsSet.add(Integer.parseInt(current.value));
                    asmBuffer.write("ETQ_" + current.value + '\n');
                } catch (NumberFormatException e) {
                    // Si es una etiqueta de iteracion.
                    if (current.value.contains("LOOP") || current.value.contains("FIND_IDX")) {
                        if (current.value.startsWith(".")) {
                            asmBuffer.write(current.value.replace(".", "") + ":\n");
                        } else {
                            asmBuffer.write(current.value + "\n");
                        }
                    } else {
                        switch (current.value) {
                            case "CMP" -> asmBuffer.write("\tFXCH\n\tFCOM\n\tFSTSW AX\n\tSAHF\n\tFFREE\n");
                            case "BLT" -> asmBuffer.write("\tJB ");
                            case "BLE" -> asmBuffer.write("\tJBE ");
                            case "BGT" -> asmBuffer.write("\tJA ");
                            case "BGE" -> asmBuffer.write("\tJAE ");
                            case "BEQ" -> asmBuffer.write("\tJE ");
                            case "BNE" -> asmBuffer.write("\tJNE ");
                            case "BI" -> asmBuffer.write("\tJMP ");
                            case "+" -> asmBuffer.write("\tFADD\n");
                            case "-" -> asmBuffer.write("\tFSUB\n");
                            case "*" -> asmBuffer.write("\tFMUL\n");
                            case "/" -> asmBuffer.write("\tFDIV\n");
                            case "=" -> assign = true;
                            case "load-array" -> {
                                current = iter.next(); // array
                                asmBuffer.write("\tmov SI, offset " + current.value + '\n');
                            }
                            case "offset" ->
                                asmBuffer.write(
                                        "\tmov AX, [SI]\n\tmov WORD PTR [@elem], AX\n\tadd SI, 4\n");
                            case "newline" -> asmBuffer.write("\tnewLine 1\n");
                            case "write" -> write = true;
                            case "read" -> read = true;
                            case "strlen" -> {
                                current = iter.next(); // string id
                                asmBuffer.write("\tmov SI, offset " + current.value + '\n');
                                asmBuffer.write("\tSTRLEN\n");
                                asmBuffer.write("\tmov @size, BX\n\tFILD @size\n");
                            }
                            case "strcut" -> {
                                asmBuffer.write("\tSTRCUT ");

                                current = iter.next();
                                asmBuffer.write(current.value + ',');
                                current = iter.next();
                                asmBuffer.write(current.value + ',');
                                current = iter.next();
                                asmBuffer.write(current.value + ',');
                                current = iter.next();
                                asmBuffer.write(current.value + '\n');
                                asmBuffer.write("\tmov SI, offset @tmp_str\n");
                                si_reg = true;
                            }
                        }
                    }
                }
            }
        }

        for (Integer id : labelsSet) {
            asmBuffer.write("ETQ_" + id + ":\n");
        }
    }

    private void end() throws IOException {
        asmBuffer.write("\nend_main:\n\tMOV EAX, 4C00h\n\tINT 21h\n\nEND main");
    }
}
