package com.task.manager.controller;
import com.task.manager.dto.TaskRequestDto;
import com.task.manager.dto.TaskResponseDto;
import com.task.manager.security.SecurityConfig;
import com.task.manager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest {

    private static final Long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JwtDecoder jwtDecoder; // needed by the resource-server part of SecurityConfig

    private RequestPostProcessor loggedInAs1() {
        return jwt().jwt(token -> token.subject(String.valueOf(USER_ID)));
    }

    @Test
    void withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(taskService);
    }

    @Test
    void create_returns201WithBody() throws Exception {
        when(taskService.create(eq(USER_ID), any(TaskRequestDto.class)))
                .thenReturn(new TaskResponseDto(1L, "Learn Spring", "desc", false));

        mockMvc.perform(post("/api/tasks")
                        .with(loggedInAs1())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Learn Spring\",\"description\":\"desc\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Learn Spring"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void create_blankTitle_returns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .with(loggedInAs1())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    void create_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .with(loggedInAs1())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{not json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_returnsTheLoggedInUsersTasks() throws Exception {
        when(taskService.getAllTask(USER_ID)).thenReturn(List.of(
                new TaskResponseDto(1L, "A", null, false),
                new TaskResponseDto(2L, "B", null, true)));

        mockMvc.perform(get("/api/tasks").with(loggedInAs1()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].completed").value(true));
    }

    @Test
    void getById_returnsTask() throws Exception {
        when(taskService.getTaskById(USER_ID, 1L)).thenReturn(new TaskResponseDto(1L, "A", "d", false));

        mockMvc.perform(get("/api/tasks/1").with(loggedInAs1()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("A"));
    }

    @Test
    void getById_missing_returns404() throws Exception {
        when(taskService.getTaskById(USER_ID, 42L)).thenThrow(new com.task.manager.handler.TaskNotFoundException(42L));

        mockMvc.perform(get("/api/tasks/42").with(loggedInAs1()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("task not found with the id:42"));
    }

    @Test
    void getById_nonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/api/tasks/abc").with(loggedInAs1()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_returnsUpdatedTask() throws Exception {
        when(taskService.updateById(eq(USER_ID), eq(1L), any(TaskRequestDto.class)))
                .thenReturn(new TaskResponseDto(1L, "New", "d", true));

        mockMvc.perform(put("/api/tasks/1")
                        .with(loggedInAs1())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New\",\"description\":\"d\",\"completed\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/tasks/1").with(loggedInAs1()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_missing_returns404() throws Exception {
        doThrow(new com.task.manager.handler.TaskNotFoundException(7L)).when(taskService).deleteById(USER_ID, 7L);

        mockMvc.perform(delete("/api/tasks/7").with(loggedInAs1()))
                .andExpect(status().isNotFound());
    }
}
