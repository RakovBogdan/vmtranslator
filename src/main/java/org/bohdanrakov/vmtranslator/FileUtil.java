package org.bohdanrakov.vmtranslator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil {

    public static List<String> readFileLines(String fileName) {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            return br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeLinesToNewFile(List<String> lines, String newFileName) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(newFileName))) {
            lines.forEach(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String changeExtensionInFileName(String fileName, String newExtension) {
        String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        return nameWithoutExtension + newExtension;
    }
}
