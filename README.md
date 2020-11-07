# Maven Tutorial

## Beispiel-Code

Wir beginnen mit zwei Java-Dateien:

```java
// Calc.java
package example;

public class Calc {
    public int twice(int a) {
        return a * 2;
    }
}
```

```java
// Main.java
package example;

public class Main {
    public static void main(String args[]) {
        Calc calc = new Calc();
        System.out.println(calc.twice(2));
    }
}

```

Diese Dateien können wir compilieren und ausführen mit

```
javac *.java
mkdir example
mv *.class example
java example.Main
```

(Java will, dass Class-Dateien in einem Ordner passend zum Paket liegen.)

## Maven

Maven ist ein Build und Dependency Management Tool, d.h. es kann zum Auflösen von Abhängigkeiten und zum Bauen von Java-Projekten verwendet werden. Maven ist ein Kommandozeilen-Tool, das über eine Datei konfiguriert wird. Diese Datei kann leicht versioniert und ausgetauscht werden, unabhängig von der IDE.

## Ordnerstruktur

Maven erwartet folgende Ordnerstruktur:

```
.
├── pom.xml
└── src
    └── main
        └── java
            └── example
                ├── Calc.java
                └── Main.java
```

[Anleitung _Maven in 5 Minutes_](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

Einfachste Konfiguration:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
 
  <groupId>de.mlte</groupId>
  <artifactId>example</artifactId>
  <version>1.0-SNAPSHOT</version>
 
  <properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>
</project>
```

Bauen mit Maven:
```
mvn compile
java -cp target/classes example.Main
```

oder
```
mvn package
java -cp target/example-1.0-SNAPSHOT.jar example.Main
```

Maven erzeugt folgende Ordnerstruktur
```
.
├── pom.xml
├── src
│   └── main
│       └── java
│           └── example
│               ├── Calc.java
│               └── Main.java
└── target
    ├── classes
    │   └── example
    │       ├── Calc.class
    │       └── Main.class
    └── example-1.0-SNAPSHOT.jar
```

Alle generierten Dateien löschen
```
mvn clean
```

## Maven und IntelliJ

`pom.xml` einfach als Projekt öffnen.

## Exec Plugin

Ausführen von Projekten mit Maven.

[Anleitung _Exec Maven Plugin_](https://www.mojohaus.org/exec-maven-plugin/usage.html)

Über die Konsole:
```
mvn exec:java -Dexec.mainClass="example.Main"
```

Konfiguration:
```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>
        <version>3.0.0</version>
        <configuration>
          <mainClass>example.Main</mainClass>
        </configuration>
      </plugin>
    </plugins>
  </build>
```

Jetzt ausführen über
```
mvn exec:java
```

## Resources

Resourcen in Java sind Dateien, die zusammen mit den Class-Dateien in die JAR gepackt werden.

Das kann zum Beispiel eine Textdatei sein:

```
Hallo Welt
Tschüß!
```

Maven erwartet folgende Ordnerstruktur:

```
.
├── pom.xml
└── src
    └── main
        ├── java
        │   └── example
        │       ├── Calc.java
        │       └── Main.java
        └── resources
            └── example
                └── test.txt
```

Laden von Resourcen:

```java
// Calc.java
package example;

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
}
```

```java
// Main.java
package example;

import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException {
        Calc calc = new Calc();
        System.out.println(calc.twice(2));
        System.out.println(calc.load());
    }
}

```

## Dependencies

[Apache Commons IO](https://mvnrepository.com/artifact/commons-io/commons-io/2.6) als Dependency konfigurieren:

```xml
  <dependencies>
    <dependency>
      <groupId>commons-io</groupId>
      <artifactId>commons-io</artifactId>
      <version>2.6</version>
    </dependency>
  </dependencies>
```

Damit können Resourcen einfacher geladen werden:

```java
// Calc.java
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
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }
}
```

## Assembly Plugin

Mit Depedencies geht Ausführen des Pakets schief:

```
mvn package
java -cp target/example-1.0-SNAPSHOT.jar example.Main
```
wirft `java.lang.NoClassDefFoundError: org/apache/commons/io/IOUtils`.

Ziel: Bauen einer JAR, die direkt über `java -jar` gestartet werden kann.

[Anleitung _Apache Maven Assembly Plugin_](http://maven.apache.org/plugins/maven-assembly-plugin/usage.html)

Konfiguration als Build-Plugin:
```xml
      <plugin>
        <artifactId>maven-assembly-plugin</artifactId>
        <version>3.3.0</version>
        <configuration>
          <descriptorRefs>
            <descriptorRef>jar-with-dependencies</descriptorRef>
          </descriptorRefs>
          <archive>
            <manifest>
              <mainClass>example.Main</mainClass>
            </manifest>
          </archive>
        </configuration>
      </plugin>
```

Bauen und Ausführen
```
mvn compile assembly:single
java -jar target/example-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## JUnit5

[Anleitung _JUnit 5 User Guide_](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-maven)

Konfiguration von Surefire als Build-Plugin:
```xml
      <plugin>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>2.22.2</version>
      </plugin>
```

Konfiguration von JUnit5 als Dependency:
```xml
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-api</artifactId>
      <version>5.7.0</version>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-engine</artifactId>
      <version>5.7.0</version>
      <scope>test</scope>
    </dependency>
```

Test anlegen:
```java
// CalcTest.java
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
```

Maven erwartet folgende Ordnerstruktur:
```
.
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── example
    │   │       ├── Calc.java
    │   │       └── Main.java
    │   └── resources
    │       └── example
    │           └── test.txt
    └── test
        └── java
            └── example
                └── CalcTest.java
```

Tests ausführen:
```
mvn test
```

## Testreport

[Anleitung _Apache Maven Site Plugin_](https://maven.apache.org/plugins/maven-site-plugin/usage.html)

Konfiguration von Site als Build-Plugin:
```xml
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-site-plugin</artifactId>
        <version>3.7.1</version>
      </plugin>
```

[Anleitung _Maven Surefire Report Plugin_](https://maven.apache.org/surefire/maven-surefire-report-plugin/usage.html)

Konfiguration von Surefire als Report-Plugin:
```xml
  <reporting>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-report-plugin</artifactId>
        <version>3.0.0-M4</version>
      </plugin>
    </plugins>
  </reporting>
```

Report generieren:
```
mvn site
open target/site/index.html
```

Spannende Seiten:
* Project Information
  * Project Dependencies
* Project Reports
  * Surefire Report
