package dev.tagtag.module.storage.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public interface FileStorageService {

    String saveFile(InputStream in, long size, String filename, String basePath) throws IOException, NoSuchAlgorithmException;

    Path getFilePath(String basePath, String filename);

    boolean fileExists(Path path);

    void deleteFile(Path path) throws IOException;
}
