package com.amazonaws.lambda.model;

public class VideoSegment {
	
	String text;
	String character;
	
	public VideoSegment(String text, String character) {
		this.text = text;
		this.character = character;
	}
	
	//FILL IN
	public boolean isLocal() {
		return true;
	}

	//FILL IN
	public boolean markLocal() {
		return true;
	}
	
	//FILL IN
	public boolean unMarkLocal() {
		return true;
	}
}
