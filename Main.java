package forumApp;

import java.util.*;

public class Main {
	
	/*
	 * These are the private attributes for this entity object
	 */
	private static int postIdCounter = 1;
    private static int replyIdCounter = 1;
    private static int threadIdCounter = 1;
    private static List<Post> posts = new ArrayList<>();
    private static List<Thread> threads = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    
    /*
   	 * Main method prints out a menu in a loop, taking in
   	 * the user choice each time
   	 */
    public static void main(String[] args) {
    	boolean running = true;
    	
    	while(running)
    	{
    		
    	System.out.println();
    	System.out.println("1. Create a thread");
    	System.out.println("2. Create a post");
    	System.out.println("3. Create a reply to post");
    	System.out.println("4. Read all posts");
    	System.out.println("5. Search posts by keyword");
    	System.out.println("6. Filter posts by unread status");
    	System.out.println("7. List posts from specific user");
    	System.out.println("8. Mark post as read");
    	System.out.println("9. Read replies");
    	System.out.println("10. Update a post");
    	System.out.println("11. Update a reply");
    	System.out.println("12. Delete a post");
    	System.out.println("13. Delete a reply");
    	System.out.println("14. Exit");
        System.out.print("Choose an option: ");
        System.out.println();
        
        
        String choice = scanner.nextLine();
        switch (choice) {

            case "1": createThread(); break;
            case "2": createPost(); break;
            case "3": replyToPost(); break;
            case "4": listPostsWithReplies(); break;
            case "5": searchPostsByKeyword(); break;
            case "6": filterByUnread(); break;
            case "7": listPostsByUser(); break;
            case "8": markPostAsRead(); break;
            case "9": viewReplies(); break;
            case "10": editPost(); break;
            case "11": editReply(); break;
            case "12": deletePost(); break;
            case "13": deleteReply(); break;
            case "14": return;
            default: System.out.println("Invalid choice.");

        }

        if(running && !choice.equals("14")) {
        	System.out.println("Enter a new choice to continue");
        }
        }
    }


