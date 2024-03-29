package com.amazonaws.lambda.http;

public class CreatePlaylistRequest {
	public String name;
	public String base64EncodedValue;
	public boolean system;
	
	public String getName( ) { return name; }
	public void setName(String name) { this.name = name; }
	
	public boolean getSystem( ) { return system; }
	public void setSystem(boolean system) { this.system = system; }
	
	public String getBase64EncodedValue() { return base64EncodedValue; }
	public void setBase64EncodedValue(String base64EncodedValue) { this.base64EncodedValue = base64EncodedValue; }
	
	public CreatePlaylistRequest() {
	}
	
	public CreatePlaylistRequest (String n, String encoding) {
		this.name = n;
		this.base64EncodedValue = encoding;
	}
	
	public CreatePlaylistRequest (String n, String encoding, boolean system) {
		this.name = n;
		this.base64EncodedValue = encoding;
		this.system = system;
	}
	
	public String toString() {
		return "CreatePlaylist(" + name + "," + base64EncodedValue + ")";
	}
}
