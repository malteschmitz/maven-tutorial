package example;

import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException {
        Calc calc = new Calc();
        System.out.println(calc.twice(2));
        System.out.println(calc.load());
        System.out.println(calc.load2());
    }
}
