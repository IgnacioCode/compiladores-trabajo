package lyc.compiler.files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SymbolTableGenerator implements FileGenerator{
	
	private static final String OUTPUT_DIRECTORY  = "target/output/symbol-table.txt";
	
	static File outputFile;
	public static String[][] symbolTable = new String[4][256];
	static int cant_variables = 0;
	static int F_NAME = 0;
	static int F_DATATYPE = 1;
	static int F_VALUE = 2;
	static int F_LENGHT = 3;
	
    @Override
    public void generate(FileWriter fileWriter) throws IOException {
		// Esto se ejecuta después del parseo
        
    	fileWriter.write("  NOMBRE  |   TIPO   |   VALOR  |  LONGITUD\n");

		for (int i = 0; i < cant_variables; i++ ) {
			fileWriter.write(
					centerText(symbolTable[0][i], 10) + "|" +
						centerText(symbolTable[1][i], 10) + "|" +
						centerText(symbolTable[2][i], 10) + "|" +
						centerText(symbolTable[3][i], 10) + "\n"
			);
		}
    }
    
    public static void addVariable(String name) {
    	
    	if(variableExist(name))
    		return;

		symbolTable[F_NAME][cant_variables] = name;

		cant_variables++;
    }
    
    
	public static void addConstant(String name, String value){
    	
		if(variableExist(name))
    		return;

		symbolTable[F_NAME][cant_variables] = name;
		symbolTable[F_VALUE][cant_variables] = value;

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
	
	public static String centerText(String text, int columnWidth) {
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
		return "          ";
	}
}
