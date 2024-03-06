package com.violetbeach.zipexam;

import java.io.IOException;
import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DriveFile {
    private final String filename;
    private final InputStream inputStream;

    public byte[] getBytes() {
        try {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new DriveIOException(e.getMessage());
        }
    }
}
