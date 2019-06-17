package microgram.impl.srv.rest;

import java.util.List;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.rest.RestPosts;
import microgram.impl.srv.java.JavaPosts;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class RestPostsResources extends RestResource implements RestPosts {

	final Posts impl;
		
	public RestPostsResources(String serverUri) {
		this.impl = new JavaPosts();
	}
	
	@Override
	public Post getPost(String postId) {
		
		return super.resultOrThrow(impl.getPost(postId));
	}

	@Override
	public String createPost(Post post) {
		
		return super.resultOrThrow(impl.createPost(post));
	}

	@Override
	public void deletePost(String postId) {
		
		super.resultOrThrow(impl.deletePost(postId));
	}
	
	@Override
	public List<String> getPosts(String userId) {
		
		return super.resultOrThrow(impl.getPosts(userId));
	}
	
	@Override
	public int getNumPosts(String userId) {
		
		return super.resultOrThrow(impl.getNumPosts(userId));
	}
	
	@Override
	public void deleteUserPosts(String userId) {
		
		super.resultOrThrow(impl.deleteUserPosts(userId));
	}

	@Override
	public List<String> getFeed(String userId) {
		
		return super.resultOrThrow(impl.getFeed(userId));
	}
	
	@Override
	public void like(String postId, String userId, boolean isLiked) {
		
		super.resultOrThrow(impl.like(postId, userId, isLiked));
	}

	@Override
	public boolean isLiked(String postId, String userId) {
		
		return super.resultOrThrow(impl.isLiked(postId, userId));
	}
 
}
