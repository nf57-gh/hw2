package forumApp;

import java.util.ArrayList;
import java.util.List;

public class Thread {

	/*
	 * These are the private attributes for this entity object
	 */
	private int id; // Thread ID
	private String name; // Thread name/topic
	private List<Post> posts; // Posts in this thread

	/*
	 * Constructor to instantiate a new thread with id and name
	 */
	public Thread(int id, String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Thread name cannot be empty.");
		}
		this.id = id;
		this.name = name;
		this.posts = new ArrayList<>();
	}

	/*
	 * Method: int getId()
	 * Description: This getter returns the id
	 */
	public int getId() {
		return id;
	}

	/*
	 * Method: String getName()
	 * Description: This getter returns the thread name
	 */
	public String getName() {
		return name;
	}

	/*
	 * Method: List<Post> getPosts()
	 * Description: This getter returns all posts in the thread
	 */
	public List<Post> getPosts() {
		return posts;
	}

	/*
	 * Method: void addPost(Post post)
	 * Description: Adds a post to this thread
	 */
	public void addPost(Post post) {
		posts.add(post);
	}

	/*
	 * Method: void removePost(Post post)
	 * Description: Removes a post from this thread
	 */
	public void removePost(Post post) {
		posts.remove(post);
	}

	/*
	 * Method: String toString()
	 * Description: Custom toString method prints out id and name
	 */
	public String toString() {
		return "Thread id: " + id + ", name: " + name + ", posts: " + posts.size();
	}
}
