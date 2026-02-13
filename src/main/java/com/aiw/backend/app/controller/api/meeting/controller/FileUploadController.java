package com.aiw.backend.app.controller.api.meeting.controller;

import com.aiw.backend.app.model.file.model.FileData;
import com.aiw.backend.app.model.file.service.FileDataService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class FileUploadController {

    private final FileDataService fileDataService;

    public FileUploadController(final FileDataService fileDataService) {
        this.fileDataService = fileDataService;
    }

    @PostMapping(
            value = "/fileUpload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<FileData> fileUpload(@RequestPart("file") final MultipartFile file) {
        final FileData tempFile = fileDataService.saveUpload(file);
        return ResponseEntity.ok(tempFile);
    }

}
