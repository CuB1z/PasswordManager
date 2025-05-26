package com.cub1z.pwmanager.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileUtils {
    
    public static <T extends Serializable> void writeObjectToFile(Path path, T object) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(object);
        }
    }

    public static <T> Optional<T> readObjectFromFile(Path path, Class<T> clazz) throws IOException {
        if (!Files.exists(path)) {
            return Optional.empty();
        }

        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(path))) {
            Object object = in.readObject();

            if (!clazz.isInstance(object)) {
                throw new IOException("Object read from file is not of type " + clazz.getName());
            }

            return Optional.of(clazz.cast(object));
        } catch (IOException e) {
            throw new IOException("Failed to read object from file: " + path, e);
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to read object from file: " + path, e);
        }
    }
}