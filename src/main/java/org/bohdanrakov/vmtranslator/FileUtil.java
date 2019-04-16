package org.bohdanrakov.vmtranslator;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FileUtil {

    private static final String TXT_EXTENSION = ".txt";
    private static final String UTF_8 = "UTF-8";

    public static List<String> getLinesFromResource(String resourceName) {
        ClassLoader classLoader = CodeWriter.class.getClassLoader();
        try {
            return IOUtils.readLines(
                    classLoader.getResourceAsStream(resourceName + TXT_EXTENSION), UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> readFileLines(Path filePath) {
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
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

    public static String getFileNameWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public static String changeExtensionInFileName(String fileName, String newExtension) {
        String nameWithoutExtension = getFileNameWithoutExtension(fileName);
        return nameWithoutExtension + newExtension;
    }

    public static List<Path> parseResourceToFileNames(String resourceToParse) {
        if (Files.isDirectory(Paths.get(resourceToParse))) {
            try {
                return Files.walk(Paths.get(resourceToParse))
                        .filter(Files::isRegularFile)
                        .filter(path -> path.toString().endsWith(".vm"))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return Collections.singletonList(Paths.get(resourceToParse));
        }
    }
}
