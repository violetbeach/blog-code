package com.violetbeach.zipexam;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ZipRequest {

    private final List<ZipEntry> entries;

    public ZipRequest() {
        this.entries = new ArrayList<>();
    }

    public void addEntry(InputStream is, String fileName) {
        entries.add(new ZipEntry(is, fileName));
    }

    @Getter
    @AllArgsConstructor
    static class ZipEntry {
        private final InputStream inputStream;
        private final String fileName;
    }

}