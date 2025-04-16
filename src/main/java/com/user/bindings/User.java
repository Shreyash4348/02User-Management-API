package com.user.bindings;

import java.time.LocalDate;

import lombok.Data;

@Data
public class User {

	private String fullName;
	private String email;
	private Long mobile;
	private LocalDate DOB;
	private String gender;
	private long SSN;

}
