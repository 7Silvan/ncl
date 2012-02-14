package ua.group42.taskmanager.control.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class ReadAndPrintXMLFile {

    public static void main(String args[]) {
        try {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(System.in));
            List<String> line = new ArrayList<String>();
            String inputLine;
            do {
                inputLine = input.readLine();
                line.add(inputLine);
            } while (!inputLine.equalsIgnoreCase("exit"));

            doCalc(line.toArray());
        } catch (IOException ex) {
            throw new ArithmeticException("buzinga");
        } catch (ArithmeticException e) {
            String err = e.toString();
            System.out.println(err);
        }
        System.out.println("hello, I'm still working.");
    }

    public static void doCalc(Object... a) {
        try {
            for (Object o : a) {
                System.out.println(o.toString());
            }
            int x = 7 / 0;
        } catch (ArithmeticException ex) {
            System.out.println("Catcherd and thrown: " + ex.getMessage());
            throw ex;
        }
    }
}