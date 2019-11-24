package com.amazonaws.lambda;

import com.amazonaws.lambda.db.PlaylistDAO;
import com.amazonaws.lambda.http.DeletePlaylistRequest;
import com.amazonaws.lambda.http.DeletePlaylistResponse;
import com.amazonaws.lambda.model.Playlist;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class DeletePlaylistHandler implements RequestHandler<DeletePlaylistRequest,DeletePlaylistResponse> {

	public LambdaLogger logger = null;

	@Override
	public DeletePlaylistResponse handleRequest(DeletePlaylistRequest req, Context context) {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler to delete");

		DeletePlaylistResponse response = null;
		logger.log(req.toString());

		PlaylistDAO dao = new PlaylistDAO();

		Playlist playlist = new Playlist(req.name);
		try {
			if (dao.deletePlaylist(playlist)) {
				response = new DeletePlaylistResponse(req.name, 200);
			} else {
				response = new DeletePlaylistResponse(req.name, 422, "Unable to delete playlist.");
			}
		} catch (Exception e) {
			response = new DeletePlaylistResponse(req.name, 403, "Unable to delete playlist: " + req.name + "(" + e.getMessage() + ")");
		}

		return response;
	}
}