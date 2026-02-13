package com.aiw.backend.app.controller.api.meeting;

import com.aiw.backend.app.model.action_item.dto.ActionItemDTO;
import com.aiw.backend.app.model.action_item.service.ActionItemService;
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
@RequestMapping(value = "/api/actionItems", produces = MediaType.APPLICATION_JSON_VALUE)
public class ActionItemController {

  private ActionItemService actionItemService;

  @GetMapping
  public ResponseEntity<List<ActionItemDTO>> getAllActionItems() {
    return ResponseEntity.ok(actionItemService.findAll());
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
  public ResponseEntity<Long> updateActionItem(@PathVariable(name = "id") final Long id,
      @RequestBody @Valid final ActionItemDTO actionItemDTO) {
    actionItemService.update(id, actionItemDTO);
    return ResponseEntity.ok(id);
  }

  @DeleteMapping("/{id}")
  @ApiResponse(responseCode = "204")
  public ResponseEntity<Void> deleteActionItem(@PathVariable(name = "id") final Long id) {
    actionItemService.delete(id);
    return ResponseEntity.noContent().build();
  }

}
