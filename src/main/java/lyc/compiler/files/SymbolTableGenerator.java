package lyc.compiler.files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SymbolTableGenerator implements FileGenerator{
	
	//private static final String OUTPUT_DIRECTORY  = "compiladores-trabajo\\target\\output\\symbol-table.txt";
	//NO PUEDO HACER ANDAR UNA RUTA RELATIVA LA RE PUTA MADRE QUE ME RE MIL PARIO
	private static final String TEST_DIR  = "SALIDA_PRUEBA_ST.txt";
	
	static File outputFile;
	public static String[][] symbolTable = new String[4][256];
	static int cant_variables = 0;
	static int F_NAME = 0;
	static int F_DATATYPE = 1;
	static int F_VALUE = 2;
	static int F_LENGHT = 3;
	
    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        
    	fileWriter.write("  NOMBRE  |   TIPO   |   VALOR  |  LONGITUD");
    }
    
    public static void addVariable(String name) {
    	
    	if(variableExist(name))
    		return;
    	
    	File st = new File(TEST_DIR);
		try {
			
			FileWriter w = new FileWriter(st,true);
			symbolTable[F_NAME][cant_variables] = name;
	    	w.write(centerText(name,10)+"|"+ centerText("",10)+"|"+ centerText("",10)+"|"+centerText("",10)+"\n");
	    	
	    	w.close();
	    	cant_variables++;
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	
    }
    
    
	public static void addConstant(String name, String value){
    	
		if(variableExist(name))
    		return;
		
    	File st = new File(TEST_DIR);
		try {
			FileWriter w = new FileWriter(st,true);
			symbolTable[F_NAME][cant_variables] = name;
	    	symbolTable[F_VALUE][cant_variables] = value;
	    	w.write(centerText(name,10)+"|"+ centerText("",10)+"|"+centerText(value,10)+"|"+centerText("",10)+"\n");
	    	w.close();
	    	
	    	cant_variables++;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
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
	    int textLength = text.length();
	    if (textLength >= columnWidth) {
	        return text.substring(0, columnWidth); // Truncate if too long
	    } else {
	        int spacesBefore = (columnWidth - textLength) / 2;
	        int spacesAfter = columnWidth - textLength - spacesBefore;
	        return " ".repeat(spacesBefore) + text + " ".repeat(spacesAfter);
	    }
	}

    
    
}
