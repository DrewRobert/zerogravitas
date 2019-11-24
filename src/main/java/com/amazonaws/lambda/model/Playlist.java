package com.amazonaws.lambda.model;

import java.util.Iterator;

public class Playlist {
	public String name;
	public VideoSegment videos;
	boolean system;      // when TRUE this is actually stored in S3 bucket
	
	public Playlist (String name) {
		this.name = name;
	}
	
	public Playlist (String name, VideoSegment videos) {
		this.name = name;
		this.videos = videos;
	}
	
	public Playlist (String name, VideoSegment videos, boolean system) {
		this.name = name;
		this.videos = videos;
		this.system = system;
	}
	
	//FILL IN
	public boolean addSegment(VideoSegment segment) {
		return true;
	}
	
	public String getName() {
		return name;
	}
	
	public VideoSegment getVideos() {
		return videos;
	}
	
}