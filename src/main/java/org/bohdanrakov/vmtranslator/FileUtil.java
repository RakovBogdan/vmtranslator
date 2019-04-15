package org.bohdanrakov.vmtranslator;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    public static String getFileNameWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public static String changeExtensionInFileName(String fileName, String newExtension) {
        String nameWithoutExtension = getFileNameWithoutExtension(fileName);
        return nameWithoutExtension + newExtension;
    }

    public static List<String> parseResourceToFiles(String resourceToParse) {
        List<String> result = new ArrayList<>();

        if (Files.isDirectory(Paths.get(resourceToParse))) {

        } else {
            result.add(resourceToParse);
        }

        return result;
    }
}
