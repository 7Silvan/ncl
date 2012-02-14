package ua.group42.taskmanager.tools;

import ua.group42.taskmanager.control.data.WritingFileException;
import java.util.Scanner;
import java.io.*;
import java.util.Arrays;
import org.apache.log4j.*;

/**
 * CSV table with header and rows.
 * @author Group42
 */
public class CSVTable {

    private static final Logger log = Logger.getLogger(CSVTable.class);
    private String[] headers;
    private String[][] values;

    /**
     * Reads csv table from given file.
     * First line of file treated as header.
     */
    public CSVTable(String fileName) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(fileName));

            this.headers = readLine(scanner);

            this.values = new String[100][];
            int rowCount = 0;
            while (scanner.hasNextLine()) {
                if (rowCount == values.length) {
                    values = Arrays.copyOf(values, values.length + 100);
                }
                values[rowCount++] = readLine(scanner);
            }
            if (rowCount != values.length) {
                values = Arrays.copyOf(values, rowCount);
            }
        } catch (Exception ex) {
            log.error(ex);
            throw new WritingFileException(ex.getMessage(), ex);
        } finally {
            scanner.close();
        }
    }

    private String[] readLine(Scanner scanner) {
        String[] vals = scanner.nextLine().split(";");
        for (int i = 0; i < vals.length; i++) {
            vals[i] = vals[i].trim();
        }
        return vals;
    }

    /**
     * @return table headers
     */
    public String[] headers() {
        return headers.clone();
    }

    /**
     * @return row count
     */
    public int getRowCount() {
        return values.length;
    }

    /**
     * @param row row number, 0 is first row
     * @param header name of column, from {@link CSVTable#headers()}
     * @return cell value at given row and column
     */
    public String getValue(int row, String header) {
        return values[row][getColumnByHeader(header)];
    }

    private int getColumnByHeader(String header) {
        int column = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(header)) {
                column = i;
            }
        }
        return column;
    }

    public void setValue(int row, String header, String value) {
        values[row][getColumnByHeader(header)] = value;
    }

    public void writeTo(PrintStream destination) {

        boolean first = true;
        for (String header : this.headers()) {
            destination.print((first ? "" : "\t; ") + header);
            first = false;
        }
        destination.println();

        for (int row = 0; row < getRowCount(); row++) {
            first = true;
            String print = "";
            for (String header : headers()) {
                print = (first ? "" : "\t; ") + getValue(row, header);
                if ("null".equals(print)) break;
                destination.print(print);
                first = false;
            }
            if ("null".equals(print)) break;
            destination.println();
        }
    }


    public void insertRow(int row) {
        String[][] newValues = Arrays.copyOf(values, values.length + 1);
        for (int r = row; r < values.length; r++) {
            newValues[r + 1] = values[r];
        }
        newValues[row] = new String[headers.length];
        this.values = newValues;
    }

    public void removeRow(int row) {
        for (int r = row + 1; r < values.length; r++) {
            values[r - 1] = values[r];
        }
        values = Arrays.copyOf(values, values.length - 1);
    }

    public void eraseTable() {
        for (int i = 0; i < getRowCount(); i++) {
            removeRow(i);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        CSVTable table = new CSVTable("example.csv");
//        table.insertRow(1);
//        table.setValue(1, "name", "New");
//        table.setValue(1, "age", "22");
//        table.setValue(1, "salary", "1200");
//        table.removeRow(0);
        table.writeTo(System.out);
        for (int i = 0; i < table.getRowCount(); i++) {
            System.out.println(table.getValue(i, "name") + "|" + table.getValue(i, "age") + "|" + table.getValue(i, "salary"));
        }
    }
}
