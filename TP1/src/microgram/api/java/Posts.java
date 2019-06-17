package microgram.api.java;

import java.util.List;

import microgram.api.Post;

/**
 * 
 * Interface for the service that manages Posts
 * 
 * @author smd
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public interface Posts {

	/**
	 * Requests a post
	 * 
	 * @param postId the unique identifier of the requested post.
	 * @return (OK,Post), or NOT_FOUND
	 */
	Result<Post> getPost(String postId);

	/**
	 * Stores a new post, generating and returning its unique identifier.
	 * 
	 * @param post the post to be created
	 * @return (OK, PostId), or (NOT FOUND) if the owner of the post does not exist.
	 */
	Result<String> createPost(Post post);

	/**
	 * Deletes a given Post.
	 * 
	 * @param postId the unique identifier of the post to be deleted
	 * @return (OK,), or NOT_FOUND if postId does not match an existing post
	 */
	Result<Void> deletePost(String postId);

	/**
	 * Adds or removes a like to a post
	 * 
	 * @param postId  the identifier of the post
	 * @param userId  the identifier of the user
	 * @param isLiked a flag with true to add a like, false to remove the like
	 * @return (OK,) if the like was added/removed; NOT_FOUND if either the post or
	 *         the like being removed does not exist, CONFLICT if the like already
	 *         exists.
	 */
	Result<Void> like(String postId, String userId, boolean isLiked);

	/**
	 * Determines if a post is liked by a user.
	 * 
	 * @param postId the identifier of the post
	 * @param userId the identifier of the user
	 * @return (OK,Boolean), or NOT_FOUND if there is no Post with the given postId
	 */
	Result<Boolean> isLiked(String postId, String userId);

	/**
	 * Retrieves the list of post identifiers of the posts published by the given
	 * user profile
	 * 
	 * @param userId the user profile that owns the requested posts
	 * @return (OK, List<PostId>|empty list) or NOT_FOUND if the user profile is not
	 *         known
	 */
	Result<List<String>> getPosts(String userId);

	/**
	 * Returns the feed of the user profile. The feed is the list of Posts made by
	 * user profiles followed by the userId profile.
	 * 
	 * @param userId user profile of the requested feed
	 * @return (OK,List<PostId>|empty list), NOT_FOUND if the user profile is not
	 *         known
	 */
	Result<List<String>> getFeed(String userId);

	/**
	 * Removes all the posts created by the user profile.
	 * 
	 * @param userId the user profile that is going to be removed
	 * @return (OK,), NOT_FOUND if the user profile is not known
	 */
	Result<Void> deleteUserPosts(String userId);

	/**
	 * Returns the number of post owned by the user profile.
	 * 
	 * @param userId the user profile
	 * @return (OK, Integer) the number of posts
	 */
	Result<Integer> getNumPosts(String userId);

}
