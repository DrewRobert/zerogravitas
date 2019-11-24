package com.amazonaws.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.amazonaws.lambda.db.PlaylistDAO;
import com.amazonaws.lambda.http.GetPlaylistsResponse;
import com.amazonaws.lambda.model.Playlist;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class ListPlaylistsHandler implements RequestHandler<Object,GetPlaylistsResponse> {

	public LambdaLogger logger;

	/** Load from RDS, if it exists
	 * 
	 * @throws Exception 
	 */
	List<Playlist> getPlaylists() throws Exception {
		logger.log("in getPlaylists");
		PlaylistDAO dao = new PlaylistDAO();
		
		return dao.getAllPlaylists();
	}
	
	// I am leaving in this S3 code so it can be a LAST RESORT if the constant is not in the database
	private AmazonS3 s3 = null;
	
	/**
	 * Retrieve all SYSTEM constants. This code is surprisingly dangerous since there could
	 * be an incredible number of objects in the bucket. Ignoring this for now.
	 */
	List<Playlist> systemPlaylists() throws Exception {
		logger.log("in systemPlaylists");
		if (s3 == null) {
			logger.log("attach to S3 request");
			s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
			logger.log("attach to S3 succeed");
		}
		ArrayList<Playlist> sysPlaylists = new ArrayList<>();
	    
		// retrieve listing of all objects in the designated bucket
		ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
				  .withBucketName("3733zerogravitas")    // top-level bucket
				  .withPrefix("playlists");       // sub-folder declarations here (i.e., a/b/c)
												  
		
		// request the s3 objects in the s3 bucket 'cs3733wpi/playlists' -- change based on your bucket name
		logger.log("process request");
		ListObjectsV2Result result = s3.listObjectsV2(listObjectsRequest);
		logger.log("process request succeeded");
		List<S3ObjectSummary> objects = result.getObjectSummaries();
		
		for (S3ObjectSummary os: objects) {
	      String name = os.getKey();
		  logger.log("S3 found:" + name);

	      // If name ends with slash it is the 'constants/' bucket itself so you skip
	      if (name.endsWith("/")) { continue; }
			
	      S3Object obj = s3.getObject("3733zerogravitas", name);
	    	
	    	try (S3ObjectInputStream playlistStream = obj.getObjectContent()) {
				Scanner sc = new Scanner(playlistStream);
				String val = sc.nextLine();
				sc.close();
				
				// just grab name *after* the slash. Note this is a SYSTEM constant
				int postSlash = name.indexOf('/');
				sysPlaylists.add(new Playlist(name.substring(postSlash+1)));
			} catch (Exception e) {
				logger.log("Unable to parse contents of " + name);
			}
	    }
		
		return sysPlaylists;
	}
	
	@Override
	public GetPlaylistsResponse handleRequest(Object input, Context context)  {
		logger = context.getLogger();
		logger.log("Loading Java Lambda handler to list all playlist");

		GetPlaylistsResponse response;
		try {
			// get all user defined constants AND system-defined constants.
			// Note that user defined constants override system-defined constants.
			List<Playlist> list = getPlaylists();
			for (Playlist c : systemPlaylists()) {
				if (!list.contains(c)) {
					list.add(c);
				}
			}
			response = new GetPlaylistsResponse(list, 200);
		} catch (Exception e) {
			response = new GetPlaylistsResponse(403, e.getMessage());
		}
		
		return response;
	}
}