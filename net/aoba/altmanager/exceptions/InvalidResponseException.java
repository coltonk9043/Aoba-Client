package net.aoba.altmanager.exceptions;

public class InvalidResponseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4593254916052579608L;
	
	
	private final String response;
	
	public InvalidResponseException(final String response) {
		super(response);
		this.response = response;
	}
	
	public String getResponse() {
		return this.response;
	}

}
