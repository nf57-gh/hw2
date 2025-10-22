package forumApp;

import java.util.*;
import java.util.function.Predicate;

public class PostCollection {
	
	/*
	 * These are the private attributes for this entity object
	 */
    private List<Post> posts;

    /*
	 * Constructor to instantiate a new collection of posts
	 */
    public PostCollection() {
        this.posts = new ArrayList<>();
    }

    /*
	 * Method: void addPost(Post post)
	 * Description: Adds a new post to the collection
	 */
    public void addPost(Post post) {
        posts.add(post);
    }
    
    /*
	 * Method: void removePost(Post post)
	 * Description: Removes a post from the collection
	 */
    public void removePost(Post post) {
        posts.remove(post);
    }

    /*
	 * Method: List<Post> getAllPosts()
	 * Description: Lists out contents of the collection
	 */
    public List<Post> getAllPosts() {
        return new ArrayList<>(posts);
    }

    /*
	 * Method: List<Post> getPosts(Predicate<Post> filter)
	 * Description: Allows for searching of posts through a filter
	 */
    public List<Post> getPosts(Predicate<Post> filter) {
        List<Post> filtered = new ArrayList<>();
        for (Post post : posts) {
            if (filter.test(post)) {
                filtered.add(post);
            }
        }
        return filtered;
    }
}