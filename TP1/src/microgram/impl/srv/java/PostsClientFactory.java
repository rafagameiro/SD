package microgram.impl.srv.java;

import java.net.URI;

import microgram.api.java.Posts;
import microgram.impl.clt.java.RetryPostsClient;
import microgram.impl.clt.rest.RestPostsClient;
import microgram.impl.clt.soap.SoapPostsClient;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class PostsClientFactory {

	private static final String REST = "/rest";
	private static final String SOAP = "/soap";

	public static Posts getPostsClient(URI uri) {

		Posts posts = null;
		String uriString = uri.toString();
		if (uriString.endsWith(REST))
			posts = new RestPostsClient(uri);
		else if (uriString.endsWith(SOAP))
			posts = new SoapPostsClient(uri);

		if (posts == null)
			throw new RuntimeException("Unknown service type..." + uri);

		return new RetryPostsClient(posts);
	}
}