    /*
   	 * Method: void createThread()
   	 * Description: Instantiates a new thread
   	 */
    private static void createThread() {
        System.out.print("Enter thread name/topic: ");
        String name = scanner.nextLine();

        try {
            Thread thread = new Thread(threadIdCounter++, name);
            threads.add(thread);
            System.out.println("Thread created!");
            System.out.println();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /*
   	 * Method: void createPost()
   	 * Description: Instantiates a new post and adds it to the collection
   	 */
    private static void createPost() {
        // Show available threads
        if (threads.isEmpty()) {
            System.out.println("No threads available. Creating a default thread.");
            Thread defaultThread = new Thread(threadIdCounter++, "General");
            threads.add(defaultThread);
        }

        System.out.println("Available threads:");
        for (Thread thread : threads) {
            System.out.println(thread);
        }

        System.out.print("Enter thread ID to post to: ");
        int threadId;
        try {
            threadId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Thread ID.");
            return;
        }

        Thread thread = threads.stream().filter(t -> t.getId() == threadId).findFirst().orElse(null);
        if (thread == null) {
            System.out.println("Thread not found.");
            return;
        }

        System.out.print("Enter your name: ");
        String author = scanner.nextLine();
        System.out.print("Enter post content: ");
        String message = scanner.nextLine();


        try {
            Post post = new Post(postIdCounter++, author, message);
            posts.add(post);
            thread.addPost(post);
            System.out.println("Post created in thread: " + thread.getName());
            System.out.println();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    
    /*
   	 * Method: void replyToPost()
   	 * Description: Instantiates a new reply and adds it to the collection
   	 */
    private static void replyToPost() {
        listPostsWithReplies();
        
        
        System.out.print("Enter Post ID to reply to: ");
        int postId;
        try {
            postId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Post ID.");
            return;
        }
        
        
        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        if (post == null) {
            System.out.println("Post not found.");
            return;
        }
        System.out.print("Enter your name: ");
        String author = scanner.nextLine();
        System.out.print("Enter reply content: ");
        String content = scanner.nextLine();
        try {
            Reply reply = new Reply(replyIdCounter++, author, content);
            post.addReply(reply);
            System.out.println("Reply added!");
            System.out.println();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    
    /*
   	 * Method: void listPostsWithReplies()
   	 * Description: Displays all posts
   	 */
    private static void listPostsWithReplies() {
    	
        if (posts.isEmpty()) {
            System.out.println("No posts yet.");
            return;
        }
        for (Post post : posts) {
            System.out.println(post);
            List<Reply> replies = post.getReplies();
            if (replies.isEmpty()) {
                System.out.println("   (No replies yet)");
            } else {
                for (Reply reply : replies) {
                    System.out.println("   " + reply);
                }
            }
        }
    }

    
    /*
   	 * Method: void viewReplies()
   	 * Description: Displays the replies under a specified post
   	 */
    private static void viewReplies() {
        listPostsWithReplies();
        
        System.out.print("Enter Post ID to view replies: ");
        int postId;
        try {
            postId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Post ID.");
            return;
        }
        
        
        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        if (post == null) {
            System.out.println("Post not found.");
            return;
        }
        if (post.getReplies().isEmpty()) {
            System.out.println("No replies yet.");
        } else {
            for (Reply reply : post.getReplies()) {
                System.out.println(reply);
            }
        }
    }
    
    
    /*
   	 * Method: void editPost()
   	 * Description: Allows the contents of a post to be changed
   	 */
    private static void editPost() {
        listPostsWithReplies();
        
        System.out.print("Enter Post ID to edit: ");
        int postId;
        try {
            postId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Post ID.");
            return;
        }
        
        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        System.out.print("Enter your name: ");
        String userName = scanner.nextLine();
        if (post.getAuthor() == null || !userName.equals(post.getAuthor())) {
            System.out.println("You can only edit your own post.");
            return;
        }

        System.out.println("Current message: " + post.getMessage());
        System.out.print("Enter new message: ");
        String newContent = scanner.nextLine();
        if (newContent == null || newContent.trim().isEmpty()) {
            System.out.println("Error: Content cannot be empty.");
            return;
        }
        
        post.updateContent(newContent);
        System.out.println("Post updated.");
    }

    
    /*
   	 * Method: void editReply()
   	 * Description: Allows the contents of a reply to be changed
   	 */
    private static void editReply() {
        listPostsWithReplies();
        
        System.out.print("Enter Post ID of the reply to edit: ");
        int postId;
        try {
            postId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Post ID.");
            return;
        }
        
        
        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        if (post == null) {
            System.out.println("Post not found.");
            return;
        }
        if (post.getReplies().isEmpty()) {
            System.out.println("No replies to edit.");
            return;
        }
        for (Reply reply : post.getReplies()) {
            System.out.println(reply);
        }
        System.out.print("Enter Reply ID to edit: ");
        int replyId;
        try {
            replyId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Reply ID.");
            return;
        }
        
        
        Reply reply = post.getReplies().stream().filter(r -> r.getId() == replyId).findFirst().orElse(null);
        System.out.print("Enter your name: ");
        String userName = scanner.nextLine();
        if (reply.getAuthor() == null || !userName.equals(reply.getAuthor())) {
            System.out.println("You can only edit your own reply.");
            return;
        }

        System.out.println("Current content: " + reply.getContent());
        System.out.print("Enter new reply content: ");
        String newContent = scanner.nextLine();
        if (newContent == null || newContent.trim().isEmpty()) {
            System.out.println("Error: Content cannot be empty.");
            return;
        }
        reply.updateContent(newContent);
        System.out.println("Reply updated.");
    }

    
    
    /*
   	 * Method: void deletePost()
   	 * Description: Allows a post to be removed (doesn't remove its replies)
   	 */
    private static void deletePost() {
        listPostsWithReplies();
        System.out.print("Enter Post ID to delete: ");
        
        int postId;
        try {
            postId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Post ID.");
            return;
        }
        
        
        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        System.out.print("Enter your name: ");
        String userName = scanner.nextLine();
        if (post.getAuthor() == null || !userName.equals(post.getAuthor())) {
            System.out.println("You can only delete your own post.");
            return;
        }
        System.out.print("Are you sure you want to delete this post? (y/n): ");
        String confirm = scanner.nextLine();
        if ("y".equalsIgnoreCase(confirm)) {
        	post.setAuthor(null);
            post.updateContent("[Message deleted by user]");
            System.out.println("Post deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    
    /*
   	 * Method: void deletePost()
   	 * Description: Allows a reply to be removed
   	 */
    private static void deleteReply() {
        listPostsWithReplies();
        
        System.out.print("Enter Post ID of the reply to delete: ");
        int postId;
        try {
            postId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Post ID.");
            return;
        }
        
        
        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        if (post == null) {
            System.out.println("Post not found.");
            return;
        }
        if (post.getReplies().isEmpty()) {
            System.out.println("No replies to delete.");
            return;
        }
        for (Reply reply : post.getReplies()) {
            System.out.println(reply);
        }
        System.out.print("Enter Reply ID to delete: ");
        int replyId;
        try {
            replyId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Reply ID.");
            return;
        }
        
        
        Reply reply = post.getReplies().stream().filter(r -> r.getId() == replyId).findFirst().orElse(null);
        System.out.print("Enter your name: ");
        String userName = scanner.nextLine();
        if (reply.getAuthor() == null || !userName.equals(reply.getAuthor())) {
            System.out.println("You can only delete your own reply.");
            return;
        }
        System.out.print("Are you sure you want to delete this reply? (y/n): ");
        String confirm = scanner.nextLine();
        if ("y".equalsIgnoreCase(confirm)) {

        	reply.setAuthor(null);
            reply.updateContent("[Message deleted by user]");
            System.out.println("Reply deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /*
     * Method: void searchPostsByKeyword()
     * Description: Searches and displays posts containing a keyword
     */
    private static void searchPostsByKeyword() {
        System.out.print("Enter keyword to search: ");
        String keyword = scanner.nextLine().toLowerCase();

        List<Post> matchingPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post.getMessage().toLowerCase().contains(keyword) ||
                post.getAuthor().toLowerCase().contains(keyword)) {
                matchingPosts.add(post);
            }
        }

        if (matchingPosts.isEmpty()) {
            System.out.println("No posts found matching keyword: " + keyword);
            return;
        }

        System.out.println("\nPosts matching '" + keyword + "':");
        for (Post post : matchingPosts) {
            System.out.println(post + " [Read: " + (post.hasBeenRead() ? "Yes" : "No") + "]");
            List<Reply> replies = post.getReplies();
            if (!replies.isEmpty()) {
                System.out.println("   (" + replies.size() + " replies)");
            }
        }
    }

    /*
     * Method: void filterByUnread()
     * Description: Displays only unread posts
     */
    private static void filterByUnread() {
        List<Post> unreadPosts = new ArrayList<>();
        for (Post post : posts) {
            if (!post.hasBeenRead()) {
                unreadPosts.add(post);
            }
        }

        if (unreadPosts.isEmpty()) {
            System.out.println("No unread posts.");
            return;
        }

        System.out.println("\nUnread posts:");
        for (Post post : unreadPosts) {
            System.out.println(post);
            List<Reply> replies = post.getReplies();
            if (!replies.isEmpty()) {
                System.out.println("   (" + replies.size() + " replies)");
            }
        }
    }

    /*
     * Method: void listPostsByUser()
     * Description: Lists all posts from a specific user with reply count and read status
     */
    private static void listPostsByUser() {
        System.out.print("Enter username to search: ");
        String username = scanner.nextLine();

        List<Post> userPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post.getAuthor() != null && post.getAuthor().equals(username)) {
                userPosts.add(post);
            }
        }

        if (userPosts.isEmpty()) {
            System.out.println("No posts found from user: " + username);
            return;
        }

        System.out.println("\nPosts by " + username + ":");
        for (Post post : userPosts) {
            int replyCount = post.getReplies().size();
            String readStatus = post.hasBeenRead() ? "Read" : "Unread";
            System.out.println(post + " | Replies: " + replyCount + " | Status: " + readStatus);
        }
    }

    /*
     * Method: void markPostAsRead()
     * Description: Marks a specific post as read
     */
    private static void markPostAsRead() {
        listPostsWithReplies();

        System.out.print("Enter Post ID to mark as read: ");
        int postId;
        try {
            postId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Post ID.");
            return;
        }

        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        if (post == null) {
            System.out.println("Post not found.");
            return;
        }

        post.markAsRead();
        System.out.println("Post marked as read.");
    }


}