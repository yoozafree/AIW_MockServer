package com.aiw.backend.meeting_speaker_map.rest;

import com.aiw.backend.meeting_speaker_map.model.MeetingSpeakerMapDTO;
import com.aiw.backend.meeting_speaker_map.service.MeetingSpeakerMapService;
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
@RequestMapping(value = "/api/meetingSpeakerMaps", produces = MediaType.APPLICATION_JSON_VALUE)
public class MeetingSpeakerMapResource {

    private final MeetingSpeakerMapService meetingSpeakerMapService;

    public MeetingSpeakerMapResource(final MeetingSpeakerMapService meetingSpeakerMapService) {
        this.meetingSpeakerMapService = meetingSpeakerMapService;
    }

    @GetMapping
    public ResponseEntity<List<MeetingSpeakerMapDTO>> getAllMeetingSpeakerMaps() {
        return ResponseEntity.ok(meetingSpeakerMapService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingSpeakerMapDTO> getMeetingSpeakerMap(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(meetingSpeakerMapService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createMeetingSpeakerMap(
            @RequestBody @Valid final MeetingSpeakerMapDTO meetingSpeakerMapDTO) {
        final Long createdId = meetingSpeakerMapService.create(meetingSpeakerMapDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateMeetingSpeakerMap(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final MeetingSpeakerMapDTO meetingSpeakerMapDTO) {
        meetingSpeakerMapService.update(id, meetingSpeakerMapDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteMeetingSpeakerMap(@PathVariable(name = "id") final Long id) {
        meetingSpeakerMapService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
