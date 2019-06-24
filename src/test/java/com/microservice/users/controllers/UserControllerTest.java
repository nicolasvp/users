package com.microservice.users.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.IUserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest  {

    // Simulate HTTP requests
    private MockMvc mockMvc;

    // Transform objects to json and json to objects
    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    private List<User> dummyUsers;

    private User user1 = new User();
    private User user2 = new User();
    private User user3 = new User();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
        createDummyUsers();
    }

    private void createDummyUsers(){
        user1.setId(1L);
        user1.setName("USER 1");
        user1.setEmail("EMAIL 1");

        user2.setId(2L);
        user2.setName("USER 2");
        user2.setEmail("EMAIL 2");

        user3.setId(3L);
        user3.setName("USER 3");
        user3.setEmail("EMAIL 3");

        dummyUsers = Arrays.asList(user1, user2, user3);
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
    public void show() throws Exception {
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
}
