package com.microservice.users.unit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.users.controllers.RolController;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.models.entity.users.Rol;
import com.microservice.users.models.services.IRolService;
import com.microservices.commons.models.services.IUtilService;
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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class RolControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IRolService rolService;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private RolController rolController;

    private List<Rol> dummyRoles;
    
    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyRolMessages = new ArrayList<>();
    private Rol invalidRol = new Rol();

    private Rol rol1;
    private Rol rol2;
    private Rol rol3;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(rolController)
                .build();

        createDummyRoles();
        setInvalidRol();
        setInvalidRolParamsMessages();
        setEmptyRolMessages();
    }

    private void createDummyRoles(){
        rol1 = new Rol("ROL1", "TEST ROL1", new Date());
        rol2 = new Rol("ROL2", "TEST ROL2", new Date());
        rol3 = new Rol("ROL3", "TEST ROL3", new Date());

        dummyRoles = Arrays.asList(rol1, rol2, rol3);
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
        invalidParamsMessages.add("The name field must have between 1 and 20 characters");
        invalidParamsMessages.add("The description field must have between 1 and 300 characters");
    }

    private void setEmptyRolMessages() {
        emptyRolMessages.add("The name field can't be empty");
        emptyRolMessages.add("The description field can't be empty");
    }

    @Test
    public void index() throws Exception {
        when(rolService.findAll()).thenReturn(dummyRoles);

        mockMvc.perform(get("/roles")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].name", is("ROL1")));

        verify(rolService, times(1)).findAll();
        verifyNoMoreInteractions(rolService);
    }

    /* BEGIN SHOW rolController method tests */

    @Test
    public void show_withProperId() throws Exception {
        when(rolService.findById(1L)).thenReturn(rol1);

        mockMvc.perform(get("/roles/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name", is("ROL1")))
                .andExpect(jsonPath("$.description", is("TEST ROL1")));

        verify(rolService, times(1)).findById(1L);
        verifyNoMoreInteractions(rolService);
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/roles/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(rolService.findById(anyLong())).thenReturn(null);
        mockMvc.perform(get("/roles/{id}", anyLong()))
                .andExpect(status().isNotFound());

        verify(rolService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(rolService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(rolService.findById(1L)).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(get("/roles/{id}", 1))
                .andExpect(status().isInternalServerError());

        verify(rolService, times(1)).findById(1L);
        verifyNoMoreInteractions(rolService);
    }

    /* END SHOW rolController method tests */

    /* BEGIN CREATE rolController method tests */

    @Test
    public void create_withProperRol() throws Exception {
        when(rolService.save(any(Rol.class))).thenReturn(rol1);

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

        verify(rolService, times(1)).save(any(Rol.class));
        verifyNoMoreInteractions(rolService);
    }

    @Test
    public void create_whenRolIsEmpty() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyRolMessages);

        mockMvc.perform(post("/roles")
                .content(objectMapper.writeValueAsString(new Rol()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem("The name field can't be empty")))
                .andExpect(jsonPath("$.errors", hasItem("The description field can't be empty")));
    }

    @Test
    public void create_whenRolHasInvalidParams() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(post("/roles")
                .content(objectMapper.writeValueAsString(invalidRol))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem("The name field must have between 1 and 20 characters")))
                .andExpect(jsonPath("$.errors", hasItem("The description field must have between 1 and 300 characters")));
    }

    @Test
    public void create_whenDBFailsThenThrowsException() throws Exception {
        when(rolService.save(any(Rol.class))).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(post("/roles")
                .content(objectMapper.writeValueAsString(rol1))
                .contentType(MediaType.APPLICATION_JSON));

        verify(rolService, times(1)).save(any(Rol.class));
        verifyNoMoreInteractions(rolService);
    }

    /* END CREATE rolController method tests */

    /* BEGIN UPDATE rolController method tests */

    @Test
    public void update_withProperRolAndId() throws Exception {
        when(rolService.findById(anyLong())).thenReturn(rol1);
        when(rolService.save(any(Rol.class))).thenReturn(rol1);

        mockMvc.perform(put("/roles/{id}", 1)
                .content(objectMapper.writeValueAsString(rol1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.rol").exists())
                .andExpect(jsonPath("$.rol.name", is("ROL1")))
                .andExpect(jsonPath("$.rol.description", is("TEST ROL1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.UPDATED.getMessage())));

        verify(rolService, times(1)).findById(anyLong());
        verify(rolService, times(1)).save(any(Rol.class));
        verifyNoMoreInteractions(rolService);
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
        when(utilService.listErrors(any())).thenReturn(emptyRolMessages);

        mockMvc.perform(put("/roles/{id}", 1)
                .content(objectMapper.writeValueAsString(new Rol()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem("The name field can't be empty")))
                .andExpect(jsonPath("$.errors", hasItem("The description field can't be empty")));
    }

    @Test
    public void update_whenRolIsInvalid_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(put("/roles/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidRol))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem("The name field must have between 1 and 20 characters")))
                .andExpect(jsonPath("$.errors", hasItem("The description field must have between 1 and 300 characters")));
    }

    @Test
    public void update_whenRolIsNotFound() throws Exception {
        when(rolService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(put("/roles/{id}", anyLong())
                .content(objectMapper.writeValueAsString(rol1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isNotFound());

        verify(rolService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(rolService);
    }

    @Test
    public void update_whenDBFailsThenThrowsException() throws Exception {
        when(rolService.save(any(Rol.class))).thenThrow(new DataAccessException("..."){});
        when(rolService.findById(anyLong())).thenReturn(rol1);

        mockMvc.perform(put("/roles/{id}", 1)
                .content(objectMapper.writeValueAsString(rol1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(rolService, times(1)).save(any(Rol.class));
        verify(rolService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(rolService);
    }

    /* END UPDATE rolController method tests */

    /* BEGIN DELETE rolController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        doNothing().when(rolService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/roles/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.DELETED.getMessage())));

        verify(rolService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(rolService);
    }

    @Test
    public void delete_withInvalidId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/roles/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_whenRolIsNotFoundThenThrowException() throws Exception {
        doThrow(new DataAccessException("..."){}).when(rolService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/roles/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(rolService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(rolService);
    }

    /* END DELETE rolController method tests */
}
