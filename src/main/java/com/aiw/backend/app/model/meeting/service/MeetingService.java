package com.aiw.backend.app.model.meeting.service;

import com.aiw.backend.app.controller.api.meeting.payload.CreateMeetingRecordRequest;
import com.aiw.backend.app.controller.api.meeting.payload.CreateMeetingRecordResponse;
import com.aiw.backend.app.controller.api.meeting.payload.ShowAISummaryResponse;
import com.aiw.backend.app.controller.api.meeting.payload.ShowActionItemResponse;
import com.aiw.backend.app.controller.api.meeting.payload.ShowMeetingListResponse;
import com.aiw.backend.app.controller.api.meeting.payload.ShowSttStatusResponse;
import com.aiw.backend.app.model.meeting.domain.Meeting;
import com.aiw.backend.app.model.meeting.dto.MeetingDTO;
import com.aiw.backend.app.model.meeting.repository.MeetingRepository;
import com.aiw.backend.app.model.project.domain.Project;
import com.aiw.backend.app.model.project.repository.ProjectRepository;
import com.aiw.backend.events.BeforeDeleteMeeting;
import com.aiw.backend.util.CustomCollectors;
import com.aiw.backend.util.NotFoundException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class MeetingService {

  private final MeetingRepository meetingRepository;
  private final ApplicationEventPublisher publisher;

  private final ProjectRepository projectRepository;

  private final AtomicLong meetingSequence = new AtomicLong(1);
  private final Map<Long, String> meetingStatusMap = new ConcurrentHashMap<>();

  public MeetingService(
      final MeetingRepository meetingRepository,
      final ApplicationEventPublisher publisher,
      final ProjectRepository projectRepository
  ) {
    this.meetingRepository = meetingRepository;
    this.publisher = publisher;
    this.projectRepository = projectRepository;
  }

  public List<ShowMeetingListResponse> getMeetingRecords() {
    return List.of(
        new ShowMeetingListResponse(1L, "주간 회의", "2026-03-18T14:00:00", "COMPLETED"),
        new ShowMeetingListResponse(2L, "기획 회의", "2026-03-19T10:00:00", "PROCESSING")
    );
  }

  @Transactional
  public MeetingDTO create(final MeetingDTO meetingDTO) {
    final Meeting meeting = new Meeting();
    mapToEntity(meetingDTO, meeting);

    // 프로젝트 연결 로직
    Project project = projectRepository.findById(meetingDTO.getProjectId())
        .orElseThrow(() -> new NotFoundException("프로젝트를 찾을 수 없습니다."));
    meeting.setProject(project);

    //DB에 저장
    final Meeting savedMeeting = meetingRepository.save(meeting);

    //저장된 엔티티를 다시 DTO로 변환하여 반환 (ID가 채워진 상태)
    return mapToDTO(savedMeeting, new MeetingDTO());
    }
  // 회의 생성
  public CreateMeetingRecordResponse createMeeting(CreateMeetingRecordRequest request) {
    Long meetingId = meetingSequence.getAndIncrement();
    meetingStatusMap.put(meetingId, "PENDING");

    return new CreateMeetingRecordResponse(
        meetingId,
        3L,
        request.getTitle(),
        "PENDING",
        LocalDateTime.now()
    );
  }

  // 파일 업로드 생성
  public CreateMeetingRecordResponse createMeetingByFile(MultipartFile file,
      CreateMeetingRecordRequest request) {

    if (file == null || file.isEmpty()) {
      throw new IllegalArgumentException("업로드된 파일이 없습니다.");
    }

    Long meetingId = meetingSequence.getAndIncrement();
    meetingStatusMap.put(meetingId, "PENDING");

    return new CreateMeetingRecordResponse(
        meetingId,
        1L,
        request.getTitle(),
        "PENDING",
        LocalDateTime.now()
    );
  }

  // 상태 조회
  public ShowSttStatusResponse getSttStatus(Long meetingId) {
    return switch (meetingId.intValue()) {
      case 1 -> new ShowSttStatusResponse(1L, "PENDING", 0);
      case 2 -> new ShowSttStatusResponse(2L, "PROCESSING", 60);
      case 3 -> new ShowSttStatusResponse(3L, "COMPLETED", 100);
      case 4 -> new ShowSttStatusResponse(4L, "FAILED", 0);
      default -> throw new NotFoundException();
    };
  }

  public ShowAISummaryResponse createSummary(Long meetingId) {
    return new ShowAISummaryResponse(
        meetingId,
        "2025/12/11 - 방학과 그로스 진행 관련",
        "2025-12-11T10:00:00",
        List.of("김이화", "이화연", "하주희", "전우치"),
        List.of(
            "방학 내에 개발 완료하기",
            "일주일에 회의는 기본 두 번",
            "2인 1조로 역할 분담하기"
        ),
        List.of(
            "이번 회의에서는 프로젝트 진행 상황을 점검하고, 방학 중 개발 목표를 다시 정리하였다. 팀원들은 핵심 기능 위주로 우선 완성도를 높이는 방향이 필요하다는 데 의견을 모았다.",
            "특히 회의 운영 방식과 관련해서는 주 2회 이상의 정기 회의를 통해 업무 진척과 막히는 부분을 빠르게 공유하자는 이야기가 나왔다. 이를 통해 작업 공백을 줄이고 각 기능별 책임 범위를 더 명확하게 하기로 했다.",
            "또한 화면 설계와 백엔드 구현을 병행하기 위해, 회의가 끝난 직후 결정사항과 TODO를 바로 정리하는 방식이 필요하다는 결론이 나왔고, 회의 분석 페이지에서도 해당 정보가 한눈에 보이도록 구성하기로 했다."
        )
    );
  }

  public List<ShowActionItemResponse> getActionItems(Long meetingId) {
    if (meetingId.equals(1L)) {
      return List.of(
          new ShowActionItemResponse(
              1L,
              LocalDateTime.of(2026, 3, 20, 18, 0),
              "피그마 프로토타입에서 발전시키기",
              "김이화",
              "TODO"
          ),
          new ShowActionItemResponse(
              2L,
              LocalDateTime.of(2026, 3, 21, 14, 0),
              "피그마 디자인 시작하기",
              "이화연",
              "IN_PROGRESS"
          ),
          new ShowActionItemResponse(
              3L,
              LocalDateTime.of(2026, 3, 22, 16, 0),
              "MOCK 서버 완성하기",
              "하주희",
              "TODO"
          ),
          new ShowActionItemResponse(
              4L,
              LocalDateTime.of(2026, 3, 23, 12, 0),
              "데이터베이스 설계 완성하기",
              "전우치",
              "DONE"
          ),
          new ShowActionItemResponse(
              5L,
              LocalDateTime.of(2026, 3, 24, 11, 0),
              "API 명세서 수정 완료하기",
              "김이화",
              "TODO"
          )
      );
    }

    if (meetingId.equals(2L)) {
      return List.of(
          new ShowActionItemResponse(
              6L,
              LocalDateTime.of(2026, 3, 25, 18, 0),
              "로그인 예외 처리 응답 형식 통일하기",
              "김이화",
              "IN_PROGRESS"
          ),
          new ShowActionItemResponse(
              7L,
              LocalDateTime.of(2026, 3, 26, 15, 0),
              "회의 생성 API 프론트 연결하기",
              "전우치",
              "TODO"
          ),
          new ShowActionItemResponse(
              8L,
              LocalDateTime.of(2026, 3, 27, 17, 0),
              "STT 상태 조회 화면 구성하기",
              "이화연",
              "TODO"
          )
      );
    }

    if (meetingId.equals(3L)) {
      return List.of(
          new ShowActionItemResponse(
              9L,
              LocalDateTime.of(2026, 3, 28, 13, 0),
              "회의 분석 페이지 요약 카드 UI 반영하기",
              "김이화",
              "IN_PROGRESS"
          ),
          new ShowActionItemResponse(
              10L,
              LocalDateTime.of(2026, 3, 29, 16, 0),
              "STT 원문 다운로드 기능 테스트하기",
              "이화연",
              "TODO"
          ),
          new ShowActionItemResponse(
              11L,
              LocalDateTime.of(2026, 3, 30, 18, 0),
              "액션 아이템 도메인 분리 구조 초안 잡기",
              "하주희",
              "TODO"
          )
      );
    }
    throw new NotFoundException();
  }

  public ResponseEntity<Resource> downloadMeetingStt(Long meetingId) {
    if (!meetingId.equals(1L) && !meetingId.equals(2L) && !meetingId.equals(3L)) {
      throw new NotFoundException();
    }

    String content = """
    [00:00:01 - 00:00:07] 화자1: 오늘 회의 시작하겠습니다. 먼저 지난 회의에서 정리했던 로그인 기능 수정 진행 상황부터 확인하겠습니다.
    [00:00:08 - 00:00:16] 화자2: 로그인 예외 처리 부분은 거의 마무리되었고, 현재는 응답 형식 통일 작업이 조금 남아 있습니다.
    [00:00:17 - 00:00:27] 화자1: 그러면 그 부분은 오늘 안으로 정리 가능할까요? 프론트에서 붙이려면 에러 응답 형태가 먼저 정리되어야 할 것 같습니다.
    [00:00:28 - 00:00:37] 화자3: 네, 오늘 중으로 가능합니다. 그리고 회의 생성 API 쪽은 녹음 직접 생성과 파일 업로드 생성 두 가지 흐름 모두 확인해봤습니다.
    [00:00:38 - 00:00:49] 화자2: 다만 지금 목서버 기준으로는 meetingId가 고정되어 있고 STT 상태도 항상 completed만 내려가고 있어서 프론트 테스트에는 조금 불편한 상태입니다.
    [00:00:50 - 00:01:01] 화자1: 그럼 상태를 pending, processing, completed, failed 정도로 나눠서 테스트할 수 있게 해야겠네요.
    [00:01:02 - 00:01:13] 화자4: 네, 그리고 summary도 지금 한 줄로만 내려오면 화면이 너무 비어 보여서 segment 단위로 더 길게 주는 편이 좋겠습니다.
    [00:01:14 - 00:01:26] 화자3: 피그마 기준으로 보면 오른쪽 영역은 summary segment 카드 여러 개로 구성되어 있어서, 최소 3개 이상의 블록이 있으면 훨씬 자연스럽게 보일 것 같습니다.
    [00:01:27 - 00:01:39] 화자2: 왼쪽에는 결정사항하고 action item이 따로 보이니까, 요약 응답도 그 구조에 맞게 나눠주는 게 좋을 것 같습니다.
    [00:01:40 - 00:01:52] 화자1: 좋습니다. 그러면 백엔드는 meeting analysis 화면에 맞는 mock 응답 구조를 우선 맞추고, action item 도메인은 별도로 만들되 meeting과 연결만 해두는 방향으로 진행하겠습니다.
    [00:01:53 - 00:02:04] 화자4: action item은 회의에서 파생된 todo라는 점만 유지하면 될 것 같고, 상세 관리나 상태 변경은 action item 도메인에서 담당하면 될 것 같습니다.
    [00:02:05 - 00:02:15] 화자1: 네, 그러면 오늘 회의에서는 API 응답 구조 개편과 action item 도메인 분리 방향까지 정리된 걸로 하겠습니다.
    """;

    ByteArrayResource resource = new ByteArrayResource(content.getBytes(StandardCharsets.UTF_8));
    String fileName = "meeting-" + meetingId + "-stt.txt";

    return ResponseEntity.ok()
        .contentType(MediaType.TEXT_PLAIN)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
        .contentLength(content.getBytes(StandardCharsets.UTF_8).length)
        .body(resource);
  }

  public List<MeetingDTO> findAll() {
    final List<Meeting> meetings = meetingRepository.findAll(Sort.by("id"));
    return meetings.stream()
        .map(meeting -> mapToDTO(meeting, new MeetingDTO()))
        .toList();
  }

  public MeetingDTO get(final Long id) {
    return meetingRepository.findById(id)
        .map(meeting -> mapToDTO(meeting, new MeetingDTO()))
        .orElseThrow(NotFoundException::new);
  }

//  public Long create(final MeetingDTO meetingDTO) {
//    final Meeting meeting = new Meeting();
//    mapToEntity(meetingDTO, meeting);
//    return meetingRepository.save(meeting).getId();
//  }

  public void update(final Long id, final MeetingDTO meetingDTO) {
    final Meeting meeting = meetingRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    mapToEntity(meetingDTO, meeting);
    meetingRepository.save(meeting);
  }

  public void delete(final Long id) {
    final Meeting meeting = meetingRepository.findById(id)
        .orElseThrow(NotFoundException::new);
    publisher.publishEvent(new BeforeDeleteMeeting(id));
    meetingRepository.delete(meeting);
  }

  private MeetingDTO mapToDTO(final Meeting meeting, final MeetingDTO meetingDTO) {
    meetingDTO.setId(meeting.getId());
    meetingDTO.setAgenda(meeting.getAgenda());
    meetingDTO.setScheduledAt(meeting.getScheduledAt());
    meetingDTO.setStartedAt(meeting.getStartedAt());
    meetingDTO.setEndedAt(meeting.getEndedAt());
    meetingDTO.setStatus(meeting.getStatus());
    meetingDTO.setActivated(meeting.getActivated());
    // 추가: DB의 값을 DTO로 옮겨줌
    meetingDTO.setCreatedType(meeting.getCreatedType());
    return meetingDTO;
  }

  private Meeting mapToEntity(final MeetingDTO meetingDTO, final Meeting meeting) {
    meeting.setAgenda(meetingDTO.getAgenda());
    meeting.setScheduledAt(meetingDTO.getScheduledAt());
    meeting.setStartedAt(meetingDTO.getStartedAt());
    meeting.setEndedAt(meetingDTO.getEndedAt());
    meeting.setStatus(meetingDTO.getStatus());
    meeting.setActivated(meetingDTO.getActivated());
    // 추가: 클라이언트가 보낸 값을 엔티티에 세팅
    meeting.setCreatedType(meetingDTO.getCreatedType());
    return meeting;
  }

  public Map<Long, String> getMeetingValues() {
    return meetingRepository.findAll(Sort.by("id"))
        .stream()
        .collect(CustomCollectors.toSortedMap(Meeting::getId, Meeting::getAgenda));
  }
}