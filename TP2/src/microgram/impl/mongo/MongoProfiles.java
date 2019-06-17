package microgram.impl.mongo;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ok;
import static microgram.api.java.Result.ErrorCode.CONFLICT;
import static microgram.api.java.Result.ErrorCode.NOT_FOUND;
import static microgram.impl.mongo.MongoPosts.Posts;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoQueryException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.DeleteResult;

import microgram.api.Pair;
import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.java.Result;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public final class MongoProfiles extends MongoResources implements Profiles {

	private static final String PROFILE_COLLECTION = "Profiles";
	private static final String FOLLOWS_COLLECTION = "Follow";
	private static final String INDEX_USER_ID = "userId";

	static MongoProfiles Profiles;
	private MongoCollection<Profile> profiles;
	private MongoCollection<Pair> follows;

	public MongoProfiles() {
		super();
		Profiles = this;
		this.profiles = dbName.getCollection(PROFILE_COLLECTION, Profile.class);
		this.profiles.createIndex(Indexes.ascending(INDEX_USER_ID), new IndexOptions().unique(true));
		this.follows = dbName.getCollection(FOLLOWS_COLLECTION, Pair.class);
		this.follows.createIndex(Indexes.ascending(INDEX_KEY, INDEX_VALUE), new IndexOptions().unique(true));
	}

	@Override
	public Result<Profile> getProfile(String userId) {
		Profile result = null;
		result = profiles.find(Filters.eq("userId", userId)).first();

		if (result == null)
			return error(NOT_FOUND);

		result.setFollowers((int) follows.countDocuments(Filters.eq("value", userId)));
		result.setFollowing((int) follows.countDocuments(Filters.eq("key", userId)));
		result.setPosts(Posts.getUserPostsStats(userId));

		return ok(result);
	}

	@Override
	public Result<Void> createProfile(Profile profile) {
		try {
			if (profiles.countDocuments(Filters.eq("userId", profile.getUserId())) != 0)
				return error(CONFLICT);

			profiles.insertOne(profile);
		} catch (MongoWriteException x) {
			return error(CONFLICT);
		}
		return ok();
	}

	@Override
	public Result<Void> deleteProfile(String userId) {

		if (profiles.countDocuments(Filters.eq("userId", userId)) == 0)
			return error(NOT_FOUND);
			

		DeleteResult resultProfile = profiles.deleteOne(Filters.eq("userId", userId));
		DeleteResult resultFollows = follows
				.deleteMany(Filters.or(Filters.eq("key", userId), Filters.eq("value", userId)));

		if (resultProfile.getDeletedCount() != 0 || resultFollows.getDeletedCount() != 0)
			return ok();
		
		return error(NOT_FOUND);
	}

	@Override
	public Result<List<Profile>> search(String prefix) {
		try {

			return ok(profiles.find(Filters.regex("userId", "^" + prefix)).into(new ArrayList<>()));

		} catch (MongoQueryException x) {
			return error(NOT_FOUND);
		}
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing) {

		int s1 = (int) profiles.countDocuments(Filters.eq("userId", userId1));
		int s2 = (int) profiles.countDocuments(Filters.eq("userId", userId2));

		if (s1 == 0 || s2 == 0)
			return error(NOT_FOUND);

		if (isFollowing)
			try {
				follows.insertOne(new Pair(userId1, userId2));
			} catch (MongoWriteException x) {}
		else
			follows.deleteOne(Filters.and(Filters.eq("key", userId1), Filters.eq("value", userId2)));

		return ok();
	}

	@Override
	public Result<Boolean> isFollowing(String userId1, String userId2) {

		int s1 = (int) profiles.countDocuments(Filters.eq("userId", userId1));
		int s2 = (int) profiles.countDocuments(Filters.eq("userId", userId2));

		if (s1 == 0 || s2 == 0)
			return error(NOT_FOUND);

		else {
			int isFollowing = (int) follows
					.countDocuments(Filters.and(Filters.eq("key", userId1), Filters.eq("value", userId2)));
			return ok(isFollowing != 0);
		}
	}

	public List<Pair> getFollowing(String userId) {
		return follows.find(Filters.eq("key", userId)).into(new ArrayList<>());
	}

}
