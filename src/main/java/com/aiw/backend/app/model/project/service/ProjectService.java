package com.aiw.backend.app.model.project.service;

import com.aiw.backend.app.model.action_item.domain.ActionItem;
import com.aiw.backend.app.model.action_item.repository.ActionItemRepository;
import com.aiw.backend.app.model.project.dto.ProjectDTO;
import com.aiw.backend.app.model.project.dto.TodoDTO;
import com.aiw.backend.app.model.team.domain.Team;
import com.aiw.backend.app.model.team.repository.TeamRepository;
import com.aiw.backend.app.model.team_member.domain.TeamMember;
import com.aiw.backend.app.model.team_member.repository.TeamMemberRepository;
import com.aiw.backend.events.BeforeDeleteTeam;
import com.aiw.backend.app.model.project.domain.Project;
import com.aiw.backend.app.model.project.repository.ProjectRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    //팀 확인용 추가
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final ActionItemRepository actionItemRepository;

    public ProjectService(final ProjectRepository projectRepository,
                          final TeamRepository teamRepository,
                          final TeamMemberRepository teamMemberRepository,
                          final ActionItemRepository actionItemRepository) {

        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.actionItemRepository = actionItemRepository;
    }

    @Transactional
    public ProjectDTO create(final ProjectDTO projectDTO) {
        final Project project = new Project();

        // 1. 팀 존재 여부 확인
        Team team = teamRepository.findById(projectDTO.getTeamId())
                .orElseThrow(() -> new NotFoundException("팀을 찾을 수 없습니다."));

        // 2. 엔티티 매핑
        project.setName(projectDTO.getName());
        project.setTargetDate(projectDTO.getTargetDate());
        project.setTeam(team);
        project.setActivated(true);

        // 3. 저장
        final Project savedProject = projectRepository.save(project);

        // 4. 저장된 엔티티를 다시 DTO로 변환해서 반환 (id 포함)
        return mapToDTO(savedProject, new ProjectDTO());
    }

    //조회 로직
    @Transactional(readOnly = true)
    public ProjectDTO get(final Long id) {
        // 1. 프로젝트 정보 가져오기
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("프로젝트를 찾을 수 없습니다."));

        ProjectDTO dto = mapToDTO(project, new ProjectDTO());

        // 2. 실제 DB의 ActionItem 데이터를 가져와서 TodoDTO로 변환
        // (ActionItem 테이블에 meeting_id가 있고, meeting이 team_id를 가지므로 이를 활용)
        List<ActionItem> actionItems = actionItemRepository.findByMeetingProjectId(project.getTeam().getId());

        List<TodoDTO> todos = actionItems.stream()
                .map(item -> {
                    TodoDTO todo = new TodoDTO();
                    todo.setTask(item.getTitle()); // title을 task로 매핑
                    todo.setCompleted(item.getCompleted()); // tinyint(1) -> boolean 자동 매핑
                    return todo;
                })
                .toList();

        dto.setTodos(todos);

        // 3. 실제 데이터를 기반으로 진행률 계산
        if (!todos.isEmpty()) {
            long completedCount = todos.stream().filter(TodoDTO::isCompleted).count();
            int progress = (int) ((completedCount * 100) / todos.size());
            dto.setProgress(progress);
        } else {
            dto.setProgress(0);
        }

        return dto;
    }

    //수정 로직
    @Transactional
    public ProjectDTO update(final Long id, final ProjectDTO projectDTO) {
        // 1. 기존 프로젝트 존재 여부 확인
        final Project project = projectRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("수정할 프로젝트를 찾을 수 없습니다."));

        // 2. 값 수정 (이름과 마감일만 수정 가능하도록 제한)
        if (projectDTO.getName() != null) {
            project.setName(projectDTO.getName());
        }
        if (projectDTO.getTargetDate() != null) {
            project.setTargetDate(projectDTO.getTargetDate());
        }

        // 3. 저장 (Dirty Checking으로 인해 자동 업데이트되지만 명시적으로 호출 가능)
        projectRepository.save(project);

        // 4. 수정된 전체 데이터를 조회(get)해서 반환 (진행률과 투두 리스트 포함)
        return get(id);
    }
    @Transactional
    public ProjectDTO delete(final Long projectId, final Long currentMemberId) {
        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 프로젝트입니다."));

        if (project.getActivated() != null && !project.getActivated()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 삭제된 프로젝트입니다.");
        }

        TeamMember teamMember = teamMemberRepository.findByTeamIdAndMemberId(project.getTeam().getId(), currentMemberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "팀원 정보를 찾을 수 없습니다."));

        if (!"LEADER".equals(teamMember.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "프로젝트 삭제 권한이 없습니다. 팀장만 삭제할 수 있습니다.");
        }

        project.setActivated(false);
        projectRepository.save(project);

        ProjectDTO response = new ProjectDTO();
        response.setId(project.getId());
        response.setActivated(false);
        response.setDeletedAt(OffsetDateTime.now());

        return response;
    }

    private TodoDTO createTodo(String task, boolean completed) {
        TodoDTO todo = new TodoDTO();
        todo.setTask(task);
        todo.setCompleted(completed);
        return todo;
    }

    private ProjectDTO mapToDTO(final Project project, final ProjectDTO projectDTO) {
        projectDTO.setId(project.getId()); // id 반영
        projectDTO.setName(project.getName()); // name 반영
        projectDTO.setTargetDate(project.getTargetDate());
        projectDTO.setTeamId(project.getTeam().getId());
        return projectDTO;
    }



    @EventListener(BeforeDeleteTeam.class)
    public void on(final BeforeDeleteTeam event) {
        final ReferencedException referencedException = new ReferencedException();
        final Project teamProject = projectRepository.findFirstByTeamId(event.getId());
        if (teamProject != null) {
            referencedException.setKey("team.project.team.referenced");
            referencedException.addParam(teamProject.getId().toString());
            throw referencedException;
        }
    }

}
