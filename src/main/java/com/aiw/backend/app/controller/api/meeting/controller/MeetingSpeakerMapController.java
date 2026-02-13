package com.aiw.backend.app.controller.api.meeting.controller;

import com.aiw.backend.app.model.meeting_speaker_map.dto.MeetingSpeakerMapDTO;
import com.aiw.backend.app.model.meeting_speaker_map.service.MeetingSpeakerMapService;
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
public class MeetingSpeakerMapController {

  private final MeetingSpeakerMapService meetingSpeakerMapService;

<<<<<<< Updated upstream:src/main/java/com/aiw/backend/app/controller/api/meeting/controller/MeetingSpeakerMapResource.java
  public MeetingSpeakerMapResource(final MeetingSpeakerMapService meetingSpeakerMapService) {
    this.meetingSpeakerMapService = meetingSpeakerMapService;
  }
=======
    public MeetingSpeakerMapController(final MeetingSpeakerMapService meetingSpeakerMapService) {
        this.meetingSpeakerMapService = meetingSpeakerMapService;
    }
>>>>>>> Stashed changes:src/main/java/com/aiw/backend/app/controller/api/meeting/controller/MeetingSpeakerMapController.java

  @GetMapping
  public ResponseEntity<List<MeetingSpeakerMapDTO>> getAllMeetingSpeakerMaps() {
    return ResponseEntity.ok(meetingSpeakerMapService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<MeetingSpeakerMapDTO> getMeetingSpeakerMap(
      @PathVariable(name = "id") final String id) {
    return ResponseEntity.ok(meetingSpeakerMapService.get(id));
  }

  @PostMapping
  @ApiResponse(responseCode = "201")
  public ResponseEntity<String> createMeetingSpeakerMap(
      @RequestBody @Valid final MeetingSpeakerMapDTO meetingSpeakerMapDTO) {
    final String createdId = meetingSpeakerMapService.create(meetingSpeakerMapDTO);
    return new ResponseEntity<>('"' + createdId + '"', HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateMeetingSpeakerMap(
      @PathVariable(name = "id") final String id,
      @RequestBody @Valid final MeetingSpeakerMapDTO meetingSpeakerMapDTO) {
    meetingSpeakerMapService.update(id, meetingSpeakerMapDTO);
    return ResponseEntity.ok('"' + id + '"');
  }

  @DeleteMapping("/{id}")
  @ApiResponse(responseCode = "204")
  public ResponseEntity<Void> deleteMeetingSpeakerMap(
      @PathVariable(name = "id") final String id) {
    meetingSpeakerMapService.delete(id);
    return ResponseEntity.noContent().build();
  }

}
