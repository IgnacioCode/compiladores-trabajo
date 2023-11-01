package lyc.compiler.files;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntermediateCodeGenerator implements FileGenerator {

    // Podemos utilizarla como lifo para escribir el codigo intermido y como una
    // fifo para escribir el assembler.
    public static Deque<Cell> intermediateStack = new ArrayDeque<Cell>(512);
    public static Deque<Cell> temporaryStack = new ArrayDeque<Cell>();
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
            Cell c = new Cell(intermediateStack.size(), value);
            intermediateStack.addFirst(c);
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

    public static void update_to_one_more() {
        Cell c = get_stacked_cell();

        while (c != null) {
            c.value = String.valueOf(intermediateStack.size() + 1);
            c = get_stacked_cell();
        }
    }

    public static void update_to_current() {
        Cell c = get_stacked_cell();

        if (c != null) {
            c.value = String.valueOf(intermediateStack.size());
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

    private static Cell get_stacked_cell() {
        Cell c;

        try {
            c = temporaryStack.removeFirst();
        } catch (NoSuchElementException e) {
            c = null;
        }

        return c;
    }
}
