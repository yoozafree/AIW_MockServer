package com.aiw.backend.app.controller.api.meeting;

import com.aiw.backend.app.model.meeting_file.dto.MeetingFileDTO;
import com.aiw.backend.app.model.meeting_file.service.MeetingFileService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/meetingFiles", produces = MediaType.APPLICATION_JSON_VALUE)
public class MeetingFileController {

    private final MeetingFileService meetingFileService;

    public MeetingFileController(final MeetingFileService meetingFileService) {
        this.meetingFileService = meetingFileService;
    }

    @GetMapping
    public ResponseEntity<List<MeetingFileDTO>> getAllMeetingFiles() {
        return ResponseEntity.ok(meetingFileService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingFileDTO> getMeetingFile(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(meetingFileService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createMeetingFile(
            @RequestBody @Valid final MeetingFileDTO meetingFileDTO) {
        final Long createdId = meetingFileService.create(meetingFileDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateMeetingFile(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final MeetingFileDTO meetingFileDTO) {
        meetingFileService.update(id, meetingFileDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteMeetingFile(@PathVariable(name = "id") final Long id) {
        meetingFileService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
