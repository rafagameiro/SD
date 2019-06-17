package microgram.impl.mongo;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;
import static microgram.impl.mongo.MongoProfiles.Profiles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.mongodb.MongoQueryException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;

import microgram.api.Pair;
import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public final class MongoPosts extends MongoResources implements Posts {

	private static final String POSTS_COLLECTION = "Posts";
	private static final String LIKES_COLLECTION = "Likes";
	private static final String INDEX_POST_ID = "postId";

	static MongoPosts Posts;
	private MongoCollection<Post> posts;
	private MongoCollection<Pair> likes;

	public MongoPosts() {
		super();
		Posts = this;
		this.posts = dbName.getCollection(POSTS_COLLECTION, Post.class);
		this.posts.createIndex(Indexes.ascending(INDEX_POST_ID), new IndexOptions().unique(true));
		this.likes = dbName.getCollection(LIKES_COLLECTION, Pair.class);
		this.likes.createIndex(Indexes.ascending(INDEX_KEY, INDEX_VALUE), new IndexOptions().unique(true));

	}

	@Override
	public Result<Post> getPost(String postId) {
		Post result = null;
		try {
			result = posts.find(Filters.eq("postId", postId)).first();
			if (result != null)
				result.setLikes((int) likes.countDocuments(Filters.eq("key", postId)));
			else
				return error(NOT_FOUND);

		} catch (MongoQueryException x) {
			return error(NOT_FOUND);
		}

		return ok(result);
	}

	@Override
	public Result<String> createPost(Post post) {
		try {
			if (Profiles.getProfile(post.getOwnerId()) == null)
				return error(NOT_FOUND);

			posts.insertOne(post);
		} catch (MongoWriteException x) {
			return error(NOT_FOUND);
		}
		return ok(post.getPostId());
	}

	@Override
	public Result<Void> deletePost(String postId) {

		DeleteResult result = posts.deleteOne(Filters.eq("postId", postId));

		if (result.getDeletedCount() != 0) {
			likes.deleteMany(Filters.eq("key", postId));
			return ok();
		} else
			return error(NOT_FOUND);
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {

		if (posts.countDocuments(Filters.eq("postId", postId)) == 0)
			return error(NOT_FOUND);

		if (isLiked) {
			if (likes.countDocuments(Filters.and(Filters.eq("key", postId), Filters.eq("value", userId))) != 0)
				return error(CONFLICT);

			likes.insertOne(new Pair(postId, userId));
		} else {
			DeleteResult result = likes.deleteOne(Filters.and(Filters.eq("key", postId), Filters.eq("value", userId)));

			if (result.getDeletedCount() == 0)
				return error(NOT_FOUND);
		}

		return ok();
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {

		int result = (int) posts.countDocuments(Filters.eq("postId", postId));
		if (result == 0)
			return error(NOT_FOUND);
		else {
			result = (int) likes.countDocuments(Filters.and(Filters.eq("key", postId), Filters.eq("value", userId)));
			
			return ok(result != 0);
		}
	}

	@Override
	public Result<List<String>> getPosts(String userId) {
		List<String> results = new ArrayList<>();
		try {
			posts.find(Filters.eq("ownerId", userId)).forEach((Consumer<? super Post>) (Post post) -> {
				results.add(post.getPostId());
			});

		} catch (MongoQueryException x) {
			return error(NOT_FOUND);
		}
		if (results.size() == 0)
			return error(NOT_FOUND);

		return ok(results);
	}

	@Override
	public Result<List<String>> getFeed(String userId) {
		List<Pair> following = Profiles.getFollowing(userId);
		if (following != null) {
			List<String> feed = new ArrayList<>();
			for (Pair followee : following)
				posts.find(Filters.eq("ownerId", followee.getValue())).forEach((Consumer<? super Post>) (Post post) -> {
					feed.add(post.getPostId());
				});
			return ok(feed);
		} else
			return error(NOT_FOUND);
	}

	int getUserPostsStats(String userId) {
		return (int) posts.countDocuments(Filters.eq("ownerId", userId));
	}

	void deleteAllUserPosts(String userId) {
		posts.deleteMany(Filters.eq("ownerId", userId));
	}

}
