package com.amazonaws.lambda.http;

public class DeletePlaylistRequest {
	public String name;
	
	public void setName(String name) {this.name = name; }
	public String getName() {return name; }
	
	public DeletePlaylistRequest (String n) {
		this.name = n;
	}

	public DeletePlaylistRequest() {
		
	}
	
	public String toString() {
		return "Delete(" + name + ")";
	}
}
