package org.jcvi.annotation.dao;

public class DaoException extends RuntimeException {

	public DaoException() {
	}

	public DaoException(String message) {
		super(message);
	}

	// exception chaining
	public DaoException(Throwable cause) {
		super(cause);
	}
	
	public DaoException(String message, Throwable cause) {
		super(message, cause);
	}

}
