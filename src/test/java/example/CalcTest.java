package example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalcTest {
    @Test
    public void testTwice() {
        Calc calc = new Calc();
        assertEquals(4, calc.twice(2));
    }
}
