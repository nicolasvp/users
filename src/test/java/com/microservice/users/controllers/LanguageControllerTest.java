package com.microservice.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.models.entity.users.Config;
import com.microservices.commons.models.entity.users.Language;
import com.microservices.commons.models.entity.users.Rol;
import com.microservice.users.models.services.ILanguageService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LanguageControllerTest {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ILanguageService languageService;

    @Mock
    private IUtilService utilService;

    @InjectMocks
    private LanguageController languageController;

    private List<Language> dummyLanguages;

    private List<String> invalidParamsMessages = new ArrayList<>();
    private List<String> emptyLanguageMessages = new ArrayList<>();
    private Language invalidLanguage = new Language();

    private Language language1;
    private Language language2;
    private Language language3;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(languageController)
                .build();

        createDummyLanguages();
        setInvalidLanguage();
        setInvalidLanguageParamsMessages();
        setEmptyLanguageMessages();
    }

    private void createDummyLanguages(){
        language1 = new Language("Language1", new Config(), new Date());
        language2 = new Language("Language2", new Config(), new Date());
        language3 = new Language("Language3", new Config(), new Date());

        dummyLanguages = Arrays.asList(language1, language2, language3);
    }

    /**
     * Language attributes with random and invalid number of characters
     * name = 21 characters
     */
    private void setInvalidLanguage() {
        invalidLanguage.setName("BptgmU0f2RctROCER4TZB");
    }

    private void setInvalidLanguageParamsMessages() {
        invalidParamsMessages.add("The name field must have between 1 and 20 characters");
    }

    private void setEmptyLanguageMessages() {
        emptyLanguageMessages.add("The name field can't be empty");
    }

    @Test
    public void index() throws Exception {
        when(languageService.findAll()).thenReturn(dummyLanguages);

        mockMvc.perform(get("/languages")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$.[0].name", is("Language1")));

        verify(languageService, times(1)).findAll();
        verifyNoMoreInteractions(languageService);
    }

    /* BEGIN SHOW languageController method tests */

    @Test
    public void show_withProperId() throws Exception {
        when(languageService.findById(1L)).thenReturn(language1);

        mockMvc.perform(get("/languages/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.name", is("Language1")));

        verify(languageService, times(1)).findById(1L);
        verifyNoMoreInteractions(languageService);
    }

    @Test
    public void show_whenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/languages/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void show_whenRecordDoesnotExist() throws Exception {
        when(languageService.findById(anyLong())).thenReturn(null);
        mockMvc.perform(get("/languages/{id}", anyLong()))
                .andExpect(status().isNotFound());

        verify(languageService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(languageService);
    }

    @Test
    public void show_whenDBFailsThenThrowsException() throws Exception {
        when(languageService.findById(1L)).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(get("/languages/{id}", 1))
                .andExpect(status().isInternalServerError());

        verify(languageService, times(1)).findById(1L);
        verifyNoMoreInteractions(languageService);
    }

    /* END SHOW languageController method tests */

    /* BEGIN CREATE languageController method tests */

    @Test
    public void create_withProperLanguage() throws Exception {
        when(languageService.save(any(Language.class))).thenReturn(language1);
        
        mockMvc.perform(post("/languages")
                .content(objectMapper.writeValueAsString(language1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.language").exists())
                .andExpect(jsonPath("$.language.name", is("Language1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.CREATED.getMessage())));

        verify(languageService, times(1)).save(any(Language.class));
        verifyNoMoreInteractions(languageService);
    }

    @Test
    public void create_whenLanguageIsEmpty() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyLanguageMessages);

        mockMvc.perform(post("/languages")
                .content(objectMapper.writeValueAsString(new Language()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("The name field can't be empty")));
    }

    @Test
    public void create_whenLanguageHasInvalidParams() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(post("/languages")
                .content(objectMapper.writeValueAsString(invalidLanguage))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("The name field must have between 1 and 20 characters")));
    }

    @Test
    public void create_whenDBFailsThenThrowsException() throws Exception {
        when(languageService.save(any(Language.class))).thenThrow(new DataAccessException("..."){});

        mockMvc.perform(post("/languages")
                .content(objectMapper.writeValueAsString(language1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(languageService, times(1)).save(any(Language.class));
        verifyNoMoreInteractions(languageService);
    }

    /* END CREATE rolController method tests */

    /* BEGIN UPDATE languageController method tests */

    @Test
    public void update_withProperLanguageAndId() throws Exception {
        when(languageService.findById(anyLong())).thenReturn(language1);
        when(languageService.save(any(Language.class))).thenReturn(language1);
        
        mockMvc.perform(put("/languages/{id}", 1)
                .content(objectMapper.writeValueAsString(language1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.language").exists())
                .andExpect(jsonPath("$.language.name", is("Language1")))
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.UPDATED.getMessage())));

        verify(languageService, times(1)).findById(anyLong());
        verify(languageService, times(1)).save(any(Language.class));
        verifyNoMoreInteractions(languageService);
    }

    @Test
    public void update_whenLanguageIsProper_andInvalidId() throws Exception {
        mockMvc.perform(put("/languages/{id}", "randomString")
                .content(objectMapper.writeValueAsString(language1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update_whenLanguageIsEmpty_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(emptyLanguageMessages);

        mockMvc.perform(put("/languages/{id}", 1)
                .content(objectMapper.writeValueAsString(new Rol()))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("The name field can't be empty")));
    }

    @Test
    public void update_whenLanguageIsInvalid_AndProperId() throws Exception {
        when(utilService.listErrors(any())).thenReturn(invalidParamsMessages);

        mockMvc.perform(put("/languages/{id}", 1)
                .content(objectMapper.writeValueAsString(invalidLanguage))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors", hasItem("The name field must have between 1 and 20 characters")));
    }

    @Test
    public void update_whenLanguageIsNotFound() throws Exception {
        when(languageService.findById(anyLong())).thenReturn(null);

        mockMvc.perform(put("/languages/{id}", anyLong())
                .content(objectMapper.writeValueAsString(language1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isNotFound());

        verify(languageService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(languageService);
    }

    @Test
    public void update_whenDBFailsThenThrowsException() throws Exception {
        when(languageService.save(any(Language.class))).thenThrow(new DataAccessException("..."){});
        when(languageService.findById(anyLong())).thenReturn(language1);

        mockMvc.perform(put("/languages/{id}", 1)
                .content(objectMapper.writeValueAsString(language1))
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(languageService, times(1)).save(any(Language.class));
        verify(languageService, times(1)).findById(anyLong());
        verifyNoMoreInteractions(languageService);
    }

    /* END UPDATE languageController method tests */

    /* BEGIN DELETE languageController method tests */

    @Test
    public void delete_withProperId() throws Exception {
        doNothing().when(languageService).delete(anyLong());
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/languages/{id}", 1))
        		.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").exists())
                .andExpect(jsonPath("$.msg", is(CrudMessagesEnum.DELETED.getMessage())));

        verify(languageService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(languageService);
    }

    @Test
    public void delete_withInvalidId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/languages/{id}", "randomString"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void delete_whenRolIsNotFoundThenThrowException() throws Exception {
        doThrow(new DataAccessException("..."){}).when(languageService).delete(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/languages/{id}", anyLong())
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isInternalServerError());

        verify(languageService, times(1)).delete(anyLong());
        verifyNoMoreInteractions(languageService);
    }

    /* END DELETE languageController method tests */
}
