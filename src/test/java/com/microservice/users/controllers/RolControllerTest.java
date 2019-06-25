package com.microservice.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.users.models.entity.Rol;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.IRolService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class RolControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IRolService rolService;

    @InjectMocks
    private RolController rolController;

    private List<Rol> dummyRoles;

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
    }

    private void createDummyRoles(){
        rol1 = new Rol("ROL1", "TEST ROL1", new Date());
        rol2 = new Rol("ROL2", "TEST ROL2", new Date());
        rol3 = new Rol("ROL3", "TEST ROL3", new Date());

        dummyRoles = Arrays.asList(rol1, rol2, rol3);
    }

    @Test
    public void index() throws Exception {
        when(rolService.findAll()).thenReturn(dummyRoles);

        mockMvc.perform(get("/api/roles")
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

        mockMvc.perform(get("/api/roles/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name", is("ROL1")))
                .andExpect(jsonPath("$.description", is("TEST ROL1")));

        verify(rolService, times(1)).findById(1L);
        verifyNoMoreInteractions(rolService);
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/roles/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(rolService.findById(999999L)).thenReturn(null);
        mockMvc.perform(get("/api/roles/{id}", 999999))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg", is("El registro con ID: 999999 no existe en la base de datos")))
                .andExpect(status().isNotFound());

        verify(rolService, times(1)).findById(999999L);
        verifyNoMoreInteractions(rolService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(rolService.findById(1L)).thenThrow(new DataAccessException("..."){});
        mockMvc.perform(get("/api/roles/{id}", 1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg", is("Error al realizar la consulta en la base de datos")))
                .andExpect(status().isInternalServerError());

        verify(rolService, times(1)).findById(1L);
        verifyNoMoreInteractions(rolService);
    }

    /* END SHOW rolController method tests */

}
