package com.amazonaws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

import java.io.ByteArrayInputStream;

import com.amazonaws.lambda.db.PlaylistDAO;
import com.amazonaws.lambda.http.CreatePlaylistRequest;
import com.amazonaws.lambda.http.CreatePlaylistResponse;
import com.amazonaws.lambda.model.Playlist;
import com.amazonaws.lambda.model.VideoSegment;
import com.amazonaws.regions.Regions;

public class CreatePlaylistHandler implements RequestHandler<CreatePlaylistRequest, CreatePlaylistResponse> {

	LambdaLogger logger;
	
	// To access S3 storage
	private AmazonS3 s3 = null;
	
	boolean createPlaylist(String name) throws Exception {
		if (logger != null) { logger.log("in createPlaylist"); }
		PlaylistDAO dao = new PlaylistDAO();
		
		// check if present
		Playlist exist = dao.getPlaylist(name);
		Playlist playlist = new Playlist(name);
		if (exist == null) {
			return dao.addPlaylist(playlist);
		} else {
			return false;
		}
	}
	
	/** Create S3 bucket
	 * 
	 * @throws Exception 
	 */
	boolean createSystemPlaylist(String name, byte[]  contents) throws Exception {
		if (logger != null) { logger.log("in createSystemPlaylist"); }
		
		if (s3 == null) {
			logger.log("attach to S3 request");
			s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
			logger.log("attach to S3 succeed");
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(contents);
		ObjectMetadata omd = new ObjectMetadata();
		omd.setContentLength(contents.length);
		
		PutObjectResult res = s3.putObject(new PutObjectRequest("3733zerogravitas", "playlists/" + name, bais, omd));
		
		// if we ever get here, then whole thing was stored
		return true;
	}
	
	@Override 
	public CreatePlaylistResponse handleRequest(CreatePlaylistRequest req, Context context)  {
		logger = context.getLogger();
		logger.log(req.toString());
		
		CreatePlaylistResponse response;
		try {
			byte[] encoded = java.util.Base64.getDecoder().decode(req.base64EncodedValue);
			if (req.system) {
				if (createSystemPlaylist(req.name, encoded)) {
					response = new CreatePlaylistResponse(req.name);
				} else {
					response = new CreatePlaylistResponse(req.name, 422);
				}
			} else {
				String contents = new String(encoded);
				//double value = Double.valueOf(contents);
				
				if (createPlaylist(req.name)) {
					response = new CreatePlaylistResponse(req.name);
				} else {
					response = new CreatePlaylistResponse(req.name, 422);
				}
			}
		} catch (Exception e) {
			response = new CreatePlaylistResponse("Unable to create playlist: " + req.name + "(" + e.getMessage() + ")", 400);
		}

		return response;
	}

	/*
    public CreatePlaylistHandler() {}

    // Test purpose only.
    CreatePlaylistHandler(AmazonS3 s3) {
        this.s3 = s3;
    }
    

    @Override
    public String handleRequest(S3Event event, Context context) {
        context.getLogger().log("Received event: " + event);

        // Get the object from the event and show its content type
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        try {
            S3Object response = s3.getObject(new GetObjectRequest(bucket, key));
            String contentType = response.getObjectMetadata().getContentType();
            context.getLogger().log("CONTENT TYPE: " + contentType);
            return contentType;
        } catch (Exception e) {
            e.printStackTrace();
            context.getLogger().log(String.format(
                "Error getting object %s from bucket %s. Make sure they exist and"
                + " your bucket is in the same region as this function.", key, bucket));
            throw e;
        }
    }
    */
}