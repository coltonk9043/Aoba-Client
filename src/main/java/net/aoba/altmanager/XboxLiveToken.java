package net.aoba.altmanager;

public class XboxLiveToken {
	private final String token;
	private final String uhs;
	
	public XboxLiveToken(String token, String uhs)
	{
		this.token = token;
		this.uhs = uhs;
	}
	
	public String getToken()
	{
		return token;
	}
	
	public String getUHS()
	{
		return uhs;
	}
}
