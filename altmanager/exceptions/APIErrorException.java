package net.aoba.altmanager.exceptions;

public class APIErrorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1133030777452596952L;
	
	
	private final String errorMessage;
	
	public APIErrorException(final String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}
	
	public final String getErrorMessage() {
		return this.errorMessage;
	}
	
}
