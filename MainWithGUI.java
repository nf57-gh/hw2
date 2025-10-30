package forumApp;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;

public class Main {
	
	/**
	 * These are the private attributes for this entity object
	 */
	private static int postIdCounter = 1;
    private static int replyIdCounter = 1;
    private static int threadIdCounter = 1;
    private static List<Post> posts = new ArrayList<>();
    private static List<Thread> threads = new ArrayList<>();
    private TextArea displayArea;
    private VBox forumView;


    public Main() {
    	setUpForumView();
    	
    	if(threads.isEmpty()) {
    		Thread defaultThread = new Thread(threadIdCounter++, "General");
            threads.add(defaultThread);
    	}
    }
    
    public VBox getForumView() {
    	return forumView;
    }
    
    public void openInNewWindow() {
    	Stage stage = new Stage();
    	stage.setTitle("Forum App");
    	Scene scene = new Scene(forumView, 800, 600);
    	stage.setScene(scene);
    	stage.show();
    }
    
    public void setUpForumView() {
    	forumView = new VBox(10);
    	displayArea = new TextArea();
    	displayArea.setEditable(false);
        displayArea.setPrefHeight(400);
        
        Button btn1 = new Button("1. Create a thread");
        Button btn2 = new Button("2. Create a post");
        Button btn3 = new Button("3. Create a reply to post");
        Button btn4 = new Button("4. Read all posts");
        Button btn5 = new Button("5. Search posts by keyword");
        Button btn6 = new Button("6. Filter posts by unread status");
        Button btn7 = new Button("7. List posts from specific user");
        Button btn8 = new Button("8. Mark post as read");
        Button btn9 = new Button("9. Read replies");
        Button btn10 = new Button("10. Update a post");
        Button btn11 = new Button("11. Update a reply");
        Button btn12 = new Button("12. Delete a post");
        Button btn13 = new Button("13. Delete a reply");
        
        btn1.setOnAction(e -> createThread());
        btn2.setOnAction(e -> createPost());
        btn3.setOnAction(e -> replyToPost());
        btn4.setOnAction(e -> listPostsWithReplies());
        btn5.setOnAction(e -> searchPostsByKeyword());
        btn6.setOnAction(e -> filterByUnread());
        btn7.setOnAction(e -> listPostsByUser());
        btn8.setOnAction(e -> markPostAsRead());
        btn9.setOnAction(e -> viewReplies());
        btn10.setOnAction(e -> editPost());
        btn11.setOnAction(e -> editReply());
        btn12.setOnAction(e -> deletePost());
        btn13.setOnAction(e -> deleteReply());
        
        FlowPane buttonPane = new FlowPane();
        buttonPane.getChildren().addAll(btn1, btn2, btn3, 
        		btn4, btn5, btn6, btn7, btn8, btn9, btn10, btn11, btn12, btn13);
        forumView.getChildren().addAll(displayArea, buttonPane);
    }

