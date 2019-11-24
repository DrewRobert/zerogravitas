package com.amazonaws.lambda.http;

public class CreatePlaylistResponse {
	public String response;
	public int httpCode;
	
	public CreatePlaylistResponse (String s, int code) {
		this.response = s;
		this.httpCode = code;
	}
	
	// 200 means success
	public CreatePlaylistResponse (String s) {
		this.response = s;
		this.httpCode = 200;
	}
	
	public String toString() {
		return "Response(" + response + ")";
	}
}
