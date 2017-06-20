package com.alienvault.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class FileLoader {

    public static File readFile(final String fileName) {
        ClassLoader classLoader = FileLoader.class.getClassLoader();
        return new File(classLoader.getResource(fileName).getFile());
    }

    public static FileReader loadFileReader(final String fileName) throws FileNotFoundException {
        return new FileReader(readFile(fileName));
    }

}
