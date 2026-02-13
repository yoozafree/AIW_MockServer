package com.aiw.backend.app.controller.api.meeting.controller;

import com.aiw.backend.app.model.meeting_participant.dto.MeetingParticipantDTO;
import com.aiw.backend.app.model.meeting_participant.service.MeetingParticipantService;
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
@RequestMapping(value = "/api/meetingParticipants", produces = MediaType.APPLICATION_JSON_VALUE)
public class MeetingParticipantController {

    private final MeetingParticipantService meetingParticipantService;

    public MeetingParticipantController(final MeetingParticipantService meetingParticipantService) {
        this.meetingParticipantService = meetingParticipantService;
    }

    @GetMapping
    public ResponseEntity<List<MeetingParticipantDTO>> getAllMeetingParticipants() {
        return ResponseEntity.ok(meetingParticipantService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingParticipantDTO> getMeetingParticipant(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(meetingParticipantService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createMeetingParticipant(
            @RequestBody @Valid final MeetingParticipantDTO meetingParticipantDTO) {
        final Long createdId = meetingParticipantService.create(meetingParticipantDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateMeetingParticipant(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final MeetingParticipantDTO meetingParticipantDTO) {
        meetingParticipantService.update(id, meetingParticipantDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteMeetingParticipant(@PathVariable(name = "id") final Long id) {
        meetingParticipantService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
