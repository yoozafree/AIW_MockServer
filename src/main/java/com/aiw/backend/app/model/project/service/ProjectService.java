package com.aiw.backend.app.model.project.service;

import com.aiw.backend.app.model.project.dto.ProjectDTO;
import com.aiw.backend.app.model.project.dto.TodoDTO;
import com.aiw.backend.app.model.team.domain.Team;
import com.aiw.backend.app.model.team.repository.TeamRepository;
import com.aiw.backend.events.BeforeDeleteTeam;
import com.aiw.backend.app.model.project.domain.Project;
import com.aiw.backend.app.model.project.repository.ProjectRepository;
import com.aiw.backend.util.NotFoundException;
import com.aiw.backend.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    //팀 확인용 추가
    private final TeamRepository teamRepository;

    public ProjectService(final ProjectRepository projectRepository, final TeamRepository teamRepository) {

        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
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

        // 2. Mock 투두 데이터 생성 (실제 DB가 생기기 전까지)
        List<TodoDTO> mockTodos = List.of(
                createTodo("요구사항 분석", true),
                createTodo("DB 설계", true),
                createTodo("API 개발", false),
                createTodo("FE 연결", false)
        );
        dto.setTodos(mockTodos);

        // 3. 진행률 계산 로직 (간단하지만 확실하게!)
        long totalCount = mockTodos.size();
        long completedCount = mockTodos.stream().filter(TodoDTO::isCompleted).count();

        int progress = totalCount > 0 ? (int) ((completedCount * 100) / totalCount) : 0;
        dto.setProgress(progress);

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
