package com.violetbeach.zipexam;

import static com.violetbeach.zipexam.Etc.*;
import static com.violetbeach.zipexam.MediaTypes.*;

import java.io.OutputStream;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drives/{driveId}/files")
public class FileController {
    private final DriveFileService driveFileService;

    @GetMapping(produces = APPLICATION_ZIP)
    public void getZip(@PathVariable String driveId,
        HttpServletResponse response) {
        List<DriveFile> driveFiles = driveFileService.getFiles(driveId);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + generateFileName(driveId) + "\";");
        response.setHeader("Content-Type", APPLICATION_ZIP);

        responseZip(response.getOutputStream(), driveFiles);
    }

    private void responseZip(OutputStream os, List<DriveFile> driveFiles) {
        ZipRequest zipRequest = new ZipRequest();
        driveFiles.stream()
            .forEach(driveFile -> zipRequest.addEntry(driveFile.getInputStream(), driveFile.getFilename()));
        ZipModule.writeZip(os, zipRequest);
    }
}