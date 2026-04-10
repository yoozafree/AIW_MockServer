package com.aiw.backend.app.controller.api.meeting;

import com.aiw.backend.app.model.action_item.dto.ActionItemDTO;
import com.aiw.backend.app.model.action_item.service.ActionItemService;
import io.swagger.v3.oas.annotations.Operation;
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

  // *액션아이템 전체 조회 (대시보드용) ?
  @GetMapping
  @Operation(summary = "*액션아이템 전체 조회 (대시보드용)")
  public ResponseEntity<List<ActionItemDTO>> getAllActionItems(@RequestParam(name = "memberId", required = false) final Long memberId) {
    return ResponseEntity.ok(actionItemService.getActionItems(memberId));
  }

  // 액션아이템 단건 조회 (액션아이템 ID 로)
  @GetMapping("/{id}")
  @Operation(summary = "액션아이템 단건 조회 (액션아이템 ID 로)")
  public ResponseEntity<ActionItemDTO> getActionItem(@PathVariable(name = "id") final Long id) {
    return ResponseEntity.ok(actionItemService.get(id));
  }

  // 액션아이템 생성
  @PostMapping
  @Operation(summary = "액션아이템 생성")
  @ApiResponse(responseCode = "201")
  public ResponseEntity<Long> createActionItem(
      @RequestBody @Valid final ActionItemDTO actionItemDTO) {
    final Long createdId = actionItemService.create(actionItemDTO);
    return new ResponseEntity<>(createdId, HttpStatus.CREATED);
  }

  // *액션아이템 수정 ?
  @PutMapping("/{id}")
  @Operation(summary = "*액션아이템 수정")
  public ResponseEntity<ActionItemDTO> updateActionItem(@PathVariable(name = "id") final Long id,
      @RequestBody @Valid final ActionItemDTO actionItemDTO) {
    ActionItemDTO updatedDTO = actionItemService.update(id, actionItemDTO);
    return ResponseEntity.ok(updatedDTO);
  }

  // 액션아이템 삭제
  @DeleteMapping("/{id}")
  @Operation(summary = "액션아이템 삭제")
  @ApiResponse(responseCode = "204")
  public ResponseEntity<Void> deleteActionItem(@PathVariable(name = "id") final Long id) {
    actionItemService.delete(id);
    return ResponseEntity.noContent().build();
  }
}