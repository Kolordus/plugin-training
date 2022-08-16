package file;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileRead {

    public static void main(String[] args) throws URISyntaxException, IOException {

        URL resource = FileRead.class.getClassLoader().getResource("test.txt");
        Path path = Paths.get(resource.toURI());
        Stream<String> lines = Files.lines(path);
        lines.forEach(System.out::println);

        Path source = Paths.get(FileRead.class.getResource("/").toURI());
        Path file = Files.createFile(source);
        Path path1 = Paths.get(String.valueOf(file));
        Files.write(path1, "halo, jedziemy z tym koksem?".getBytes());
    }
}
