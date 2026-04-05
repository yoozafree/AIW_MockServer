package com.aiw.backend.app.controller.api.meeting;

import com.aiw.backend.app.model.action_item.dto.ActionItemDTO;
import com.aiw.backend.app.model.action_item.service.ActionItemService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/actionItems", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ActionItemController {

  private final ActionItemService actionItemService;

  @GetMapping
  public ResponseEntity<List<ActionItemDTO>> getAllActionItems(@RequestParam(name = "memberId", required = false) final Long memberId) {
    return ResponseEntity.ok(actionItemService.getActionItems(memberId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ActionItemDTO> getActionItem(@PathVariable(name = "id") final Long id) {
    return ResponseEntity.ok(actionItemService.get(id));
  }

  @PostMapping
  @ApiResponse(responseCode = "201")
  public ResponseEntity<Long> createActionItem(
      @RequestBody @Valid final ActionItemDTO actionItemDTO) {
    final Long createdId = actionItemService.create(actionItemDTO);
    return new ResponseEntity<>(createdId, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ActionItemDTO> updateActionItem(@PathVariable(name = "id") final Long id,
      @RequestBody @Valid final ActionItemDTO actionItemDTO) {
    ActionItemDTO updatedDTO = actionItemService.update(id, actionItemDTO);
    return ResponseEntity.ok(updatedDTO);
  }

  @DeleteMapping("/{id}")
  @ApiResponse(responseCode = "204")
  public ResponseEntity<Void> deleteActionItem(@PathVariable(name = "id") final Long id) {
    actionItemService.delete(id);
    return ResponseEntity.noContent().build();
  }

}
