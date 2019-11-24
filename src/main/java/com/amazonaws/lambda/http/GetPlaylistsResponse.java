package com.amazonaws.lambda.http;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.lambda.model.Playlist;

public class GetPlaylistsResponse {
	public final List<Playlist> list;
	public final int statusCode;
	public final String error;
	
	public GetPlaylistsResponse (List<Playlist> list, int code) {
		this.list = list;
		this.statusCode = code;
		this.error = "";
	}
	
	public GetPlaylistsResponse (int code, String errorMessage) {
		this.list = new ArrayList<Playlist>();
		this.statusCode = code;
		this.error = errorMessage;
	}
	
	public String toString() {
		if (list == null) { return "EmptyPlaylists"; }
		return "AllPlaylists(" + list.size() + ")";
	}
}
