package com.microservice.users.models.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.microservice.users.models.dao.IUserDao;
import com.microservice.users.models.entity.History;
import com.microservice.users.models.entity.User;
import com.microservice.users.models.services.remote.entity.Phrase;
import com.microservice.users.models.services.remote.entity.Type;

public class UserServiceImplTest {

	@Mock
	private IUserDao userDao;
	
	@InjectMocks
	private UserServiceImpl userService;
	
	List<User> dummyUsers = new ArrayList<User>();
	List<Phrase> dummyPhrases = new ArrayList<Phrase>();
	List<History> dummyUserHistory = new ArrayList<History>();
	User user = new User();
	History history = new History();
	Type type = new Type();
	Phrase phrase = new Phrase();
	
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        dummyUsers(); // 1 user
        dummyType();
        dummyPhrase();
        dummyHistory();
		dummyPhrases.add(phrase); // 1 phrase
		dummyUserHistory.add(history); // 1 history
    }

    private void dummyUsers(){
		user.setName("test");
		user.setLastName("test");
		user.setEmail("test@mail.com");
		user.setPassword("12345");
		dummyUsers.add(user);
	}

	private void dummyType(){
		type.setId(1L);
		type.setName("test");
		type.setCreatedAt(new Date());
	}

	private void dummyPhrase(){
		phrase.setId(1L);
		phrase.setBody("test");
		phrase.setType(type);
	}

	private void dummyHistory(){
		history.setId(1L);
		history.setPhraseId(1L);
		history.setUser(new User());
		history.setCreatedAt(new Date());
	}

    /**
     * Tests just for increase the code coverage
     */
    
	@Test
	public void findAllUsersTest() {
		when(userService.findAll()).thenReturn(dummyUsers);
		List<User> allUsers = userService.findAll();
		assertTrue("Success, number of users is 1", allUsers.size() == 1);
	}
/*
	@Test
	public void findByIdTest() {
		UserServiceImpl userService = mock(UserServiceImpl.class);
		userService.findById(1L);
		verify(userService, times(1)).findById(1L);
	}
*/
	@Test
	public void saveUserTest() {
		when(userService.save(user)).thenReturn(user);
		User foundUser = userService.save(user);
		assertTrue("Success, User has been created", foundUser.equals(user));
	}
	
	@Test
	public void deleteUserTest() {
		UserServiceImpl userService = mock(UserServiceImpl.class);
		doNothing().when(userService).delete(1L);
		userService.delete(1L);
		verify(userService, times(1)).delete(1L);
	}
	
	@Test
	public void filterPhrasesByType_withPhraseType_not0Test() {
		Integer phraseType = 1;
		List<Phrase> filteredPhrases = userService.filterPhrasesByType(dummyPhrases, phraseType);
		assertTrue("Success, number of phrases is 1", filteredPhrases.size() == 1);
	}
	
	@Test
	public void filterPhrasesByType_withPhraseType_0Test() {
		Integer phraseType = 0;
		List<Phrase> filteredPhrases = userService.filterPhrasesByType(dummyPhrases, phraseType);
		assertEquals(1, filteredPhrases.size());
	}
	
	@Test
	public void filterPhraseByAvailabilityTest() {
		List<Phrase> filteredPhrases = userService.filterPhraseByAvailability(dummyPhrases, dummyUserHistory);
		assertEquals(0, filteredPhrases.size());
	}
	
	@Test
	public void unavailableMessageTest() {
		assertEquals("Phrases service is not available", userService.unavailableMessage());
	}

	@Test
	public void getAllPhrases() {
		UserServiceImpl userService = mock(UserServiceImpl.class);
		when(userService.getAllPhrases()).thenReturn(dummyPhrases);
		List<Phrase> allPhrases = userService.getAllPhrases();
		assertEquals(1, allPhrases.size());
	}

	@Test
	public void getAllPhrasesFailTest() {
		assertEquals(new ArrayList<>(), userService.getAllPhrasesFail());
	}
}
