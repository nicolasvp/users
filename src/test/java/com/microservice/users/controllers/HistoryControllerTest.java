package com.microservice.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.users.config.MessagesTranslate;
import com.microservice.users.models.entity.History;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.IHistoryService;
import com.microservice.users.models.services.IUtilService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HistoryControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IHistoryService historyService;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private HistoryController historyController;

	@Autowired
	private MessagesTranslate messages;
	
    private List<History> dummyHistories;

    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyHistoryMessages = new ArrayList<>();

    private History history1;
    private History history2;
    private History history3;

    private void createDummyHistories(){
        history1 = new History(new User(), 1L, new Date());
        history2 = new History(new User(), 2L, new Date());
        history3 = new History(new User(), 3L, new Date());

        dummyHistories = Arrays.asList(history1, history2, history3);
    }

    private void setInvalidHistoryParamsMessages() {
        invalidParamsMessages.add("El campo user debe tener entre 1 y 20 caracteres");
    }

    private void setEmptyHistoryMessages() {
        emptyHistoryMessages.add("El campo user no puede estar vacío");
        emptyHistoryMessages.add("El campo phraseId no puede estar vacío");
    }


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(historyController)
                .build();

        createDummyHistories();
        setInvalidHistoryParamsMessages();
        setEmptyHistoryMessages();
    }

    @Test
    public void index() throws Exception {
        when(historyService.findAll()).thenReturn(dummyHistories);

        mockMvc.perform(get("/api/histories")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].phraseId", is(1)));

        verify(historyService, times(1)).findAll();
        verifyNoMoreInteractions(historyService);
    }

    /* BEGIN SHOW historyController method tests */

    @Test
    public void show_withProperId() throws Exception {
        when(historyService.findById(1L)).thenReturn(history1);

        mockMvc.perform(get("/api/histories/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.phraseId", is(1)));

        verify(historyService, times(1)).findById(1L);
        verifyNoMoreInteractions(historyService);
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/histories/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(historyService.findById(anyLong())).thenReturn(null);
        mockMvc.perform(get("/api/histories/{id}", anyLong()))
                .andExpect(status().isNotFound());

        verify(historyService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(historyService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(historyService.findById(1L)).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(get("/api/histories/{id}", 1))
                .andExpect(status().isInternalServerError());

        verify(historyService, times(1)).findById(1L);
        verifyNoMoreInteractions(historyService);
    }

    /* END SHOW historyController method tests */

    /* BEGIN CREATE historyController method tests */

    @Test
    public void create_withProperHistory() throws Exception {
        when(historyService.save(any(History.class))).thenReturn(history1);

        mockMvc.perform(post("/api/histories")
                .content(objectMapper.writeValueAsString(history1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.history").exists())
                .andExpect(jsonPath("$.history.phraseId", is(1)))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(messages.getCreated())));

        verify(historyService, times(1)).save(any(History.class));
        verifyNoMoreInteractions(historyService);
    }

    @Test
    public void create_whenHistoryIsEmpty() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyHistoryMessages);

        mockMvc.perform(post("/api/histories")
                .content(objectMapper.writeValueAsString(new History()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem("El campo phraseId no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo user no puede estar vacío")));
    }

    @Test
    public void create_whenDBFailsThenThrowsException() throws Exception {
        when(historyService.save(any(History.class))).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(post("/api/histories")
                .content(objectMapper.writeValueAsString(history1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(historyService, times(1)).save(any(History.class));
        verifyNoMoreInteractions(historyService);
    }

    /* END CREATE historyController method tests */

    /* BEGIN UPDATE historyController method tests */

    @Test
    public void update_withProperHistoryAndId() throws Exception {
        when(historyService.findById(anyLong())).thenReturn(history1);
        when(historyService.save(any(History.class))).thenReturn(history1);

        mockMvc.perform(put("/api/histories/{id}", 1)
                .content(objectMapper.writeValueAsString(history1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.history").exists())
                .andExpect(jsonPath("$.history.phraseId", is(1)))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(messages.getUpdated())));

        verify(historyService, times(1)).findById(anyLong());
        verify(historyService, times(1)).save(any(History.class));
        verifyNoMoreInteractions(historyService);
    }

    @Test
    public void update_whenHistoryIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/api/histories/{id}", "randomString")
                .content(objectMapper.writeValueAsString(history1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenHistoryIsEmpty_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyHistoryMessages);

        mockMvc.perform(put("/api/histories/{id}", 1)
                .content(objectMapper.writeValueAsString(new User()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem("El campo phraseId no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo user no puede estar vacío")));
    }

    @Test
    public void update_whenHistoryIsNotFound() throws Exception {
        when(historyService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(put("/api/histories/{id}", anyLong())
                .content(objectMapper.writeValueAsString(history1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isNotFound());

        verify(historyService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(historyService);
    }

    @Test
    public void update_whenDBFailsThenThrowsException() throws Exception {
        when(historyService.save(any(History.class))).thenThrow(new DataAccessException("..."){});
        when(historyService.findById(anyLong())).thenReturn(history1);

        mockMvc.perform(put("/api/histories/{id}", 1)
                .content(objectMapper.writeValueAsString(history1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(historyService, times(1)).save(any(History.class));
        verify(historyService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(historyService);
    }

    /* END UPDATE historyController method tests */

    /* BEGIN DELETE historyController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        doNothing().when(historyService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/histories/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(messages.getDeleted())));

        verify(historyService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(historyService);
    }

    @Test
    public void delete_withInvalidId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/histories/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_whenUserIsNotFoundThenThrowException() throws Exception {
        doThrow(new DataAccessException("..."){}).when(historyService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/histories/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(historyService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(historyService);
    }

    /* END DELETE historyController method tests */
}
