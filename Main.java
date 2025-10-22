package forumApp;

import java.util.*;

public class Main {
	
	/*
	 * These are the private attributes for this entity object
	 */
	private static int postIdCounter = 1;
    private static int replyIdCounter = 1;
    private static List<Post> posts = new ArrayList<>();
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
    	System.out.println("1. Create a post");
    	System.out.println("2. Create a reply to post");
    	System.out.println("3. Read all posts");
    	System.out.println("4. Read replies");
    	System.out.println("5. Update a post");
    	System.out.println("6. Update a reply");
    	System.out.println("7. Delete a post");
    	System.out.println("8. Delete a reply");
    	System.out.println("9. Exit");
        System.out.print("Choose an option: ");
        System.out.println();
        
        
        String choice = scanner.nextLine();
        switch (choice) {
        
            case "1": createPost(); break;
            case "2": replyToPost(); break;
            case "3": listPostsWithReplies(); break;
            case "4": viewReplies(); break;
            case "5": editPost(); break;
            case "6": editReply(); break;
            case "7": deletePost(); break;
            case "8": deleteReply(); break;
            case "9": return;
            default: System.out.println("Invalid choice.");
            
        }
        
        if(running && !choice.equals("9")) {
        	System.out.println("Enter a new choice to continue");
        }
        }
    }


    /*
   	 * Method: void createPost()
   	 * Description: Instantiates a new post and adds it to the collection
   	 */
    private static void createPost() {
        System.out.print("Enter your name: ");
        String author = scanner.nextLine();
        System.out.print("Enter post content: ");
        String message = scanner.nextLine();
        
        
        try {
            Post post = new Post(postIdCounter++, author, message);
            posts.add(post);
            System.out.println("Post created!");
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

    
}