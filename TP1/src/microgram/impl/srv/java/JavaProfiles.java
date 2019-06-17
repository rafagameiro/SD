package microgram.impl.srv.java;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import discovery.Discovery;
import microgram.api.Profile;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.impl.srv.rest.RestResource;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class JavaProfiles extends RestResource implements microgram.api.java.Profiles {

	static final String SERVICE = "Microgram-Posts";

	protected Map<String, Profile> users = new ConcurrentHashMap<>();
	protected Map<String, Set<String>> followers = new ConcurrentHashMap<>();
	protected Map<String, Set<String>> following = new ConcurrentHashMap<>();
	private Posts posts;

	private void fillPosts() {
		URI uris[];
		try {
			uris = Discovery.findUrisOf(SERVICE, 1);

			if (uris.length > 0)
				posts = PostsClientFactory.getPostsClient(uris[0]);

		} catch (IOException | URISyntaxException e) {

			e.printStackTrace();
		}
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		
		Profile res = users.get(userId);
		if (res == null)
			return error(NOT_FOUND);

		if (posts == null)
			fillPosts();

		Result<Integer> answer = posts.getNumPosts(userId);

		res.setPosts(answer.value());
		res.setFollowers(followers.get(userId).size());
		res.setFollowing(following.get(userId).size());

		return ok(res);
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		
		Profile res = users.putIfAbsent(profile.getUserId(), profile);
		if (res != null)
			return error(CONFLICT);

		followers.put(profile.getUserId(), ConcurrentHashMap.newKeySet());
		following.put(profile.getUserId(), ConcurrentHashMap.newKeySet());
		return ok();
	}

	@Override
	public Result<Void> deleteProfile(String userId) {

		Profile res = users.get(userId);
		if (res == null)
			return error(NOT_FOUND);

		if (posts == null)
			fillPosts();

		posts.deleteUserPosts(userId);
		users.remove(userId);

		for (String follower : followers.get(userId)) {
			following.get(follower).remove(userId);
		}
		followers.remove(userId);

		for (String following : following.get(userId)) {
			followers.get(following).remove(userId);
		}
		following.remove(userId);

		return ok();
	}

	@Override
	public Result<List<Profile>> search(String prefix) {

		return ok(users.values().stream().filter(p -> p.getUserId().startsWith(prefix)).collect(Collectors.toList()));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {

		Set<String> s1 = following.get(userId1);
		Set<String> s2 = followers.get(userId2);

		if (s1 == null || s2 == null)
			return error(NOT_FOUND);

		if (isFollowing) {
			boolean added1 = s1.add(userId2), added2 = s2.add(userId1);
			if (!added1 || !added2)
				return error(CONFLICT);
		} else {
			boolean removed1 = s1.remove(userId2), removed2 = s2.remove(userId1);
			if (!removed1 || !removed2)
				return error(NOT_FOUND);
		}
		return ok();
	}

	@Override
	public Result<Set<String>> getFollowing(String userId) {

		Set<String> s = following.get(userId);

		if (s == null)
			return error(NOT_FOUND);
		else
			return ok(s);
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {

		Set<String> s1 = following.get(userId1);
		Set<String> s2 = followers.get(userId2);

		if (s1 == null || s2 == null)
			return error(NOT_FOUND);
		else
			return ok(s1.contains(userId2) && s2.contains(userId1));
	}

}
