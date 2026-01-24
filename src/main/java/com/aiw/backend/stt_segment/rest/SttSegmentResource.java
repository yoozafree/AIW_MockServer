package com.aiw.backend.stt_segment.rest;

import com.aiw.backend.stt_segment.model.SttSegmentDTO;
import com.aiw.backend.stt_segment.service.SttSegmentService;
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
@RequestMapping(value = "/api/sttSegments", produces = MediaType.APPLICATION_JSON_VALUE)
public class SttSegmentResource {

    private final SttSegmentService sttSegmentService;

    public SttSegmentResource(final SttSegmentService sttSegmentService) {
        this.sttSegmentService = sttSegmentService;
    }

    @GetMapping
    public ResponseEntity<List<SttSegmentDTO>> getAllSttSegments() {
        return ResponseEntity.ok(sttSegmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SttSegmentDTO> getSttSegment(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(sttSegmentService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createSttSegment(
            @RequestBody @Valid final SttSegmentDTO sttSegmentDTO) {
        final Long createdId = sttSegmentService.create(sttSegmentDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateSttSegment(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final SttSegmentDTO sttSegmentDTO) {
        sttSegmentService.update(id, sttSegmentDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSttSegment(@PathVariable(name = "id") final Long id) {
        sttSegmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
