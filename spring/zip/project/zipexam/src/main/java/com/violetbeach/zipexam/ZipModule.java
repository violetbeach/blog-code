package com.violetbeach.zipexam;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.util.StreamUtils;

public class ZipModule {
    public static void writeZip(OutputStream os, ZipRequest request) {
        Map<String, Integer> fileNames = new HashMap<>();

        try (ZipOutputStream zos = new ZipOutputStream(os)) {
            for (ZipRequest.ZipEntry entry : request.getEntries()) {
                String fileName = entry.getFileName();
                fileNames.put(fileName, fileNames.getOrDefault(fileName, 0) + 1);

                int duplicatedCount = fileNames.get(fileName);
                if (duplicatedCount > 1) {
                    fileName = String.format("%s_%s_%s", FilenameUtils.getBaseName(fileName), duplicatedCount,
                        FileNameUtils.getExtension(fileName));
                }

                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);
                try (InputStream is = entry.getInputStream()) {
                    StreamUtils.copy(is, zos);
                }
                zos.closeEntry();
            }
        } catch (IOException e) {
            throw new DriveIOException(e.getMessage());
        }
    }
}