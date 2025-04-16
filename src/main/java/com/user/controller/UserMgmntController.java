package com.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.user.bindings.ActivateAccount;
import com.user.bindings.Login;
import com.user.bindings.User;
import com.user.service.IUserMasterService;

@RestController
public class UserMgmntController {

	@Autowired
	private IUserMasterService service;

	@PostMapping("/user")
	public ResponseEntity<String> saveUserRegistration(@RequestBody User user) {
		boolean saveUser = service.saveUser(user);
		if (saveUser) {
			return new ResponseEntity<>("Registration Success", HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>("Registration failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/activate")
	public ResponseEntity<String> activateAccount(@RequestBody ActivateAccount acc) {
		boolean activateUserAcc = service.activateUserAcc(acc);
		if (activateUserAcc) {
			return new ResponseEntity<>("Account Activated", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Invalid Temporary password", HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsers() {
		List<User> allUsers = service.getAllUsers();
		return new ResponseEntity<>(allUsers, HttpStatus.OK);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<User> getUserById(@PathVariable Integer userId) {
		User user = service.getUserById(userId);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@DeleteMapping("/user/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable Integer userId) {
		boolean deleteUserById = service.deleteUserById(userId);

		if (deleteUserById) {
			return new ResponseEntity<>("Record deleted", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Record delete Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/status/{userId}/{status}")
	public ResponseEntity<String> changeActiveStatus(@PathVariable Integer userId, String status) {
		boolean isStatusChanged = service.changeAccountStatus(userId, status);
		if (isStatusChanged) {
			return new ResponseEntity<>("Status got Changed", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Status not changed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody Login login) {
		String status = service.login(login);
		return new ResponseEntity<>(status, HttpStatus.OK);
	}

	@GetMapping("/forgotpwd/{email}")
	public ResponseEntity<String> forgotPwd(@PathVariable String email) {
		String forgotPwd = service.forgotPwd(email);
		return new ResponseEntity<>(forgotPwd, HttpStatus.OK);
	}
}
