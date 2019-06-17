package microgram.api.java;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import microgram.api.Profile;

/**
 * 
 * Interface for the service that manages Users/Profiles.
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 * 
 */
public interface Profiles {

	/**
	 * Obtains a profile
	 * 
	 * @param userId unique identifier of the requested profile
	 * @return result of (OK,Profile), or NOT_FOUND
	 */
	Result<Profile> getProfile(String userId);

	/**
	 * Creates a profile
	 * 
	 * @param profile to be created
	 * @return result of (OK,), or CONFLICT
	 */
	Result<Void> createProfile(Profile profile);

	/**
	 * Delete a profile
	 * 
	 * @param userId identifier of the profile to be deleted
	 * @return result of (OK,), or NOT_FOUND
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	Result<Void> deleteProfile(String userId);

	/**
	 * Searches for profiles by prefix of the profile identifier
	 * 
	 * @param prefix - the prefix used to match identifiers
	 * @return result of (OK, List<Profile>); an empty list if the search yields no
	 *         profiles
	 */
	Result<List<Profile>> search(String prefix);

	/**
	 * Causes a profile to follow or stop following another.
	 * 
	 * @param userId1     the profile that will follow or cease to follow the
	 *                    followed profile
	 * @param userId2     the followed profile
	 * @param isFollowing flag that indicates the desired end status of the
	 *                    operation
	 * @return (OK,), NOT_FOUND if any of the profiles does not exist
	 */
	Result<Void> follow(String userId1, String userId2, boolean isFollowing);

	/**
	 * Checks if a profile is following another or not
	 * 
	 * @param userId1 the follower profile
	 * @param userId2 the followed profile
	 * @return (OK,Boolean), NOT_FOUND if any of the profiles does not exist
	 */
	Result<Boolean> isFollowing(String userId1, String userId2);

	/**
	 * Returns a set with all the userID's that the given userID is following.
	 * 
	 * @param userId the follower profile
	 * @return (OK, Set<String>), NOT_FOUND if the user doesn't follow any user
	 */
	Result<Set<String>> getFollowing(String userId);

}
