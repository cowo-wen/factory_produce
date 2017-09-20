package com.app.exception;

import org.springframework.security.authentication.AccountStatusException;

public class LoginAccountStatusException extends AccountStatusException {

	public LoginAccountStatusException(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 6797960225396382725L;

}
