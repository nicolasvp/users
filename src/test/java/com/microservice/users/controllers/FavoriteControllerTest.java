package com.microservice.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.users.models.entity.Favorite;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.IFavoriteService;
import com.microservice.users.models.services.IUtilService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FavoriteControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private IFavoriteService favoriteService;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private FavoriteController favoriteController;

    private List<Favorite> dummyFavorites;

    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyFavoriteMessages = new ArrayList<>();

    private Favorite favorite1;
    private Favorite favorite2;
    private Favorite favorite3;

    private void createDummyFavorites(){
        favorite1 = new Favorite(new User(), 1L, new Date());
        favorite2 = new Favorite(new User(), 2L, new Date());
        favorite3 = new Favorite(new User(), 3L, new Date());

        dummyFavorites = Arrays.asList(favorite1, favorite2, favorite3);
    }

    private void setInvalidFavoriteParamsMessages() {
        invalidParamsMessages.add("El campo user debe tener entre 1 y 20 caracteres");
    }

    private void setEmptyFavoriteMessages() {
        emptyFavoriteMessages.add("El campo user no puede estar vacío");
        emptyFavoriteMessages.add("El campo phraseId no puede estar vacío");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(favoriteController)
                .build();

        createDummyFavorites();
        setInvalidFavoriteParamsMessages();
        setEmptyFavoriteMessages();
    }

    @Test
    public void index() throws Exception {
        when(favoriteService.findAll()).thenReturn(dummyFavorites);

        mockMvc.perform(get("/api/favorities")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].phraseId", is(1)));

        verify(favoriteService, times(1)).findAll();
        verifyNoMoreInteractions(favoriteService);
    }

    /* BEGIN SHOW favoriteController method tests */

    @Test
    public void show_withProperId() throws Exception {
        when(favoriteService.findById(1L)).thenReturn(favorite1);

        mockMvc.perform(get("/api/favorities/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.phraseId", is(1)));

        verify(favoriteService, times(1)).findById(1L);
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/favorities/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(favoriteService.findById(999999L)).thenReturn(null);
        mockMvc.perform(get("/api/favorities/{id}", 999999))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg", is("El registro con ID: 999999 no existe en la base de datos")))
                .andExpect(status().isNotFound());

        verify(favoriteService, times(1)).findById(999999L);
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(favoriteService.findById(1L)).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(get("/api/favorities/{id}", 1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg", is("Error al realizar la consulta en la base de datos")))
                .andExpect(status().isInternalServerError());

        verify(favoriteService, times(1)).findById(1L);
        verifyNoMoreInteractions(favoriteService);
    }

    /* END SHOW favoriteController method tests */

    /* BEGIN CREATE favoriteController method tests */

    @Test
    public void create_withProperFavorite() throws Exception {
        when(favoriteService.save(any(Favorite.class))).thenReturn(favorite1);

        mockMvc.perform(post("/api/favorities")
                .content(objectMapper.writeValueAsString(favorite1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.favorite").exists())
                .andExpect(jsonPath("$.favorite.phraseId", is(1)))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro creado con éxito")));

        verify(favoriteService, times(1)).save(any(Favorite.class));
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    public void create_whenFavoriteIsEmpty() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyFavoriteMessages);

        mockMvc.perform(post("/api/favorities")
                .content(objectMapper.writeValueAsString(new Favorite()))
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
        when(favoriteService.save(any(Favorite.class))).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(post("/api/favorities")
                .content(objectMapper.writeValueAsString(favorite1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg", is("Error al intentar guardar el registro")));

        verify(favoriteService, times(1)).save(any(Favorite.class));
        verifyNoMoreInteractions(favoriteService);
    }

    /* END CREATE favoriteController method tests */

    /* BEGIN UPDATE favoriteController method tests */

    @Test
    public void update_withProperFavoriteAndId() throws Exception {
        when(favoriteService.findById(anyLong())).thenReturn(favorite1);
        when(favoriteService.save(any(Favorite.class))).thenReturn(favorite1);

        mockMvc.perform(put("/api/favorities/{id}", 1)
                .content(objectMapper.writeValueAsString(favorite1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.favorite").exists())
                .andExpect(jsonPath("$.favorite.phraseId", is(1)))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro actualizado con éxito")));

        verify(favoriteService, times(1)).findById(anyLong());
        verify(favoriteService, times(1)).save(any(Favorite.class));
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    public void update_whenFavoriteIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/api/favorities/{id}", "randomString")
                .content(objectMapper.writeValueAsString(favorite1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenFavoriteIsEmpty_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyFavoriteMessages);

        mockMvc.perform(put("/api/favorities/{id}", 1)
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
    public void update_whenFavoriteIsNotFound() throws Exception {
        when(favoriteService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(put("/api/favorities/{id}", anyLong())
                .content(objectMapper.writeValueAsString(favorite1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("El registro no existe en la base de datos")));

        verify(favoriteService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    public void update_whenDBFailsThenThrowsException() throws Exception {
        when(favoriteService.save(any(Favorite.class))).thenThrow(new DataAccessException("..."){});
        when(favoriteService.findById(anyLong())).thenReturn(favorite1);

        mockMvc.perform(put("/api/favorities/{id}", 1)
                .content(objectMapper.writeValueAsString(favorite1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg", is("Error al intentar actualizar el registro en la base de datos")))
                .andExpect(status().isInternalServerError());

        verify(favoriteService, times(1)).save(any(Favorite.class));
        verify(favoriteService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(favoriteService);
    }

    /* END UPDATE favoriteController method tests */

    /* BEGIN DELETE favoriteController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        doNothing().when(favoriteService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorities/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro eliminado con éxito")));

        verify(favoriteService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(favoriteService);
    }

    @Test
    public void delete_withInvalidId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorities/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_whenUserIsNotFoundThenThrowException() throws Exception {
        doThrow(new DataAccessException("..."){}).when(favoriteService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/favorities/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg", is("Error al intentar eliminar el registro de la base de datos")))
                .andExpect(status().isInternalServerError());

        verify(favoriteService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(favoriteService);
    }

    /* END DELETE favoriteController method tests */
}
