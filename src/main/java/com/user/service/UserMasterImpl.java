package com.user.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.user.bindings.ActivateAccount;
import com.user.bindings.Login;
import com.user.bindings.User;
import com.user.entity.UserMaster;
import com.user.entity.UserMasterRepo;
import com.user.utils.EmailUtils;

@Service
public class UserMasterImpl implements IUserMasterService {

	@Autowired
	private UserMasterRepo userRepo;

	@Autowired
	private EmailUtils emailUtils;

	@Override
	public boolean saveUser(User user) {

		UserMaster entity = new UserMaster();
		BeanUtils.copyProperties(user, entity);

		entity.setPassword(generateRandomPassword());
		entity.setAccStatus("In-Active");
		String filename = "Email-Body.txt";
		String body = readEmailBody(entity.getFullName(), entity.getPassword(), filename);//just for writing body of the email
		String subject = "User registration uccess";
		emailUtils.sendEmailMessage(user.getEmail(), subject, body);//this method is responsible for sending mail to the user

		UserMaster save = userRepo.save(entity);

		return save.getUserId() != null;
	}

	@Override
	public boolean activateUserAcc(ActivateAccount actAccount) {

		UserMaster entity = new UserMaster();
		entity.setEmail(actAccount.getGmail());
		entity.setPassword(actAccount.getTempPwd());

		// select * from user_master where email = ? and pwd = ?;
		Example<UserMaster> of = Example.of(entity);

		List<UserMaster> findAll = userRepo.findAll(of);

		if (findAll.isEmpty()) {
			return false;
		} else {
			UserMaster userMaster = findAll.get(0);// Getting first record from the List
			userMaster.setPassword(actAccount.getNewPwd());
			userMaster.setAccStatus("Active");
			userRepo.save(userMaster);
			return true;
		}
	}

	@Override
	public List<User> getAllUsers() {

		List<UserMaster> findAll = userRepo.findAll();

		List<User> users = new ArrayList<>();

		for (UserMaster entity : findAll) {
			User user = new User();
			BeanUtils.copyProperties(entity, user);
			users.add(user);
		}
		return users;
	}

	@Override
	public User getUserById(Integer userId) {

		Optional<UserMaster> byId = userRepo.findById(userId);
		if (byId.isPresent()) {
			User user = new User();
			UserMaster userMaster = byId.get();
			BeanUtils.copyProperties(userMaster, user);
			return user;
		}
		return null;
	}

	@Override
	public boolean deleteUserById(Integer userId) {
		try {
			userRepo.deleteById(userId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean changeAccountStatus(Integer userId, String accStatus) {

		Optional<UserMaster> findById = userRepo.findById(userId);
		if (findById.isPresent()) {
			UserMaster userMaster = findById.get();
			userMaster.setAccStatus(accStatus);
			userRepo.save(userMaster);
			return true;
		}
		return false;
	}

	@Override
	public String login(Login login) {

		UserMaster entity = new UserMaster();
		entity.setEmail(login.getEmail()); // if use method findByEmailAndPassword no need to write this and line number
											// 122
		entity.setPassword(login.getPassword());

		Example<UserMaster> of = Example.of(entity); // Also we can write findByEmailAndPassword(String email, String
														// password) in JPA interface and call that here
		List<UserMaster> findAll = userRepo.findAll(of);
		if (findAll.isEmpty()) { // this conditions are necessary and condition will look like (entity == null)
			return "Invalid Credentials";
		} else {
			UserMaster userMaster = findAll.get(0);
			if (userMaster.getAccStatus().equals("Active")) {
				return "Login Success";
			} else {
				return "Your Account is Not Activated";
			}
		}

	}

	@Override
	public String forgotPwd(String email) {

		UserMaster entity = userRepo.findByEmail(email);
		if (entity == null) {
			return "Invalid E-mail Id";
		}

		String subject = "Forgot password recovery";
		String filename = "RECOVER_PWD.txt";
		String body = readEmailBody(entity.getFullName(), entity.getPassword(), filename);
		boolean isMailSent = emailUtils.sendEmailMessage(email, subject, body);
		if (isMailSent) {
			return "Password sent to your registered email successfully";
		} else {
			return "Enter valid email to recover password";
		}

	}

	private String generateRandomPassword() // not an overridden method, just to generate random pwd and pass to the
											// saveUser method
	{
		String upperAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowerAlphabet = "abcdefghijklmnopqrstuvwxyz";
		String numbers = "0123456789";
		String alphaNumeric = upperAlphabet + lowerAlphabet + numbers;

		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		int length = 6;
		for (int i = 0; i < length; i++) {
			int index = random.nextInt(alphaNumeric.length());
			char randomChar = alphaNumeric.charAt(index);
			sb.append(randomChar);
		}
		return sb.toString();
	}

	private String readEmailBody(String fullname, String pwd, String filename) {
		String url = "";
		String mailBody = null;
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			StringBuffer buffer = new StringBuffer();

			String line = br.readLine();
			while (line != null) {
				buffer.append(line);
				line = br.readLine();
			}
			br.close();
			mailBody = buffer.toString();
			mailBody = mailBody.replace("{FULLNAME}", fullname);
			mailBody = mailBody.replace("{TEMP-PWD}", pwd);
			mailBody = mailBody.replace("{URL}", url);
			mailBody = mailBody.replace("{PWD}", pwd);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mailBody;

	}

}
