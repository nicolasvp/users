package com.microservice.users.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.users.controllers.UserController;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.models.entity.users.User;
import com.microservice.users.models.services.IUserService;
import com.microservices.commons.models.services.IUtilService;
import com.microservices.commons.models.entity.phrases.Author;
import com.microservices.commons.models.entity.phrases.Image;
import com.microservices.commons.models.entity.phrases.Phrase;
import com.microservices.commons.models.entity.phrases.Type;
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

    //@Mock
    //private IHistoryService historyService;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private UserController userController;

    private List<User> dummyUsers;

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
        user1.setUsername("USERNAME 1");
        user1.setName("USER 1");
        user1.setLastName("LASTNAME 1");
        user1.setEmail("EMAIL 1");
        user1.setPassword("PASSWORD 1");
        //user1.setHistory(Arrays.asList(new History(user1, 1L, new Date())));

        user2.setId(2L);
        user2.setUsername("USERNAME 2");
        user2.setName("USER 2");
        user2.setEmail("EMAIL 2");

        user3.setId(3L);
        user3.setUsername("USERNAME 3");
        user3.setName("USER 3");
        user3.setEmail("EMAIL 3");

        dummyUsers = Arrays.asList(user1, user2, user3);
    }

    private void createDummyPhrases() {
        phrase1 = new Phrase(1L, "TEST1", 1L, new Type(1L, "test1", new Date()), new Author(), new Image());
        phrase2 = new Phrase(2L, "TEST2", 2L, new Type(2L, "test2", new Date()), new Author(), new Image());
        phrase3 = new Phrase(3L, "TEST3", 3L, new Type(3L, "test3", new Date()), new Author(), new Image());
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
        invalidParamsMessages.add("The field username must have between 1 and 20 characters");
        invalidParamsMessages.add("The field name must have between 1 and 50 characters");
        invalidParamsMessages.add("The field last name must have between 1 and 50 characters");
        invalidParamsMessages.add("The field email must have between 1 and 30 characters");
        invalidParamsMessages.add("The field password must have between 1 and 100 characters");
    }

    private void setEmptyUserMessages() {
        emptyUserMessages.add("The field username must not be empty");
        emptyUserMessages.add("The field name must not be empty");
        emptyUserMessages.add("The field last name must not be empty");
        emptyUserMessages.add("The field email must not be empty");
        emptyUserMessages.add("The field password must not be empty");
    }

    @Test
    public void index() throws Exception {
        when(userService.findAll()).thenReturn(dummyUsers);

        mockMvc.perform(get("/users")
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

        mockMvc.perform(get("/users/{id}", 1))
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
        mockMvc.perform(get("/users/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(userService.findById(anyLong())).thenReturn(null);
        mockMvc.perform(get("/users/{id}", anyLong()))
        		//.andDo(print())
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(userService.findById(1L)).thenThrow(new DataAccessException("..."){});
        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).findById(1L);
        verifyNoMoreInteractions(userService);
    }

    /* END SHOW userController method tests */


    /* BEGIN CREATE userController method tests */

    @Test
    public void create_withProperUser() throws Exception {
        when(userService.save(any(User.class))).thenReturn(user1);

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
        		.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.user.name", is("USER 1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.CREATED.getMessage())));

        verify(userService, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void create_whenUserIsEmpty() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyUserMessages);

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(new User()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(5)))
                .andExpect(jsonPath("$.errors", hasItem(emptyUserMessages.get(0))))
                .andExpect(jsonPath("$.errors", hasItem(emptyUserMessages.get(1))))
                .andExpect(jsonPath("$.errors", hasItem(emptyUserMessages.get(2))))
                .andExpect(jsonPath("$.errors", hasItem(emptyUserMessages.get(3))))
                .andExpect(jsonPath("$.errors", hasItem(emptyUserMessages.get(4))));
    }

    @Test
    public void create_whenUserHasInvalidParams() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(invalidUser))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(5)))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(0))))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(1))))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(2))))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(3))))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(4))));
    }

    @Test
    public void create_whenDBFailsThenThrowsException() throws Exception {
        when(userService.save(any(User.class))).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
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

        mockMvc.perform(put("/users/{id}", 1L)
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.user.name", is("USER 1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.UPDATED.getMessage())));

        verify(userService, times(1)).findById(anyLong());
        verify(userService, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void update_whenUserIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/users/{id}", "randomString")
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenUserIsEmpty_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyUserMessages);

        mockMvc.perform(put("/users/{id}", 1)
                .content(objectMapper.writeValueAsString(new User()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(5)))
                .andExpect(jsonPath("$.errors", hasItem(emptyUserMessages.get(0))))
                .andExpect(jsonPath("$.errors", hasItem(emptyUserMessages.get(1))))
                .andExpect(jsonPath("$.errors", hasItem(emptyUserMessages.get(2))))
                .andExpect(jsonPath("$.errors", hasItem(emptyUserMessages.get(3))))
                .andExpect(jsonPath("$.errors", hasItem(emptyUserMessages.get(4))));
    }

    @Test
    public void update_whenUserIsInvalid_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(put("/users/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidUser))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(5)))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(0))))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(1))))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(2))))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(3))))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(4))));
    }

    @Test
    public void update_whenUserIsNotFound() throws Exception {
        when(userService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(put("/users/{id}", anyLong())
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void update_whenDBFailsThenThrowsException() throws Exception {
        when(userService.save(any(User.class))).thenThrow(new DataAccessException("..."){});
        when(userService.findById(anyLong())).thenReturn(user1);

        mockMvc.perform(put("/users/{id}", 1)
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
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

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.DELETED.getMessage())));

        verify(userService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void delete_withInvalidId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_whenUserIsNotFoundThenThrowException() throws Exception {
        doThrow(new DataAccessException("..."){}).when(userService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(userService);
    }

    /* END DELETE userController method tests */

    /* BEGIN SEARCH USER BY USERNAME userController method tests */

    @Test
    public void search_withProperUsername() throws Exception {
        when(userService.findByUsername(anyString())).thenReturn(user1);

        mockMvc.perform(get("/users/search/{username}", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("USER 1")))
                .andExpect(jsonPath("$.email", is("EMAIL 1")));

        verify(userService, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void search_whenUsernameIsInvalid() throws Exception {
        mockMvc.perform(get("/users/search/{username}", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void search_whenRecordDoesNotExist() throws Exception {
        when(userService.findByUsername(anyString())).thenReturn(null);
        mockMvc.perform(get("/users/search/{username}", "user1"))
                //.andDo(print())
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void search_whenDBFailsThenThrowsException() throws Exception {
        when(userService.findByUsername(anyString())).thenThrow(new DataAccessException("..."){});
        mockMvc.perform(get("/users/search/{username}", "user1"))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).findByUsername(anyString());
        verifyNoMoreInteractions(userService);
    }

    /* END DELETE userController method tests */
}
