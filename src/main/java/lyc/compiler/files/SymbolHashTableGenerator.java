package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import lyc.compiler.errors.ErrorHandling;
import lyc.compiler.model.InvalidTypeException;
import lyc.compiler.model.VariablePreviouslyDefined;

public class SymbolHashTableGenerator implements FileGenerator {

	public static Hashtable<String, Symbol> symbolTable = new Hashtable<String, Symbol>();

	public static enum VariableTypes {
		INT, FLOAT, STRING
	}

	public static class Symbol {
		public int length;
		public String name;
		public String value;
		public VariableTypes type;

		public Symbol(String name, VariableTypes type) {
			this.name = name;
			this.type = type;
		}

		Symbol(String name, VariableTypes type, String value) {
			this.name = name;
			this.type = type;
			this.value = value;
		}

		Symbol(String name, VariableTypes type, String value, int length) {
			this.name = name;
			this.type = type;
			this.value = value;
			this.length = length;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (!(o instanceof Symbol)) {
				return false;
			}

			Symbol s = (Symbol) o;
			return this.name.equals(s.name);
		}

		public String toString(int pad) {
			String lengthStr = (this.length != 0) ? String.valueOf(this.length) : "";
			return centerText(this.name, pad) + "|" +
					centerText(this.type.name(), pad) + "|" +
					centerText(this.value, pad) + "|" +
					centerText(lengthStr, pad) + "\n";
		}

		public static String header(int pad) {
			return centerText("NOMBRE", pad) + "|" +
					centerText("TIPO", pad) + "|" +
					centerText("VALOR", pad) + "|" +
					centerText("LONGITUD", pad) + "\n";
		}

		private static String centerText(String text, int columnWidth) {
			if (text != null) {
				int textLength = text.length();
				if (textLength >= columnWidth) {
					return text.substring(0, columnWidth);
				} else {
					int spacesBefore = (columnWidth - textLength) / 2;
					int spacesAfter = columnWidth - textLength - spacesBefore;
					return " ".repeat(spacesBefore) + text + " ".repeat(spacesAfter);
				}
			}
			return " ".repeat(columnWidth);
		}
	}

	@Override
	public void generate(FileWriter fileWriter) throws IOException {
		int pad = maxWidth() + 5;

		fileWriter.write(Symbol.header(pad));
		fileWriter.write("-".repeat(pad * 4) + "\n");
		for (Symbol sym : symbolTable.values()) {
			fileWriter.write(sym.toString(pad));
		}
	}

	public static void addVariable(String name, VariableTypes type) {
		try {
			_addVariable(name, type);
		} catch (VariablePreviouslyDefined e) {
			System.err.println(ErrorHandling.formatError("Semantic", e.getMessage()));
		}
	}

	public static void addConstant(String value, VariableTypes type) {
		Symbol sym = type.equals(VariableTypes.STRING) ? new Symbol("_" + value, type, value, value.length())
				: new Symbol('_' + value.replace('.', '_'), type, value);
		symbolTable.put(value, sym);
	}

	public static boolean variableExists(String name) {
		return symbolTable.containsKey(name);
	}

	public static Symbol getVariable(String name) {
		return symbolTable.get(name);
	}

	public static VariableTypes castType(String type) throws InvalidTypeException {
		return switch (type) {
			case "int" -> VariableTypes.INT;
			case "float" -> VariableTypes.FLOAT;
			case "string" -> VariableTypes.STRING;
			default -> throw new InvalidTypeException("Tipo de dato no reconocido.");
		};
	}

	private static void _addVariable(String name, VariableTypes type) throws VariablePreviouslyDefined {
		if (symbolTable.containsKey(name)) {
			throw new VariablePreviouslyDefined("Variable \"" + name + "\" ya fue previamente definida.");
		}

		Symbol sym = new Symbol(name, type);
		symbolTable.put(name, sym);
	}

	private static int maxWidth() {
		int longest_var = 10;

		for (Symbol sym : symbolTable.values()) {
			if (sym.length > longest_var) {
				longest_var = sym.length;
			}
		}

		return longest_var;
	}

}
