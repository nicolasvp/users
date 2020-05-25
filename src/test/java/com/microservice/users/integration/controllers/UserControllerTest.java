package com.microservice.users.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.models.entity.users.User;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Pruebas de integración utilizando base de datos en memoria(H2)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Configuracion de base de datos h2, tomando propiedades de yml test
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyUserMessages = new ArrayList<>();

    private User user1 = new User();
    private User user2 = new User();

    private User invalidUser = new User();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(wac).build();
        createDummyUsers();
        setInvalidUser();
        setInvalidUserParamsMessages();
        setEmptyUserMessages();
    }

    private void createDummyUsers() {
        user1.setUsername("USERNAME 1");
        user1.setName("USER 1");
        user1.setLastName("LASTNAME 1");
        user1.setEmail("EMAIL 1");
        user1.setPassword("PASSWORD 1");
        //user1.setConfig(new Config(user1, new Language("Ingles"), 1, true, new Date()));
        //user1.setHistory(Arrays.asList(new History(user1, 1L, new Date())));

        user2.setUsername("USERNAME 2");
        user2.setName("USER 2");
        user2.setLastName("LASTNAME 2");
        user2.setEmail("EMAIL 2");
        user2.setPassword("PASSWORD 2");
        //user2.setConfig(new Config(user2, new Language("Ingles"), 2, true, new Date()));
    }

    /**
     * User attributes with random and invalid number of characters
     * name = 60 characters
     * lastName = 60 characters
     * email = 31 characters
     * password = 101 characters
     */
    private void setInvalidUser() {
        invalidUser.setUsername("TEdWBkhRvB9Xm31D7zklB4qsBjw0NDUTfvBR97t60i");
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
    public void a_index() throws Exception {
        mockMvc.perform(get("/users")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$.[0].name", is("Johnathon")));
    }

    /* BEGIN SHOW userController method tests */

    @Test
    public void show_withProperId() throws Exception {
        mockMvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Johnathon")))
                .andExpect(jsonPath("$.email", is("Johnathon@mail.com")));
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/users/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        mockMvc.perform(get("/users/{id}", 0))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /* END SHOW userController method tests */


    /* BEGIN CREATE userController method tests */

    @Test
    public void create_withProperUser() throws Exception {
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.id", is(6)))
                .andExpect(jsonPath("$.user.name", is("USER 1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.CREATED.getMessage())));
    }

    @Test
    public void create_whenUserIsEmpty() throws Exception {
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(new User()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
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
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(invalidUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
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

    /* END CREATE userController method tests */

    /* BEGIN UPDATE userController method tests */

    @Test
    public void update_withProperUserAndId() throws Exception {
        mockMvc.perform(put("/users/{id}", 2)
                .content(objectMapper.writeValueAsString(user2))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.id", is(2)))
                .andExpect(jsonPath("$.user.name", is("USER 2")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.UPDATED.getMessage())));
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
        mockMvc.perform(put("/users/{id}", 1)
                .content(objectMapper.writeValueAsString(new User()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
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
        mockMvc.perform(put("/users/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
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
        mockMvc.perform(put("/users/{id}", 0)
                .content(objectMapper.writeValueAsString(user1))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /* END UPDATE userController method tests */

    /* BEGIN DELETE userController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.DELETED.getMessage())));
    }

    @Test
    public void delete_withInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    /* END DELETE userController method tests */

    /* BEGIN SEARCH USER BY USERNAME userController method tests */

    @Test
    public void search_withProperUsername() throws Exception {
        mockMvc.perform(get("/users/search/{username}", "calvin"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Calvin")))
                .andExpect(jsonPath("$.email", is("Calvin@mail.com")));
    }

    @Test
    public void search_whenUsernameIsInvalid() throws Exception {
        mockMvc.perform(get("/users/search/{username}", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void search_whenRecordDoesNotExist() throws Exception {
        mockMvc.perform(get("/users/search/{username}", "user1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /* END DELETE userController method tests */
}
