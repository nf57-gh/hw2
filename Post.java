package forumApp;

import java.util.ArrayList;
import java.util.List;

public class Post {

	/*
	 * These are the private attributes for this entity object
	 */
	
	private int id;
	private String author;
	private String message;
	private boolean hasBeenRead;
	private List<Reply> replies;
	
	
	/*
	 * Constructor to instantiate a new post with id, author, and the message
	 */
	public Post(int id, String author, String message)
	{
		if(message.trim().isEmpty() || message == null)
		{
			throw new IllegalArgumentException("Message is empty.");
		}
		
		this.id = id;
		this.author = author;
		this.message = message;
		this.hasBeenRead = false;
		this.replies = new ArrayList<>();
	}
	
	/*
	 * Method: int getId()
	 * Description: This getter returns the id
	 */
	public int getId() {
		return id;
	}
	
	/*
	 * Method: String getAuthor()
	 * Description: This getter returns the author
	 */
	public String getAuthor() {
		return author;
	}
	
	/*
	 * Method: String getMessage()
	 * Description: This getter returns the message
	 */
	public String getMessage() {
		return message;
	}
	
	/*
	 * Method: boolean hasBeenRead
	 * Description: This getter returns whether the message content
	 * has been read or not
	 */
	public boolean hasBeenRead() {
		return hasBeenRead;
	}
	
	/*
	 * Method: List<Reply> getReplies()
	 * Description: This getter returns the replies
	 */
	public List<Reply> getReplies() {
		return replies;
	}
	
	
	
	/*
	 * Method: void updateContent()
	 * Description: Allows the contents of a message to be changed
	 */
	public void updateContent(String newContent) {
        if (newContent == null || newContent.trim().isEmpty())
            throw new IllegalArgumentException("Content cannot be empty.");
        this.message = newContent;
    }
	
	/*
	 * Method: void markAsRead()
	 * Description: Allows hasBeenRead to be set to true
	 */
    public void markAsRead() {
        this.hasBeenRead = true;
    }

    /*
	 * Method: void addReply(Reply reply)
	 * Description: Allows a reply to be made
	 */
    public void addReply(Reply reply) {
        replies.add(reply);
    }

    /*
	 * Method: void removeReply(Reply reply)
	 * Description: Allows a reply to be taken away
	 */
    public void removeReply(Reply reply) {
        replies.remove(reply);
    }
    
    /*
	 * Method: void deletePost()
	 * Description: Allows a post to be taken away
	 */
    public void deletePost() {
    	message = "[Deleted by user]";
    }
    
    /*
	 * Method: String toString
	 * Description: Custom toString method prints out id, author, and message
	 */
    public String toString() {
    	return "Post id: " + id + ", author: " + author + ", message: " + message;
    }

    /*
	 * Method: void setAuthor(String name)
	 * Description: This setter changes the value of author
	 */
	public void setAuthor(String name) {
		author = name;
	}
}

