package lyc.compiler.files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SymbolTableGenerator implements FileGenerator{
	
	//private static final String OUTPUT_DIRECTORY  = "target/output/symbol-table.txt";
	//static File outputFile;

	public static String[][] symbolTable = new String[4][256];
	static int cant_variables = 0;
	static int F_NAME = 0;
	static int F_DATATYPE = 1;
	static int F_VALUE = 2;
	static int F_LENGHT = 3;
	
    @Override
    public void generate(FileWriter fileWriter) throws IOException {
		// Esto se ejecuta después del parseo
		int pad = maxWidth() + 5;
    	
		fileWriter.write(
			centerText("NOMBRE", pad) + "|" +
			centerText("TIPO", pad) + "|" +
			centerText("VALOR", pad) + "|" +
			centerText("LONGITUD", pad) + "\n"
		);

		fileWriter.write("-".repeat(pad * 4) + "\n");
		
		for (int i = 0; i < cant_variables; i++ ) {
			fileWriter.write(
				centerText(symbolTable[0][i], pad) + "|" +
				centerText(symbolTable[1][i], pad) + "|" +
				centerText(symbolTable[2][i], pad) + "|" +
				centerText(symbolTable[3][i], pad) + "\n"
			);
		}
    }
    
    public static void addVariable(String name) {
    	
    	if(variableExist(name))
    		return;

		symbolTable[F_NAME][cant_variables] = name;

		cant_variables++;
    }

	public static void addVariablesType(ArrayList<String> id_list, String type) {
		for (String id: id_list) {
			int pos = findVariable(id);
			if (pos >= 0) {
				symbolTable[F_DATATYPE][pos] = type;
			}
		}
		id_list.clear();
	}
    
	public static void addConstant(String value) {
    	
		if(variableExist("_" + value))
    		return;

		symbolTable[F_NAME][cant_variables] = "_" + value;
		symbolTable[F_VALUE][cant_variables] = value;

		cant_variables++;
    }

	public static void addStringLiteral(String value) {
    	
		if(variableExist("_" + value))
    		return;

		symbolTable[F_NAME][cant_variables] = "_" + value;
		symbolTable[F_VALUE][cant_variables] = value;
		symbolTable[F_LENGHT][cant_variables] = String.valueOf(value.length());

		cant_variables++;
    }
	
	private static boolean variableExist(String name) {
		for (int i = 0; i < cant_variables; i++) {
	        if (symbolTable[F_NAME][i].equals(name)) {
	            // Se encontró una coincidencia, devolver true
	            return true;
	        }
	    }
		return false;
	}

	private static int findVariable(String name) {
		for (int i = 0; i < cant_variables; i++) {
	        if (symbolTable[F_NAME][i].equals(name)) {
	            return i;
	        }
	    }
		return -1;
	}
	
	private static String centerText(String text, int columnWidth) {
		if (text != null) {
			int textLength = text.length();
			
			if (textLength >= columnWidth) {
				return text.substring(0, columnWidth); // Truncate if too long
			} else {
				int spacesBefore = (columnWidth - textLength) / 2;
				int spacesAfter = columnWidth - textLength - spacesBefore;
				return " ".repeat(spacesBefore) + text + " ".repeat(spacesAfter);
			}
		}
		return " ".repeat(columnWidth);
	}

	private static int maxWidth() {
		int longest_var = 10;
		int curent_long = 0;
		
		for (int i = 0; i < cant_variables; i++ ) {
			try {
				curent_long = Integer.parseInt(symbolTable[F_LENGHT][i]);
				if (curent_long > longest_var) {
					longest_var = curent_long;
				}
			 } catch (NumberFormatException nfe) {}
		}

		return longest_var;
	}
}
