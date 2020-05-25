package com.microservice.users.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.models.entity.users.Rol;
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
import java.util.Date;
import java.util.List;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Pruebas de integración utilizando base de datos en memoria(H2)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Configuracion de base de datos h2, tomando propiedades de yml test
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RolControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();


    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyRolMessages = new ArrayList<>();
    private Rol invalidRol = new Rol();

    private Rol rol1;
    private Rol rol2;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(wac).build();

        createDummyRoles();
        setInvalidRol();
        setInvalidRolParamsMessages();
        setEmptyRolMessages();
    }

    private void createDummyRoles(){
        rol1 = new Rol("ROL1", "TEST ROL1", new Date());
        rol2 = new Rol("ROL2", "TEST ROL2", new Date());
    }

    /**
     * Rol attributes with random and invalid number of characters
     * name = 21 characters
     * description = 301 characters
     */
    private void setInvalidRol() {
        invalidRol.setName("BptgmU0f2RctROCER4TZB");
        invalidRol.setDescription("lCyoKH8NtTGZsN2cstlec1eBDgzbrZcJ2JRXrUCKeeXtg0hKlaYZuReJVTO8TUAt1GWq9pieZsCalNtVkuhf5pIO7NnND2Dbog5gZCvIdHCykUMa4Uthjyc8Ro6gkgVxREhGpqOBNXPCtklCojmUALsauyBqRv1uyBtxZwE49wTIAT6f2E9hbwFXp4pcxh7ezIylORQySGugDXuQNfH64XCDhl8dC0Vaox00cSSk9MNgFrWGBHE71EJcMRwSTOQe9ot2HEPtUWy5PFMW8VkihSQIluhYicrKbVpvuhOgI25s6");
    }

    private void setInvalidRolParamsMessages() {
        invalidParamsMessages.add("The field name must have between 1 and 20 characters");
        invalidParamsMessages.add("The field description must have between 1 and 50 characters");
    }

    private void setEmptyRolMessages() {
        emptyRolMessages.add("The field name must not be empty");
        emptyRolMessages.add("The field description must not be empty");
    }

    @Test
    public void a_index() throws Exception {
        mockMvc.perform(get("/roles")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].name", is("ROLE_ADMIN")));
    }

    /* BEGIN SHOW rolController method tests */

    @Test
    public void show_withProperId() throws Exception {
        mockMvc.perform(get("/roles/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name", is("ROLE_ADMIN")))
                .andExpect(jsonPath("$.description", is("System Administrator")));
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/roles/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        mockMvc.perform(get("/roles/{id}", 0))
                .andExpect(status().isNotFound());
    }

    /* END SHOW rolController method tests */

    /* BEGIN CREATE rolController method tests */

    @Test
    public void create_withProperRol() throws Exception {
        mockMvc.perform(post("/roles")
                .content(objectMapper.writeValueAsString(rol1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.rol").exists())
                .andExpect(jsonPath("$.rol.name", is("ROL1")))
                .andExpect(jsonPath("$.rol.description", is("TEST ROL1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.CREATED.getMessage())));
    }

    @Test
    public void create_whenRolIsEmpty() throws Exception {
        mockMvc.perform(post("/roles")
                .content(objectMapper.writeValueAsString(new Rol()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem(emptyRolMessages.get(0))))
                .andExpect(jsonPath("$.errors", hasItem(emptyRolMessages.get(1))));
    }

    @Test
    public void create_whenRolHasInvalidParams() throws Exception {
        mockMvc.perform(post("/roles")
                .content(objectMapper.writeValueAsString(invalidRol))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(0))))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(1))));
    }

    /* END CREATE rolController method tests */

    /* BEGIN UPDATE rolController method tests */

    @Test
    public void update_withProperRolAndId() throws Exception {
        mockMvc.perform(put("/roles/{id}", 1)
                .content(objectMapper.writeValueAsString(rol2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.rol").exists())
                .andExpect(jsonPath("$.rol.name", is("ROL2")))
                .andExpect(jsonPath("$.rol.description", is("TEST ROL2")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.UPDATED.getMessage())));
    }

    @Test
    public void update_whenRolIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/roles/{id}", "randomString")
                .content(objectMapper.writeValueAsString(rol1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenRolIsEmpty_AndProperId() throws Exception {
        mockMvc.perform(put("/roles/{id}", 1)
                .content(objectMapper.writeValueAsString(new Rol()))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem(emptyRolMessages.get(0))))
                .andExpect(jsonPath("$.errors", hasItem(emptyRolMessages.get(1))));
    }

    @Test
    public void update_whenRolIsInvalid_AndProperId() throws Exception {
        mockMvc.perform(put("/roles/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidRol))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(0))))
                .andExpect(jsonPath("$.errors", hasItem(invalidParamsMessages.get(1))));
    }

    @Test
    public void update_whenRolIsNotFound() throws Exception {
        mockMvc.perform(put("/roles/{id}", 0)
                .content(objectMapper.writeValueAsString(rol1))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /* END UPDATE rolController method tests */

    /* BEGIN DELETE rolController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/roles/{id}", 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.DELETED.getMessage())));
    }

    @Test
    public void delete_withInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/roles/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    /* END DELETE rolController method tests */
}
