package com.user.service;

import java.util.List;

import com.user.bindings.ActivateAccount;
import com.user.bindings.Login;
import com.user.bindings.User;

public interface IUserMasterService {

	public boolean saveUser (User user);// first we need to save data of user then we will send g-mail to the user with temporary password
	
	public boolean activateUserAcc(ActivateAccount actAccount); //then user can set his/her password and login
	
	public List<User> getAllUsers(); //We will get all the users
	
	public User getUserById(Integer userId); //if admin wants to change data of the user
	
	public boolean deleteUserById(Integer userId); //admin can delete any particular account
	
	public boolean changeAccountStatus(Integer userId, String accStatus); // we can change the account status by using id and status
	
	public String login(Login login); //String is return type because i need to send reason if login is failed due to what reason
	
	public String forgotPwd (String email);

	
}