    /**
   	 * Method: void createThread()
   	 * Description: Instantiates a new thread
   	 */
    public void createThread() {
    	TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Thread");
        dialog.setHeaderText("Enter thread name/topic:");
        dialog.setContentText("Thread name:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            try {
                Thread thread = new Thread(threadIdCounter++, name);
                threads.add(thread);
                displayArea.setText("Thread created!\n");
            } catch (IllegalArgumentException e) {
                displayArea.setText("Error: " + e.getMessage() + "\n");
            }
        });
    }

    /**
   	 * Method: void createPost()
   	 * Description: Instantiates a new post and adds it to the collection
   	 */
    public void createPost() {
        // Show available threads

    	if (threads.isEmpty()) {
            displayArea.setText("No threads available. Creating a default thread.\n");
            Thread defaultThread = new Thread(threadIdCounter++, "General");
            threads.add(defaultThread);
        }
        
        StringBuilder sb = new StringBuilder("Available threads:\n");
        for (Thread thread : threads) {
            sb.append(thread).append("\n");
        }
        displayArea.setText(sb.toString());
        
        TextInputDialog threadDialog = new TextInputDialog();
        threadDialog.setTitle("Create Post");
        threadDialog.setHeaderText("Enter thread ID to post to:");
        threadDialog.setContentText("Thread ID:");
        
        Optional<String> threadResult = threadDialog.showAndWait();
        if (!threadResult.isPresent()) return;
        
        int threadId;
        try {
            threadId = Integer.parseInt(threadResult.get());
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid Thread ID.\n");
            return;
        }
        
        Thread thread = threads.stream().filter(t -> t.getId() == threadId).findFirst().orElse(null);
        if (thread == null) {
            displayArea.setText("Thread not found.\n");
            return;
        }
        
        TextInputDialog authorDialog = new TextInputDialog();
        authorDialog.setTitle("Create Post");
        authorDialog.setHeaderText("Enter your name:");
        authorDialog.setContentText("Name:");
        
        Optional<String> authorResult = authorDialog.showAndWait();
        if (!authorResult.isPresent()) return;
        
        TextInputDialog contentDialog = new TextInputDialog();
        contentDialog.setTitle("Create Post");
        contentDialog.setHeaderText("Enter post content:");
        contentDialog.setContentText("Content:");
        
        Optional<String> contentResult = contentDialog.showAndWait();
        if (!contentResult.isPresent()) return;
        
        try {
            Post post = new Post(postIdCounter++, authorResult.get(), contentResult.get());
            posts.add(post);
            thread.addPost(post);
            displayArea.setText("Post created in thread: " + thread.getName() + "\n");
        } catch (IllegalArgumentException e) {
            displayArea.setText("Error: " + e.getMessage() + "\n");
        }
    }
    
    
    /**
   	 * Method: void replyToPost()
   	 * Description: Instantiates a new reply and adds it to the collection
   	 */
    public void replyToPost() {
    	
    	listPostsWithReplies();
        
        TextInputDialog postDialog = new TextInputDialog();
        postDialog.setTitle("Reply to Post");
        postDialog.setHeaderText("Enter Post ID to reply to:");
        postDialog.setContentText("Post ID:");
        
        Optional<String> postResult = postDialog.showAndWait();
        if (!postResult.isPresent()) return;
        
        int postId;
        try {
            postId = Integer.parseInt(postResult.get());
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid Post ID.\n");
            return;
        }
        
        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        if (post == null) {
            displayArea.setText("Post not found.\n");
            return;
        }
        
        TextInputDialog authorDialog = new TextInputDialog();
        authorDialog.setTitle("Reply to Post");
        authorDialog.setHeaderText("Enter your name:");
        authorDialog.setContentText("Name:");
        
        Optional<String> authorResult = authorDialog.showAndWait();
        if (!authorResult.isPresent()) return;
        
        TextInputDialog contentDialog = new TextInputDialog();
        contentDialog.setTitle("Reply to Post");
        contentDialog.setHeaderText("Enter reply content:");
        contentDialog.setContentText("Content:");
        
        Optional<String> contentResult = contentDialog.showAndWait();
        if (!contentResult.isPresent()) return;
        
        try {
            Reply reply = new Reply(replyIdCounter++, authorResult.get(), contentResult.get());
            post.addReply(reply);
            displayArea.setText("Reply added!\n");
        } catch (IllegalArgumentException e) {
            displayArea.setText("Error: " + e.getMessage() + "\n");
        }
    }

    
    /**
   	 * Method: void listPostsWithReplies()
   	 * Description: Displays all posts
   	 */
    public void listPostsWithReplies() {
    	

    	if (posts.isEmpty()) {
            displayArea.setText("No posts yet.\n");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        for (Post post : posts) {
            sb.append(post).append("\n");
            List<Reply> replies = post.getReplies();
            if (replies.isEmpty()) {
                sb.append("   (No replies yet)\n");
            } else {
                for (Reply reply : replies) {
                    sb.append("   ").append(reply).append("\n");
                }
            }
        }
        displayArea.setText(sb.toString());
    }

    
    /**
   	 * Method: void viewReplies()
   	 * Description: Displays the replies under a specified post
   	 */
    public void viewReplies() {
        
    	listPostsWithReplies();
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("View Replies");
        dialog.setHeaderText("Enter Post ID to view replies:");
        dialog.setContentText("Post ID:");
        
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;
        
        int postId;
        try {
            postId = Integer.parseInt(result.get());
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid Post ID.\n");
            return;
        }
        
        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        if (post == null) {
            displayArea.setText("Post not found.\n");
            return;
        }
        
        if (post.getReplies().isEmpty()) {
            displayArea.setText("No replies yet.\n");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Reply reply : post.getReplies()) {
                sb.append(reply).append("\n");
            }
            displayArea.setText(sb.toString());
        }
    }
    
    
    /**
   	 * Method: void editPost()
   	 * Description: Allows the contents of a post to be changed
   	 */
    public void editPost() {
        
    	
    	listPostsWithReplies();
        
        TextInputDialog postDialog = new TextInputDialog();
        postDialog.setTitle("Edit Post");
        postDialog.setHeaderText("Enter Post ID to edit:");
        postDialog.setContentText("Post ID:");
        
        Optional<String> postResult = postDialog.showAndWait();
        if (!postResult.isPresent()) return;
        
        int postId;
        try {
            postId = Integer.parseInt(postResult.get());
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid Post ID.\n");
            return;
        }
        
        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        if (post == null) {
            displayArea.setText("Post not found.\n");
            return;
        }
        
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Edit Post");
        nameDialog.setHeaderText("Enter your name:");
        nameDialog.setContentText("Name:");
        
        Optional<String> nameResult = nameDialog.showAndWait();
        if (!nameResult.isPresent()) return;
        
        String userName = nameResult.get();
        if (post.getAuthor() == null || !userName.equals(post.getAuthor())) {
            displayArea.setText("You can only edit your own post.\n");
            return;
        }
        
        displayArea.setText("Current message: " + post.getMessage() + "\n");
        
        TextInputDialog contentDialog = new TextInputDialog();
        contentDialog.setTitle("Edit Post");
        contentDialog.setHeaderText("Enter new message:");
        contentDialog.setContentText("New message:");
        
        Optional<String> contentResult = contentDialog.showAndWait();
        if (!contentResult.isPresent()) return;
        
        String newContent = contentResult.get();
        if (newContent == null || newContent.trim().isEmpty()) {
            displayArea.setText("Error: Content cannot be empty.\n");
            return;
        }
        
        post.updateContent(newContent);
        displayArea.setText("Post updated.\n");
    }

    
    /**
   	 * Method: void editReply()
   	 * Description: Allows the contents of a reply to be changed
   	 */
    public void editReply() {
    	listPostsWithReplies();
        
        TextInputDialog postDialog = new TextInputDialog();
        postDialog.setTitle("Edit Reply");
        postDialog.setHeaderText("Enter Post ID of the reply to edit:");
        postDialog.setContentText("Post ID:");
        
        Optional<String> postResult = postDialog.showAndWait();
        if (!postResult.isPresent()) return;
        
        int postId;
        try {
            postId = Integer.parseInt(postResult.get());
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid Post ID.\n");
            return;
        }
        
        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        if (post == null) {
            displayArea.setText("Post not found.\n");
            return;
        }
        
        if (post.getReplies().isEmpty()) {
            displayArea.setText("No replies to edit.\n");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        for (Reply reply : post.getReplies()) {
            sb.append(reply).append("\n");
        }
        displayArea.setText(sb.toString());
        
        TextInputDialog replyDialog = new TextInputDialog();
        replyDialog.setTitle("Edit Reply");
        replyDialog.setHeaderText("Enter Reply ID to edit:");
        replyDialog.setContentText("Reply ID:");
        
        Optional<String> replyResult = replyDialog.showAndWait();
        if (!replyResult.isPresent()) return;
        
        int replyId;
        try {
            replyId = Integer.parseInt(replyResult.get());
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid Reply ID.\n");
            return;
        }
        
        Reply reply = post.getReplies().stream().filter(r -> r.getId() == replyId).findFirst().orElse(null);
        if (reply == null) {
            displayArea.setText("Reply not found.\n");
            return;
        }
        
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Edit Reply");
        nameDialog.setHeaderText("Enter your name:");
        nameDialog.setContentText("Name:");
        
        Optional<String> nameResult = nameDialog.showAndWait();
        if (!nameResult.isPresent()) return;
        
        String userName = nameResult.get();
        if (reply.getAuthor() == null || !userName.equals(reply.getAuthor())) {
            displayArea.setText("You can only edit your own reply.\n");
            return;
        }
        
        displayArea.setText("Current content: " + reply.getContent() + "\n");
        
        TextInputDialog contentDialog = new TextInputDialog();
        contentDialog.setTitle("Edit Reply");
        contentDialog.setHeaderText("Enter new reply content:");
        contentDialog.setContentText("New content:");
        
        Optional<String> contentResult = contentDialog.showAndWait();
        if (!contentResult.isPresent()) return;
        
        String newContent = contentResult.get();
        if (newContent == null || newContent.trim().isEmpty()) {
            displayArea.setText("Error: Content cannot be empty.\n");
            return;
        }
        
        reply.updateContent(newContent);
        displayArea.setText("Reply updated.\n");
    }

    
    
    /**
   	 * Method: void deletePost()
   	 * Description: Allows a post to be removed (doesn't remove its replies)
   	 */
    public void deletePost() {
        
    	listPostsWithReplies();
        
        TextInputDialog postDialog = new TextInputDialog();
        postDialog.setTitle("Delete Post");
        postDialog.setHeaderText("Enter Post ID to delete:");
        postDialog.setContentText("Post ID:");
        
        Optional<String> postResult = postDialog.showAndWait();
        if (!postResult.isPresent()) return;
        
        int postId;
        try {
            postId = Integer.parseInt(postResult.get());
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid Post ID.\n");
            return;
        }
        
        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        if (post == null) {
            displayArea.setText("Post not found.\n");
            return;
        }
        
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Delete Post");
        nameDialog.setHeaderText("Enter your name:");
        nameDialog.setContentText("Name:");
        
        Optional<String> nameResult = nameDialog.showAndWait();
        if (!nameResult.isPresent()) return;
        
        String userName = nameResult.get();
        if (post.getAuthor() == null || !userName.equals(post.getAuthor())) {
            displayArea.setText("You can only delete your own post.\n");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Post");
        alert.setHeaderText("Are you sure you want to delete this post?");
        alert.setContentText("Press OK to confirm deletion.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            post.setAuthor(null);
            post.updateContent("[Message deleted by user]");
            displayArea.setText("Post deleted.\n");
        } else {
            displayArea.setText("Deletion cancelled.\n");
        }
    }

    
    /**
   	 * Method: void deletePost()
   	 * Description: Allows a reply to be removed
   	 */
    public void deleteReply() {
        
    	listPostsWithReplies();
        
        TextInputDialog postDialog = new TextInputDialog();
        postDialog.setTitle("Delete Reply");
        postDialog.setHeaderText("Enter Post ID of the reply to delete:");
        postDialog.setContentText("Post ID:");
        
        Optional<String> postResult = postDialog.showAndWait();
        if (!postResult.isPresent()) return;
        
        int postId;
        try {
            postId = Integer.parseInt(postResult.get());
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid Post ID.\n");
            return;
        }
        
        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        if (post == null) {
            displayArea.setText("Post not found.\n");
            return;
        }
        
        if (post.getReplies().isEmpty()) {
            displayArea.setText("No replies to delete.\n");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        for (Reply reply : post.getReplies()) {
            sb.append(reply).append("\n");
        }
        displayArea.setText(sb.toString());
        
        TextInputDialog replyDialog = new TextInputDialog();
        replyDialog.setTitle("Delete Reply");
        replyDialog.setHeaderText("Enter Reply ID to delete:");
        replyDialog.setContentText("Reply ID:");
        
        Optional<String> replyResult = replyDialog.showAndWait();
        if (!replyResult.isPresent()) return;
        
        int replyId;
        try {
            replyId = Integer.parseInt(replyResult.get());
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid Reply ID.\n");
            return;
        }
        
        Reply reply = post.getReplies().stream().filter(r -> r.getId() == replyId).findFirst().orElse(null);
        if (reply == null) {
            displayArea.setText("Reply not found.\n");
            return;
        }
        
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Delete Reply");
        nameDialog.setHeaderText("Enter your name:");
        nameDialog.setContentText("Name:");
        
        Optional<String> nameResult = nameDialog.showAndWait();
        if (!nameResult.isPresent()) return;
        
        String userName = nameResult.get();
        if (reply.getAuthor() == null || !userName.equals(reply.getAuthor())) {
            displayArea.setText("You can only delete your own reply.\n");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Reply");
        alert.setHeaderText("Are you sure you want to delete this reply?");
        alert.setContentText("Press OK to confirm deletion.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            reply.setAuthor(null);
            reply.updateContent("[Message deleted by user]");
            displayArea.setText("Reply deleted.\n");
        } else {
            displayArea.setText("Deletion cancelled.\n");
        }
    }

    /**
     * Method: void searchPostsByKeyword()
     * Description: Searches and displays posts containing a keyword
     */
    public void searchPostsByKeyword() {
        
    	TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Posts");
        dialog.setHeaderText("Enter keyword to search:");
        dialog.setContentText("Keyword:");
        
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;
        
        String keyword = result.get().toLowerCase();
        
        List<Post> matchingPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post.getMessage().toLowerCase().contains(keyword) ||
                (post.getAuthor() != null && post.getAuthor().toLowerCase().contains(keyword))) {
                matchingPosts.add(post);
            }
        }
        
        if (matchingPosts.isEmpty()) {
            displayArea.setText("No posts found matching keyword: " + keyword + "\n");
            return;
        }
        
        StringBuilder sb = new StringBuilder("Posts matching '" + keyword + "':\n");
        for (Post post : matchingPosts) {
            sb.append(post).append(" [Read: ").append(post.hasBeenRead() ? "Yes" : "No").append("]\n");
            List<Reply> replies = post.getReplies();
            if (!replies.isEmpty()) {
                sb.append("   (").append(replies.size()).append(" replies)\n");
            }
        }
        displayArea.setText(sb.toString());
    }

    /**
     * Method: void filterByUnread()
     * Description: Displays only unread posts
     */
    public void filterByUnread() {
        
    	List<Post> unreadPosts = new ArrayList<>();
        for (Post post : posts) {
            if (!post.hasBeenRead()) {
                unreadPosts.add(post);
            }
        }
        
        if (unreadPosts.isEmpty()) {
            displayArea.setText("No unread posts.\n");
            return;
        }
        
        StringBuilder sb = new StringBuilder("Unread posts:\n");
        for (Post post : unreadPosts) {
            sb.append(post).append("\n");
            List<Reply> replies = post.getReplies();
            if (!replies.isEmpty()) {
                sb.append("   (").append(replies.size()).append(" replies)\n");
            }
        }
        displayArea.setText(sb.toString());
    }

    /**
     * Method: void listPostsByUser()
     * Description: Lists all posts from a specific user with reply count and read status
     */
    public void listPostsByUser() {
        
    	TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Posts by User");
        dialog.setHeaderText("Enter username to search:");
        dialog.setContentText("Username:");
        
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;
        
        String username = result.get();
        
        List<Post> userPosts = new ArrayList<>();
        for (Post post : posts) {
            if (post.getAuthor() != null && post.getAuthor().equals(username)) {
                userPosts.add(post);
            }
        }
        
        if (userPosts.isEmpty()) {
            displayArea.setText("No posts found from user: " + username + "\n");
            return;
        }
        
        StringBuilder sb = new StringBuilder("Posts by " + username + ":\n");
        for (Post post : userPosts) {
            int replyCount = post.getReplies().size();
            String readStatus = post.hasBeenRead() ? "Read" : "Unread";
            sb.append(post).append(" | Replies: ").append(replyCount).append(" | Status: ").append(readStatus).append("\n");
        }
        displayArea.setText(sb.toString());
    }

    /**
     * Method: void markPostAsRead()
     * Description: Marks a specific post as read
     */
    public void markPostAsRead() {
        
    	listPostsWithReplies();
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Mark Post as Read");
        dialog.setHeaderText("Enter Post ID to mark as read:");
        dialog.setContentText("Post ID:");
        
        Optional<String> result = dialog.showAndWait();
        if (!result.isPresent()) return;
        
        int postId;
        try {
            postId = Integer.parseInt(result.get());
        } catch (NumberFormatException e) {
            displayArea.setText("Invalid Post ID.\n");
            return;
        }
        
        Post post = posts.stream().filter(p -> p.getId() == postId).findFirst().orElse(null);
        if (post == null) {
            displayArea.setText("Post not found.\n");
            return;
        }
        
        post.markAsRead();
        displayArea.setText("Post marked as read.\n");

    }


}