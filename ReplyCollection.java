package forumApp;

import java.util.*;
import java.util.function.Predicate;

public class ReplyCollection {
	
	/*
	 * These are the private attributes for this entity object
	 */
    private List<Reply> replies;

    /*
	 * Constructor to instantiate a new collection of replies
	 */
    public ReplyCollection() {
        this.replies = new ArrayList<>();
    }

    /*
   	 * Method: void addReply(Reply reply)
   	 * Description: Adds a new reply to the collection
   	 */
    public void addReply(Reply reply) {
        replies.add(reply);
    }

    /*
   	 * Method: void removeReply(Reply reply)
   	 * Description: Removes a reply from the collection
   	 */
    public void removeReply(Reply reply) {
        replies.remove(reply);
    }

    /*
	 * Method: List<Reply> getAllReplies()
	 * Description: Lists out contents of the collection
	 */
    public List<Reply> getAllReplies() {
        return new ArrayList<>(replies);
    }

    /*
	 * Method: List<Reply> getReplies(Predicate<Reply> filter)
	 * Description: Allows for searching of replies through a filter
	 */
    public List<Reply> getReplies(Predicate<Reply> filter) {
        List<Reply> filtered = new ArrayList<>();
        for (Reply reply : replies) {
            if (filter.test(reply)) {
                filtered.add(reply);
            }
        }
        return filtered;
    }
}