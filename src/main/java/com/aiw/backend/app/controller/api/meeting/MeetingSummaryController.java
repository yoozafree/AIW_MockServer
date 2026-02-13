package com.aiw.backend.app.controller.api.meeting;

import com.aiw.backend.app.model.meeting_summary.dto.MeetingSummaryDTO;
import com.aiw.backend.app.model.meeting_summary.service.MeetingSummaryService;
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
@RequestMapping(value = "/api/meetingSummaries", produces = MediaType.APPLICATION_JSON_VALUE)
public class MeetingSummaryController {

    private final MeetingSummaryService meetingSummaryService;

    public MeetingSummaryController(final MeetingSummaryService meetingSummaryService) {
        this.meetingSummaryService = meetingSummaryService;
    }

    @GetMapping
    public ResponseEntity<List<MeetingSummaryDTO>> getAllMeetingSummaries() {
        return ResponseEntity.ok(meetingSummaryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingSummaryDTO> getMeetingSummary(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(meetingSummaryService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createMeetingSummary(
            @RequestBody @Valid final MeetingSummaryDTO meetingSummaryDTO) {
        final Long createdId = meetingSummaryService.create(meetingSummaryDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateMeetingSummary(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final MeetingSummaryDTO meetingSummaryDTO) {
        meetingSummaryService.update(id, meetingSummaryDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteMeetingSummary(@PathVariable(name = "id") final Long id) {
        meetingSummaryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
