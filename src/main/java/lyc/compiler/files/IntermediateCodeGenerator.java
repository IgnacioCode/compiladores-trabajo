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
    public static Deque<Cell> intermediateStack = new ArrayDeque<Cell>(512);
    public static Deque<Cell> temporaryStack = new ArrayDeque<Cell>();
    public static Deque<Loop> loopStack = new ArrayDeque<Loop>();
    private static Cell lastCMP = null;

    private static class Cell {
        int id;
        String value;

        Cell(int id) {
            this.id = id;
        }

        Cell(int id, String value) {
            this.id = id;
            this.value = value;
        }
    }

    private static class Loop {
        int id;
        ArrayList<Cell> cells;
        static int current = 0;

        Loop() {
            this.id = current++;
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

    public static void insert(String... values) {
        for (String value : values) {
            Cell c = new Cell(intermediateStack.size() + 1, value);
            intermediateStack.addFirst(c);
        }
    }

    public static void insertLoop() {
        // .ALGO define que luego se deberia crear una etiqueta en assembler
        insert(".LOOP_" + Loop.current);
        loopStack.addFirst(new Loop());
    }

    public static void moveToLoop() {
        Loop l = loopStack.peek();
        if (l != null) {
            Cell c = getStacked(temporaryStack);
            while (c != null) {
                l.cells.add(c);
                c = getStacked(temporaryStack);
            }
        }
    }

    public static void endLoop() {
        Loop l = getStacked(loopStack);
        if (l != null) {
            insert("LOOP_" + l.id);
            for (Cell c : l.cells) {
                c.value = String.valueOf(intermediateStack.size() + 1);
            }
        }
    }

    public static void move() {
        Cell c = new Cell(intermediateStack.size());
        intermediateStack.addFirst(c);
    }

    public static void stack_current() {
        Cell c = intermediateStack.peek();
        if (c != null) {
            temporaryStack.addFirst(c);
        }
    }

    public static void stack_and_move() {
        move();
        stack_current();
    }

    public static void update_stacked_cell(int offset) {
        Cell c = getStacked(temporaryStack);
        if (c != null) {
            c.value = String.valueOf(intermediateStack.size() + offset);
        }
    }

    public static void update_all_stacked_cell(int offset) {
        Cell c = getStacked(temporaryStack);
        while (c != null) {
            c.value = String.valueOf(intermediateStack.size() + offset);
            c = getStacked(temporaryStack);
        }
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
