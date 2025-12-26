package dev.tagtag.module.storage.service.impl;

import dev.tagtag.module.storage.service.FileStorageService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Override
    public String saveFile(InputStream in, long size, String filename, String basePath) throws IOException, NoSuchAlgorithmException {
        Path root = Paths.get(basePath).toAbsolutePath();
        Files.createDirectories(root);
        String fname = System.currentTimeMillis() + "_" + (filename == null ? "file" : filename);
        Path target = root.resolve(fname);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (DigestInputStream dis = new DigestInputStream(in, md); OutputStream out = Files.newOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = dis.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        return HexFormat.of().formatHex(md.digest());
    }

    @Override
    public Path getFilePath(String basePath, String filename) {
        return Paths.get(basePath).toAbsolutePath().resolve(filename);
    }

    @Override
    public boolean fileExists(Path path) {
        return path != null && Files.exists(path);
    }

    @Override
    public void deleteFile(Path path) throws IOException {
        if (path != null && Files.exists(path)) {
            Files.delete(path);
        }
    }
}
