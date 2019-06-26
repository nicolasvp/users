package com.microservice.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.users.models.entity.Config;
import com.microservice.users.models.entity.History;
import com.microservice.users.models.entity.Language;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.IHistoryService;
import com.microservice.users.models.services.IUserService;
import com.microservice.users.models.services.IUtilService;
import com.microservice.users.models.services.remote.entity.Author;
import com.microservice.users.models.services.remote.entity.Image;
import com.microservice.users.models.services.remote.entity.Phrase;
import com.microservice.users.models.services.remote.entity.Type;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;

public class UserControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IUserService userService;

    @Mock
    private IHistoryService historyService;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private UserController userController;

    private List<User> dummyUsers;
    private List<Phrase> dummyPhrases;

    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyUserMessages = new ArrayList<>();

    private User user1 = new User();
    private User user2 = new User();
    private User user3 = new User();

    private Phrase phrase1;
    private Phrase phrase2;
    private Phrase phrase3;

    private User invalidUser = new User();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
        createDummyUsers();
        createDummyPhrases();
        setInvalidUser();
        setInvalidUserParamsMessages();
        setEmptyUserMessages();
    }

    private void createDummyUsers() {
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

    private void createDummyPhrases() {
        phrase1 = new Phrase(1L, "TEST1", 1L, new Type(1L, "test1", new Date()), new Author(), new Image());
        phrase2 = new Phrase(2L, "TEST2", 2L, new Type(2L, "test2", new Date()), new Author(), new Image());
        phrase3 = new Phrase(3L, "TEST3", 3L, new Type(3L, "test3", new Date()), new Author(), new Image());

        dummyPhrases = Arrays.asList(phrase1, phrase2, phrase3);
    }

    /**
     * User attributes with random and invalid number of characters
     * name = 60 characters
     * lastName = 60 characters
     * email = 31 characters
     * password = 101 characters
     */
    private void setInvalidUser() {
        invalidUser.setName("EEFzAnTEdWBkhRvB9Xm31D7zklB4qsBjw0NDUTfvBR97t60idBmm2Osyp2WH");
        invalidUser.setLastName("Cv35EYfqrFXoQH0fhWHHhkeBX15uhxB6bpTM4kAGDjOeWXqjefzai6fFFsIe");
        invalidUser.setEmail("G8pGOerRmq4t5jj9kLHWKw146rfkROf");
        invalidUser.setPassword("HqvfGLJWGd4pd563lHalV8pIXCvFsc6bUUi0e5VBu6EkyZOP6jR4L3LoCSg1JKQEr5O8QlHnkCR7HWunPsPRwBoq1bCdPHayvj5Iq\n");
    }

    private void setInvalidUserParamsMessages() {
        invalidParamsMessages.add("El campo name debe tener entre 1 y 50 caracteres");
        invalidParamsMessages.add("El campo email debe tener entre 1 y 30 caracteres");
        invalidParamsMessages.add("El campo lastName debe tener entre 1 y 50 caracteres");
        invalidParamsMessages.add("El campo password debe tener entre 1 y 100 caracteres");
    }

    private void setEmptyUserMessages() {
        emptyUserMessages.add("El campo name no puede estar vacío");
        emptyUserMessages.add("El campo email no puede estar vacío");
        emptyUserMessages.add("El campo lastName no puede estar vacío");
        emptyUserMessages.add("El campo password no puede estar vacío");
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

    /* BEGIN SHOW userController method tests */

    @Test
    public void show_withProperId() throws Exception {
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
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/users/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(userService.findById(999999L)).thenReturn(null);
        mockMvc.perform(get("/api/users/{id}", 999999))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg", is("El registro con ID: 999999 no existe en la base de datos")))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(999999L);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(userService.findById(1L)).thenThrow(new DataAccessException("..."){});
        mockMvc.perform(get("/api/users/{id}", 1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg", is("Error al realizar la consulta en la base de datos")))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).findById(1L);
        verifyNoMoreInteractions(userService);
    }

    /* END SHOW userController method tests */


    /* BEGIN CREATE userController method tests */

    @Test
    public void create_withProperUser() throws Exception {
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
    public void create_whenUserIsEmpty() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyUserMessages);

        mockMvc.perform(post("/api/users")
                .content(objectMapper.writeValueAsString(new User()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(4)))
                .andExpect(jsonPath("$.errors", hasItem("El campo name no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo email no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo lastName no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo password no puede estar vacío")));
    }

    @Test
    public void create_whenUserHasInvalidParams() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(post("/api/users")
                .content(objectMapper.writeValueAsString(invalidUser))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(4)))
                .andExpect(jsonPath("$.errors", hasItem("El campo name debe tener entre 1 y 50 caracteres")))
                .andExpect(jsonPath("$.errors", hasItem("El campo email debe tener entre 1 y 30 caracteres")))
                .andExpect(jsonPath("$.errors", hasItem("El campo lastName debe tener entre 1 y 50 caracteres")))
                .andExpect(jsonPath("$.errors", hasItem("El campo password debe tener entre 1 y 100 caracteres")));
    }

    @Test
    public void create_whenDBFailsThenThrowsException() throws Exception {
        when(userService.save(any(User.class))).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(post("/api/users")
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg", is("Error al intentar guardar el registro")))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userService);
    }

    /* END CREATE userController method tests */


    /* BEGIN UPDATE userController method tests */
    @Test
    public void update_withProperUserAndId() throws Exception {
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
    public void update_whenUserIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/api/users/{id}", "randomString")
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenUserIsEmpty_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyUserMessages);

        mockMvc.perform(put("/api/users/{id}", 1)
                .content(objectMapper.writeValueAsString(new User()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(4)))
                .andExpect(jsonPath("$.errors", hasItem("El campo name no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo email no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo lastName no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo password no puede estar vacío")));
    }

    @Test
    public void update_whenUserIsInvalid_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(put("/api/users/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidUser))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(4)))
                .andExpect(jsonPath("$.errors", hasItem("El campo name debe tener entre 1 y 50 caracteres")))
                .andExpect(jsonPath("$.errors", hasItem("El campo email debe tener entre 1 y 30 caracteres")))
                .andExpect(jsonPath("$.errors", hasItem("El campo lastName debe tener entre 1 y 50 caracteres")))
                .andExpect(jsonPath("$.errors", hasItem("El campo password debe tener entre 1 y 100 caracteres")));
    }

    @Test
    public void update_whenUserIsNotFound() throws Exception {
        when(userService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(put("/api/users/{id}", anyLong())
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("El registro no existe en la base de datos")));

        verify(userService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void update_whenDBFailsThenThrowsException() throws Exception {
        when(userService.save(any(User.class))).thenThrow(new DataAccessException("..."){});
        when(userService.findById(anyLong())).thenReturn(user1);

        mockMvc.perform(put("/api/users/{id}", 1)
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg", is("Error al intentar actualizar el registro en la base de datos")))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).save(any(User.class));
        verify(userService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService);
    }

    /* END UPDATE userController method tests */


    /* BEGIN DELETE userController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        doNothing().when(userService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro eliminado con éxito")));

        verify(userService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void delete_withInvalidId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_whenUserIsNotFoundThenThrowException() throws Exception {
        doThrow(new DataAccessException("..."){}).when(userService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg", is("Error al intentar eliminar el registro en la base de datos, el registro no existe")))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(userService);
    }

    /* END DELETE userController method tests */


    /* BEGIN SET PHRASES userController method tests */

    @Test
    public void setPhrases_whenItsOk() throws Exception {
        History history = new History(user1, 1L, new Date());

        when(userService.findAll()).thenReturn(dummyUsers);
        when(userService.getAllPhrases()).thenReturn(dummyPhrases);
        when(historyService.save(any(History.class))).thenReturn(history);
        when(userService.filterPhraseByAvailability(anyList(), anyList())).thenReturn(dummyPhrases);

        mockMvc.perform(get("/api/users/set-phrases-to-users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.phrasesAsigned").exists())
                .andExpect(status().isOk());
    }

    @Test
    public void setPhrases_whenDBFailsThenThrowsException() throws Exception {
        when(historyService.save(any(History.class))).thenThrow(new DataAccessException("..."){});
        when(userService.findAll()).thenReturn(dummyUsers);
        when(userService.getAllPhrases()).thenReturn(dummyPhrases);
        when(userService.filterPhraseByAvailability(anyList(), anyList())).thenReturn(dummyPhrases);

        mockMvc.perform(get("/api/users/set-phrases-to-users")
                .contentType("application/json"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Error al intentar guardar el registro")));
    }

    /* END SET PHRASES userController method tests */
}
