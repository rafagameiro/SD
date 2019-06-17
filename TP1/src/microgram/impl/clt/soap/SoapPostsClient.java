package microgram.impl.clt.soap;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.api.soap.SoapPosts;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class SoapPostsClient extends SoapClient implements Posts {

	SoapPosts impl;
	private static final String WSDL = "?wsdl";
	private static final String POSTS = "/posts";
	private static Logger Log = Logger.getLogger(SoapPostsClient.class.getName());
	private String serverUri;

	public SoapPostsClient(URI serverUri) {
		super(serverUri);
		this.serverUri = serverUri.toString() + POSTS;
	}

	private SoapPosts impl() {

		QName QNAME = new QName(SoapPosts.NAMESPACE, SoapPosts.NAME);

		if (impl == null) {
			try {
				Service service = Service.create(new URL(serverUri + WSDL), QNAME);
				impl = service.getPort(microgram.api.soap.SoapPosts.class);

			} catch (MalformedURLException e) {
				Log.info("MalformedURL Exception, reason: " + e.getMessage());
				e.printStackTrace();
			}

		}
		return impl;
	}
	
	@Override
	public Result<Post> getPost(String postId) {
		return super.tryCatchResult(() -> impl().getPost(postId));
	}

	@Override
	public Result<String> createPost(Post post) {
		return super.tryCatchResult(() -> impl().createPost(post));
	}

	@Override
	public Result<Void> deletePost(String postId) {

		return super.tryCatchVoid(() -> impl().deletePost(postId));
	}

	@Override
	public Result<List<String>> getPosts(String userId) {

		return super.tryCatchResult(() -> impl().getPosts(userId));
	}

	@Override
	public Result<Void> deleteUserPosts(String userId) {

		return super.tryCatchVoid(() -> impl().deleteUserPosts(userId));
	}

	@Override
	public Result<Integer> getNumPosts(String userId) {

		return super.tryCatchResult(() -> impl().getNumPosts(userId));
	}

	@Override
	public Result<List<String>> getFeed(String userId) {

		return super.tryCatchResult(() -> impl().getFeed(userId));
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {

		return super.tryCatchVoid(() -> impl().like(postId, userId, isLiked));
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {

		return super.tryCatchResult(() -> impl().isLiked(postId, userId));
	}

}
