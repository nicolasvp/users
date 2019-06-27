package com.microservice.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.users.models.entity.Likes;
import com.microservice.users.models.entity.Rol;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.ILikesService;
import com.microservice.users.models.services.IUtilService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class LikesControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ILikesService likesService;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private LikesController likesController;

    private List<Likes> dummyLikes;

    private Likes like1;
    private Likes like2;
    private Likes like3;

    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyLikeMessages = new ArrayList<>();
    private Likes invalidLike = new Likes();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(likesController)
                .build();

        createDummyLikes();
    }

    private void createDummyLikes(){
        like1 = new Likes(new User(), 1L, new Date());
        like2 = new Likes(new User(), 2L, new Date());
        like3 = new Likes(new User(), 3L, new Date());

        dummyLikes = Arrays.asList(like1, like2, like3);
        setInvalidLike();
        setInvalidLikeParamsMessages();
        setEmptyLikeMessages();
    }

    /**
     * Likes attributes with random and invalid number of characters
     * phraseId = 21 characters
     * user = 301 characters
     */
    private void setInvalidLike() {
        //invalidLike.setPhraseId(1L);
        //invalidLike.setUser();
    }

    private void setInvalidLikeParamsMessages() {
        invalidParamsMessages.add("El campo user debe tener entre 1 y 20 caracteres");
        invalidParamsMessages.add("El campo phraseId debe tener entre 1 y 300 caracteres");
    }

    private void setEmptyLikeMessages() {
        emptyLikeMessages.add("El campo user no puede estar vacío");
        emptyLikeMessages.add("El campo phraseId no puede estar vacío");
    }

    @Test
    public void index() throws Exception {
        when(likesService.findAll()).thenReturn(dummyLikes);

        mockMvc.perform(get("/api/likes")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].phraseId", is(1)));

        verify(likesService, times(1)).findAll();
        verifyNoMoreInteractions(likesService);
    }

    /* BEGIN SHOW likesController method tests */

    @Test
    public void show_withProperId() throws Exception {
        when(likesService.findById(1L)).thenReturn(like1);

        mockMvc.perform(get("/api/likes/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.phraseId", is(1)));

        verify(likesService, times(1)).findById(1L);
        verifyNoMoreInteractions(likesService);
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/api/likes/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(likesService.findById(999999L)).thenReturn(null);
        mockMvc.perform(get("/api/likes/{id}", 999999))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg", is("El registro con ID: 999999 no existe en la base de datos")))
                .andExpect(status().isNotFound());

        verify(likesService, times(1)).findById(999999L);
        verifyNoMoreInteractions(likesService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(likesService.findById(1L)).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(get("/api/likes/{id}", 1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.msg", is("Error al realizar la consulta en la base de datos")))
                .andExpect(status().isInternalServerError());

        verify(likesService, times(1)).findById(1L);
        verifyNoMoreInteractions(likesService);
    }

    /* END SHOW likesController method tests */

    /* BEGIN CREATE likesController method tests */

    @Test
    public void create_withProperLike() throws Exception {
        when(likesService.save(any(Likes.class))).thenReturn(like1);

        mockMvc.perform(post("/api/likes")
                .content(objectMapper.writeValueAsString(like1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.like").exists())
                .andExpect(jsonPath("$.phraseId", is(1)))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is("Registro creado con éxito")));

        verify(likesService, times(1)).save(any(Likes.class));
        verifyNoMoreInteractions(likesService);
    }

    @Test
    public void create_whenLikeIsEmpty() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyLikeMessages);

        mockMvc.perform(post("/api/likes")
                .content(objectMapper.writeValueAsString(new Rol()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem("El campo phraseId no puede estar vacío")))
                .andExpect(jsonPath("$.errors", hasItem("El campo user no puede estar vacío")));
    }
/*
    @Test
    public void create_whenLikeHasInvalidParams() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(post("/api/roles")
                .content(objectMapper.writeValueAsString(invalidLike))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(2)))
                .andExpect(jsonPath("$.errors", hasItem("El campo phraseId debe tener entre 1 y 20 caracteres")))
                .andExpect(jsonPath("$.errors", hasItem("El campo user debe tener entre 1 y 300 caracteres")));
    }
*/
    @Test
    public void create_whenDBFailsThenThrowsException() throws Exception {
        when(likesService.save(any(Likes.class))).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(post("/api/likes")
                .content(objectMapper.writeValueAsString(like1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.msg", is("Error al intentar guardar el registro")));

        verify(likesService, times(1)).save(any(Likes.class));
        verifyNoMoreInteractions(likesService);
    }

    /* END CREATE likesController method tests */
}
