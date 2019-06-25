package com.microservice.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.users.models.entity.Config;
import com.microservice.users.models.entity.History;
import com.microservice.users.models.entity.Language;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.IHistoryService;
import com.microservice.users.models.services.IUserService;
import com.microservice.users.models.services.remote.entity.Author;
import com.microservice.users.models.services.remote.entity.Image;
import com.microservice.users.models.services.remote.entity.Phrase;
import com.microservice.users.models.services.remote.entity.Type;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

public class UserControllerTest  {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IUserService userService;

    @Mock
    private IHistoryService historyService;

    @InjectMocks
    private UserController userController;

    private List<User> dummyUsers;
    private List<Phrase> dummyPhrases;

    private User user1 = new User();
    private User user2 = new User();
    private User user3 = new User();

    private Phrase phrase1;
    private Phrase phrase2;
    private Phrase phrase3;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
        createDummyUsers();
        createDummyPhrases();
    }

    private void createDummyUsers(){
        user1.setId(1L);
        user1.setName("USER 1");
        user1.setLastName("LASTNAME 1");
        user1.setEmail("EMAIL 1");
        user1.setPassword("PASSWORD 1");
        user1.setConfig(new Config(user1, new Language(), 1, true, new Date()));
        user1.setHistory(Arrays.asList(new History(user1, 1L, new Date())));

        user2.setId(2L);
        user2.setName("USER 2");
        user2.setEmail("EMAIL 2");
        user2.setConfig(new Config(user2, new Language(), 2, true, new Date()));

        user3.setId(3L);
        user3.setName("USER 3");
        user3.setEmail("EMAIL 3");
        user3.setConfig(new Config(user3, new Language(), 3, true, new Date()));

        dummyUsers = Arrays.asList(user1, user2, user3);
    }

    private void createDummyPhrases(){
        phrase1 = new Phrase(1L, "TEST1", 1L, new Type(1L, "test1", new Date()), new Author(), new Image());
        phrase2 = new Phrase(2L, "TEST2", 2L, new Type(2L, "test2", new Date()), new Author(), new Image());
        phrase3 = new Phrase(3L, "TEST3", 3L, new Type(3L, "test3", new Date()), new Author(), new Image());

        dummyPhrases = Arrays.asList(phrase1, phrase2, phrase3);
    }

    @Test
    public void index() throws Exception {
        when(userService.findAll()).thenReturn(dummyUsers);

        mockMvc.perform(get("/api/users")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].name", is("USER 1")));

        verify(userService, times(1)).findAll();
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void show_whenItsOk() throws Exception {
        when(userService.findById(1L)).thenReturn(user1);

        mockMvc.perform(get("/api/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("USER 1")))
                .andExpect(jsonPath("$.email", is("EMAIL 1")));

        verify(userService, times(1)).findById(1L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void create_whenItsOk() throws Exception {
        when(userService.save(any(User.class))).thenReturn(user1);

        mockMvc.perform(post("/api/users")
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.user.name", is("USER 1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro creado con éxito")));

        verify(userService, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void update_whenItsOk() throws Exception {
        when(userService.findById(anyLong())).thenReturn(user1);
        when(userService.save(any(User.class))).thenReturn(user1);

        mockMvc.perform(put("/api/users/{id}", 1)
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.user.name", is("USER 1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro actualizado con éxito")));

        verify(userService, times(1)).findById(anyLong());
        verify(userService, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void delete_whenItOk() throws Exception {
        doNothing().when(userService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro eliminado con éxito")));

        verify(userService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void setPhrases_whenItsOk() throws Exception {
        History history = new History(user1, 1L, new Date());

        when(userService.findAll()).thenReturn(dummyUsers);
        when(userService.getAllPhrases()).thenReturn(dummyPhrases);
        when(historyService.save(any(History.class))).thenReturn(history);

        mockMvc.perform(get("/api/users/set-phrases-to-users")
                .contentType("application/json"))
                .andExpect(jsonPath("$.phrasesAsigned").exists())
                .andExpect(status().isOk());
    }
}
