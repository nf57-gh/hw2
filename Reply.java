package forumApp;

public class Reply {
	
	/*
	 * These are the private attributes for this entity object
	 */
    private int id;
    private String author;
    private String message;
    private boolean isRead;

    
    /*
	 * Constructor to instantiate a new reply with id, author, and the message
	 */
    public Reply(int id, String author, String message) {
        if (message == null || message.trim().isEmpty()){
            throw new IllegalArgumentException("Reply content cannot be empty.");
        }
        
        this.id = id;
        this.author = author;
        this.message = message;
        this.isRead = false;
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
    public String getContent() {
    	return message;
    }
    
    /*
	 * Method: boolean isRead()
	 * Description: This getter returns whether the message content
	 * has been read or not
	 */
    public boolean isRead() {
    	return isRead;
    }
    
        
    /*
	 * Method: void updateContent()
	 * Description: Allows the contents of a message to be changed
	 */
    public void updateContent(String newMessage) {
        if (newMessage == null || newMessage.trim().isEmpty())
            throw new IllegalArgumentException("Reply message cannot be empty.");
        this.message = newMessage;
    }
    
    /*
	 * Method: void markAsRead()
	 * Description: Allows hasBeenRead to be set to true
	 */
    public void markAsRead() {
        this.isRead = true;
    }
    
    /*
	 * Method: String toString
	 * Description: Custom toString method prints out id, author, and message
	 */
    public String toString() {
    	return "Reply id: " + id + ", author: " + author + ", message: " + message;
    }
    
    /*
	 * Method: void setAuthor(String name)
	 * Description: This setter changes the value of author
	 */
    public void setAuthor(String name) {
		author = name;
	}
}