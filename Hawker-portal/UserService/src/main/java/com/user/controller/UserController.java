package com.user.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.user.AddressServiceProxy;
import com.user.model.Address;
import com.user.model.User;
import com.user.service.UserService;

@RestController
public class UserController {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserService userService;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	AddressServiceProxy addressProxy;

	// -------------------Retrieve All
	// Users---------------------------------------------
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	@HystrixCommand(fallbackMethod = "callGetAllUserData_Fallback")
	@Produces("application/json")
	public ResponseEntity<?> listAllUsers() {
		List<User> users = userService.findAllUsers();
		if (users.isEmpty()) {
			return new ResponseEntity<>(callGetAllUserData_Fallback(), HttpStatus.NO_CONTENT);
			// You many decide to return HttpStatus.NOT_FOUND
		}
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}

	// -------------------DEfault fall back method for retrieve All
	// Users---------------------------------------------
	public ResponseEntity<?> callGetAllUserData_Fallback() {
		List<User> usersList = new ArrayList<>();
		User user = new User();
		user.setName("Default_user");
		user.setId(1);
		user.setExt(0);
		return new ResponseEntity<List<User>>(usersList, HttpStatus.OK);
	}

	// -------------------Retrieve Single
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	@HystrixCommand(fallbackMethod = "retrieveSingleUserData_Fallback")
	@Produces("application/json")
	public ResponseEntity<?> getUser(@PathVariable("id") long id) {
		logger.info("getUser() - Fetching User with id {}", id);
		User user = userService.findById(id);
		if (user == null) {
			logger.error("User with id {} not found.", id);
			return new ResponseEntity<>(retrieveSingleUserData_Fallback(id), HttpStatus.NOT_FOUND);
		}
		user.setAddress(addressProxy.getUser(user.getName()));
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	public ResponseEntity<?> retrieveSingleUserData_Fallback(@PathVariable("id") long id) {
		User user = new User();
		user.setName("Default_user");
		user.setId(1);
		user.setExt(0);
		user.setAddress(new Address(String.valueOf(id), "fallback_name", 100, "fallback_county",
				"fallback_community", "fallback_state", "fallback_country", null));
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	// -------------------Create a User-------------------------------------------
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	@Produces("application/json")
	@HystrixCommand(fallbackMethod = "createUserData_Fallback")
	public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
		logger.info("createUser() - Creating User : {}", user);
		if (userService.isUserExist(user)) {
			logger.error("Unable to create. A User with name {} already exist", user.getName());
			return new ResponseEntity<>(createUserData_Fallback(user, ucBuilder), HttpStatus.CONFLICT);
		}
		userService.saveUser(user);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(user.getId()).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}

	public ResponseEntity<?> createUserData_Fallback(@RequestBody User user, UriComponentsBuilder ucBuilder) {
		User user1 = new User();
		user1.setName("Default_user");
		user1.setId(1);
		user1.setExt(0);
		return new ResponseEntity<User>(user1, HttpStatus.OK);
	}

	// ------------------- Update a User
	// ------------------------------------------------
	@RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
	@Produces("application/json")
	@HystrixCommand(fallbackMethod = "updateUserData_Fallback")
	public ResponseEntity<?> updateUser(@PathVariable("id") long id, @RequestBody User user) {
		logger.info("updateUser() - Updating User with id {}", id);

		User currentUser = userService.findById(id);
		if (currentUser == null) {
			logger.error("updateUser() - Unable to update. User with id {} not found.", id);
			return new ResponseEntity<>(updateUserData_Fallback(id, user), HttpStatus.NOT_FOUND);
		}
		currentUser.setName(user.getName());
		currentUser.setExt(user.getExt());
		userService.updateUser(currentUser);
		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

	public ResponseEntity<?> updateUserData_Fallback(@PathVariable("id") long id, @RequestBody User user) {
		User user1 = new User();
		user1.setName("Default_user");
		user1.setId(1);
		user1.setExt(0);
		return new ResponseEntity<User>(user1, HttpStatus.OK);
	}

	// ------------------- Delete a User-----------------------------------------
	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
	@Produces("application/json")
	@HystrixCommand(fallbackMethod = "deleteUserData_Fallback")
	public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
		logger.info("deleteUser() - Fetching & Deleting User with id {}", id);
		User user = userService.findById(id);
		if (user == null) {
			logger.error("deleteUser() - Unable to delete. User with id {} not found.", id);
			return new ResponseEntity<>(deleteUserData_Fallback(id), HttpStatus.NOT_FOUND);
		}
		userService.deleteUserById(id);
		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	}

	public ResponseEntity<?> deleteUserData_Fallback(@PathVariable("id") long id) {
		User user = new User();
		user.setName("Default_user");
		user.setId(1);
		user.setExt(0);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	// ------------------- Delete All Users-----------------------------
	@RequestMapping(value = "/user", method = RequestMethod.DELETE)
	@Produces("application/json")
	@HystrixCommand(fallbackMethod = "deleteAllUserData_Fallback")
	public ResponseEntity<User> deleteAllUsers() {
		logger.info("deleteAllUsers() - Deleting All Users");
		userService.deleteAllUsers();
		return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	}

	public ResponseEntity<User> deleteAllUserData_Fallback() {
		User user = new User();
		user.setName("Default_user");
		user.setId(1);
		user.setExt(0);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

}