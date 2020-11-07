package example;

import org.apache.commons.io.IOUtils;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Calc {
    public int twice(int a) {
        return a * 2;
    }

    public String load() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("test.txt")) {
            StringBuilder stringBuilder = new StringBuilder();
            try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                int c = 0;
                while ((c = reader.read()) != -1) {
                    stringBuilder.append((char) c);
                }
            }
            return stringBuilder.toString();
        }
    }

    public String load2() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("test.txt")) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }
}
