package microgram.impl.srv.rest;

import java.util.List;
import java.util.Set;

import microgram.api.Profile;
import microgram.api.java.Profiles;
import microgram.api.rest.RestProfiles;
import microgram.impl.srv.java.JavaProfiles;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class RestProfilesResources extends RestResource implements RestProfiles {

	final Profiles impl;

	public RestProfilesResources(String serverUri) {
		this.impl = new JavaProfiles();
	}

	@Override
	public Profile getProfile(String userId) {

		return super.resultOrThrow(impl.getProfile(userId));
	}

	@Override
	public void createProfile(Profile profile) {

		super.resultOrThrow(impl.createProfile(profile));
	}

	@Override
	public void deleteProfile(String userId) {

		super.resultOrThrow(impl.deleteProfile(userId));
	}

	@Override
	public List<Profile> search(String prefix) {

		return super.resultOrThrow(impl.search(prefix));
	}

	@Override
	public void follow(String userId1, String userId2, boolean isFollowing) {

		super.resultOrThrow(impl.follow(userId1, userId2, isFollowing));
	}

	@Override
	public Set<String> getFollowing(String userId) {

		return super.resultOrThrow(impl.getFollowing(userId));
	}

	@Override
	public boolean isFollowing(String userId1, String userId2) {

		return super.resultOrThrow(impl.isFollowing(userId1, userId2));
	}

}
