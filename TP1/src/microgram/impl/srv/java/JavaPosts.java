package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import discovery.Discovery;
import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Profiles;
import microgram.api.java.Result;
import utils.Hash;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class JavaPosts implements Posts {

	static final String SERVICE = "Microgram-Profiles";

	protected Map<String, Post> posts = new ConcurrentHashMap<>();
	protected Map<String, Set<String>> likes = new ConcurrentHashMap<>();
	protected Map<String, Set<String>> userPosts = new ConcurrentHashMap<>();
	private Profiles profiles;

	private void fillProfiles() {
		URI uris[];
		try {
			uris = Discovery.findUrisOf(SERVICE, 1);

			if (uris.length > 0)
				profiles = ProfilesClientFactory.getProfilesClient(uris[0]);

		} catch (IOException | URISyntaxException e) {

			e.printStackTrace();
		}
	}

	@Override
	public Result<Post> getPost(String postId) {

		Post res = posts.get(postId);
		if (res != null)
			return ok(res);
		else
			return error(NOT_FOUND);
	}

	@Override
	public Result<String> createPost(Post post) {

		if (profiles == null)
			fillProfiles();

		if (profiles.getProfile(post.getOwnerId()).value() == null)
			return error(NOT_FOUND);

		String postId = Hash.of(post.getOwnerId(), post.getMediaUrl());
		post.setPostId(postId);
		if (posts.putIfAbsent(postId, post) == null) {

			likes.put(postId, ConcurrentHashMap.newKeySet());

			Set<String> posts = userPosts.get(post.getOwnerId());
			if (posts == null)
				userPosts.put(post.getOwnerId(), posts = ConcurrentHashMap.newKeySet());
			posts.add(postId);
		}

		return ok(postId);
	}

	@Override
	public Result<Void> deletePost(String postId) {

		Post res = posts.remove(postId);
		if (res == null)
			return error(NOT_FOUND);

		likes.remove(postId);
		String user = res.getOwnerId();
		userPosts.get(user).remove(postId);

		return ok();
	}

	@Override
	public Result<List<String>> getPosts(String userId) {

		Set<String> res = userPosts.get(userId);
		if (res != null)
			return ok(new ArrayList<>(res));
		else
			return error(NOT_FOUND);
	}

	@Override
	public Result<Integer> getNumPosts(String userId) {

		Set<String> res = userPosts.get(userId);
		if (res != null)
			return ok(res.size());
		else
			return ok(0);
	}

	@Override
	public Result<Void> deleteUserPosts(String userId) {

		Set<String> posts = userPosts.get(userId);
		if (posts == null)
			return error(NOT_FOUND);

		for (String post : userPosts.get(userId)) {
			likes.remove(post);
			posts.remove(post);
		}
		userPosts.remove(userId);

		return ok();
	}

	@Override
	public Result<List<String>> getFeed(String userId) {

		if (profiles == null)
			fillProfiles();

		List<String> feed = new ArrayList<String>();
		for (String following : profiles.getFollowing(userId).value()) {
			Set<String> followingPosts = userPosts.get(following);
			if (followingPosts != null)
				for (String posts : followingPosts)
					feed.add(posts);
		}

		return ok(feed);
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {

		Set<String> res = likes.get(postId);
		if (res == null)
			return error(NOT_FOUND);

		if (isLiked) {
			if (!res.add(userId))
				return error(CONFLICT);
		} else {
			if (!res.remove(userId))
				return error(NOT_FOUND);
		}

		getPost(postId).value().setLikes(res.size());
		return ok();
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {
		Set<String> res = likes.get(postId);

		if (res != null)
			return ok(res.contains(userId));
		else
			return error(NOT_FOUND);
	}

}
