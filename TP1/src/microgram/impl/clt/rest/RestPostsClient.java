package microgram.impl.clt.rest;

import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.api.rest.RestPosts;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class RestPostsClient extends RestClient implements Posts {

	public RestPostsClient(URI serverUri) {
		super(serverUri, RestPosts.PATH);
	}

	@Override
	public Result<Post> getPost(String postId) {

		Response r = target.path(postId).request().accept(MediaType.APPLICATION_JSON).get();

		return super.responseContents(r, Status.OK, new GenericType<Post>() {
		});
	}

	public Result<String> createPost(Post post) {

		Response r = target.request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(post, MediaType.APPLICATION_JSON));

		return super.responseContents(r, Status.OK, new GenericType<String>() {
		});
	}

	@Override
	public Result<Void> deletePost(String postId) {

		Response r = target.path("/" + postId).request().delete();

		return super.verifyResponse(r, Status.OK);
	}

	@Override
	public Result<List<String>> getPosts(String userId) {

		Response r = target.path("/from/" + userId).request().accept(MediaType.APPLICATION_JSON).get();

		return super.responseContents(r, Status.OK, new GenericType<List<String>>() {
		});
	}

	@Override
	public Result<Integer> getNumPosts(String userId) {

		Response r = target.path("/count/" + userId).request().accept(MediaType.APPLICATION_JSON).get();

		return super.responseContents(r, Status.OK, new GenericType<Integer>() {
		});
	}

	@Override
	public Result<Void> deleteUserPosts(String userId) {

		Response r = target.path("/from/" + userId).request().delete();

		return super.verifyResponse(r, Status.OK);
	}

	@Override
	public Result<List<String>> getFeed(String userId) {

		Response r = target.path("/feed/" + userId).request().accept(MediaType.APPLICATION_JSON).get();

		return super.responseContents(r, Status.OK, new GenericType<List<String>>() {
		});
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {

		Response r = target.path("/" + postId + "/likes/" + userId).request()
				.post(Entity.entity(isLiked, MediaType.APPLICATION_JSON));

		return super.verifyResponse(r, Status.OK);
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {

		Response r = target.path("/" + postId + "/likes/" + userId).request().accept(MediaType.APPLICATION_JSON).get();

		return super.responseContents(r, Status.OK, new GenericType<Boolean>() {
		});
	}

}
