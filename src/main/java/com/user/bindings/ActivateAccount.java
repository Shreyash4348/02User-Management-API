package com.user.bindings;

import lombok.Data;

@Data
public class ActivateAccount {

	private String gmail;
	private String tempPwd;
	private String newPwd;
	private String confirmPwd;
}
