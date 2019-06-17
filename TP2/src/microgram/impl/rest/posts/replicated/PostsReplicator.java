package microgram.impl.rest.posts.replicated;

import static microgram.api.java.Result.error;
import static microgram.api.java.Result.ErrorCode.NOT_IMPLEMENTED;
import static microgram.impl.rest.replication.MicrogramOperation.Operation.*;

import java.util.List;

import microgram.api.Post;
import microgram.api.java.Posts;
import microgram.api.java.Result;
import microgram.impl.rest.replication.MicrogramOperation;
import microgram.impl.rest.replication.MicrogramOperationExecutor;
import microgram.impl.rest.replication.OrderedExecutor;

/**
 * 
 * @author Ana Josefa Matos (49938)
 * @author Rafael Gameiro (50677)
 *
 */
public class PostsReplicator implements MicrogramOperationExecutor, Posts {

	private static final int PostID = 0, UserID = 1;

	final Posts localReplicaDB;
	final OrderedExecutor executor;

	PostsReplicator(Posts localDB, OrderedExecutor executor) {
		this.localReplicaDB = localDB;
		this.executor = executor.init(this);
	}

	@Override
	public Result<?> execute(MicrogramOperation op) {
		switch (op.type) {
		case GetPost: {
			return localReplicaDB.getPost(op.arg(String.class));
		}
		case GetPosts: {
			return localReplicaDB.getPosts(op.arg(String.class));
		}
		case CreatePost: {
			return localReplicaDB.createPost(op.arg(Post.class));
		}
		case DeletePost: {
			return localReplicaDB.deletePost(op.arg(String.class));
		}
		case LikePost: {
			String[] users = op.args(String[].class);
			String[] posts = op.args(String[].class);
			return localReplicaDB.like(posts[PostID], users[UserID], true);
		}
		case UnLikePost: {
			String[] users = op.args(String[].class);
			String[] posts = op.args(String[].class);
			return localReplicaDB.like(posts[PostID], users[UserID], false);
		}
		case IsLiked: {
			String[] users = op.args(String[].class);
			String[] posts = op.args(String[].class);
			return localReplicaDB.isLiked(posts[PostID], users[UserID]);
		}
		case GetFeed: {
			return localReplicaDB.getFeed(op.arg(String.class));
		}
		default:
			return error(NOT_IMPLEMENTED);
		}
	}

	@Override
	public Result<Post> getPost(String postId) {

		return executor.replicate(new MicrogramOperation(GetPost, postId));
	}

	@Override
	public Result<String> createPost(Post post) {

		return executor.replicate(new MicrogramOperation(CreatePost, post));
	}

	@Override
	public Result<Void> deletePost(String postId) {

		return executor.replicate(new MicrogramOperation(DeletePost, postId));
	}

	@Override
	public Result<Void> like(String postId, String userId, boolean isLiked) {

		String[] args = { postId, userId };

		if (isLiked)
			return executor.replicate(new MicrogramOperation(LikePost, args));
		else
			return executor.replicate(new MicrogramOperation(UnLikePost, args));
	}

	@Override
	public Result<Boolean> isLiked(String postId, String userId) {

		String[] args = { postId, userId };

		return executor.replicate(new MicrogramOperation(IsLiked, args));
	}

	@Override
	public Result<List<String>> getPosts(String userId) {

		return executor.replicate(new MicrogramOperation(GetPosts, userId));
	}

	@Override
	public Result<List<String>> getFeed(String userId) {

		return executor.replicate(new MicrogramOperation(GetFeed, userId));

	}

}
