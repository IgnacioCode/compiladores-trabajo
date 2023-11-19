package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntermediateCodeGenerator implements FileGenerator {

    // Podemos utilizarla como lifo para escribir el codigo intermido y como una
    // fifo para escribir el assembler.
    private static Deque<Cell> intermediateStack = new ArrayDeque<Cell>(512);
    // Stack temporal para guardar saltos no asignados.
    private static Deque<Cell> temporaryStack = new ArrayDeque<Cell>();
    // Stack donde se guardan todos los saltos asignados a una seleccion o
    // iteracion.
    private static Deque<Jump> jumpsStack = new ArrayDeque<Jump>();
    // Ultimo comparador utilizado, lo guardamos en caso de tener que invertirlo por
    // una negacion.
    private static Cell lastCMP = null;

    public static class Cell {
        public int id;
        public String value;

        private Cell(int id) {
            this.id = id;
        }

        private Cell(int id, String value) {
            this.id = id;
            this.value = value;
        }
    }

    private static class Jump {
        int id;
        int jump_id; // Numero de celda donde comienza las condiciones.
        ArrayList<Cell> cells;
        static int current = 0;

        Jump(int jump_id) {
            this.id = current++;
            this.jump_id = jump_id;
            this.cells = new ArrayList<Cell>();
        }
    }

    @Override
    public void generate(FileWriter fileWriter) throws IOException {
        Iterator<Cell> iter = intermediateStack.descendingIterator();
        while (iter.hasNext()) {
            Cell cell = iter.next();
            fileWriter.write("[" + String.format("%03d", cell.id) + "]" + ": " + cell.value + "\n");
        }
    }

    public static Deque<Cell> get() {
        return intermediateStack;
    }

    public static void insert(String... values) {
        for (String value : values) {
            Cell c = new Cell(intermediateStack.size() + 1, value);
            intermediateStack.addFirst(c);
        }
    }

    public static void startJumps(String name) {
        // .ALGO define que luego se deberia crear una etiqueta en assembly (aunque se
        // puede ignorar la del .IF).
        insert("." + name.toUpperCase() + "_" + Jump.current);
        jumpsStack.addFirst(new Jump(intermediateStack.size()));
    }

    public static void moveJumps() {
        Jump l = jumpsStack.peek();
        if (l != null) {
            Cell c = temporaryStack.peek();
            // Mientras el condicional en el stack sea mas nuevo que el inicio del jump...
            while (c != null && c.id > l.jump_id) {
                l.cells.add(c);
                temporaryStack.remove();
                c = temporaryStack.peek();
            }
        }
    }

    public static void endLoopJumps(String name) {
        insert("BI");
        Jump l = getStacked(jumpsStack);
        if (l != null) {
            insert(name.toUpperCase() + "_" + l.id);
            for (Cell c : l.cells) {
                c.value = String.valueOf(intermediateStack.size() + 1);
            }
        }
    }

    public static void endIfJumps() {
        Jump l = getStacked(jumpsStack);
        if (l != null) {
            for (Cell c : l.cells) {
                c.value = String.valueOf(intermediateStack.size() + 1);
            }
        }
    }

    public static void endIfElseJumps() {
        insert("BI");
        move();
        Jump l = getStacked(jumpsStack);
        if (l != null) {
            for (Cell c : l.cells) {
                c.value = String.valueOf(intermediateStack.size() + 1);
            }
        }
        stackCurrent();
    }

    public static void move() {
        Cell c = new Cell(intermediateStack.size() + 1);
        intermediateStack.addFirst(c);
    }

    public static void stackCurrent() {
        Cell c = intermediateStack.peek();
        if (c != null) {
            temporaryStack.addFirst(c);
        }
    }

    public static void moveAndStack() {
        move();
        stackCurrent();
    }

    public static Cell unstack() {
        return intermediateStack.remove();
    }

    public static void updateStacked(int offset) {
        Cell c = getStacked(temporaryStack);
        if (c != null) {
            c.value = String.valueOf(intermediateStack.size() + offset);
        }
    }

    public static int getCurrentID() {
        return intermediateStack.peek().id;
    }

    public static void saveLastCMP() {
        lastCMP = intermediateStack.peek();
    }

    public static void invertCMP() {
        if (lastCMP == null) {
            return;
        }

        lastCMP.value = switch (lastCMP.value) {
            case "BLT" -> "BGE";
            case "BLE" -> "BGT";
            case "BGT" -> "BLE";
            case "BGE" -> "BLT";
            case "BEQ" -> "BNE";
            case "BNE" -> "BEQ";
            default -> "BI";
        };
    }

    private static <T extends Object> T getStacked(Deque<T> stack) {
        T c;
        try {
            c = stack.removeFirst();
        } catch (NoSuchElementException e) {
            c = null;
        }
        return c;
    }

}
