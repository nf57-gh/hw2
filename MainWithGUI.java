package forumApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Rich, forum-inspired JavaFX view that lets students browse threads, read posts and replies,
 * and contribute new content. This replaces the console-style UI for a friendlier experience.
 */
public class MainWithGUI {

    private static int postIdCounter = 1;
    private static int replyIdCounter = 1;
    private static int threadIdCounter = 1;

    private static final List<Thread> allThreads = new ArrayList<>();
    private static final List<Post> allPosts = new ArrayList<>();
    private static final Map<Post, Thread> postToThread = new HashMap<>();

    private enum ViewMode { THREAD, FILTERED }

    private ViewMode viewMode = ViewMode.THREAD;

    private final ObservableList<Thread> threadData = FXCollections.observableArrayList();
    private final ObservableList<Post> postData = FXCollections.observableArrayList();
    private final ObservableList<Reply> replyData = FXCollections.observableArrayList();

    private BorderPane root;

    private ComboBox<Thread> threadSelector;
    private ListView<Post> postListView;
    private ListView<Reply> replyListView;
    private TextField usernameField;
    private TextArea newPostArea;
    private TextArea newReplyArea;
    private Button addReplyButton;
    private Button editPostButton;
    private Button deletePostButton;
    private Button markReadButton;
    private Button editReplyButton;
    private Button deleteReplyButton;
    private Label statusLabel;

    public MainWithGUI() {
        buildUI();
        ensureDefaultThread();
        refreshThreadSelector();
        if (!threadData.isEmpty()) {
            threadSelector.getSelectionModel().selectFirst();
            refreshPostsForThread(threadSelector.getValue());
        }
    }

    public static void main(String[] args) {
        ForumAppLauncher.main(args);
    }

    public Parent getRootView() {
        return root;
    }

    /**
     * Convenience for other screens wanting to pop open the forum window.
     */
    public void openInNewWindow() {
        ForumAppLauncher.showStandaloneWindow();
    }

    private void buildUI() {
        root = new BorderPane();
        root.setPadding(new Insets(14));

        root.setTop(buildTopBar());
        root.setCenter(buildContentArea());
        root.setBottom(buildBottomComposer());
    }

    private VBox buildTopBar() {
        VBox container = new VBox(10);

        HBox row1 = new HBox(12);
        row1.setAlignment(Pos.CENTER_LEFT);

        Label userLabel = new Label("Display name:");
        usernameField = new TextField();
        usernameField.setPromptText("Enter the name to post as");
        usernameField.setPrefWidth(200);

        Label threadLabel = new Label("Thread:");
        threadSelector = new ComboBox<>(threadData);
        threadSelector.setPrefWidth(240);
        threadSelector.setButtonCell(new ThreadListCell());
        threadSelector.setCellFactory(list -> new ThreadListCell());
        threadSelector.setOnAction(event -> {
            Thread selected = threadSelector.getValue();
            if (selected != null) {
                viewMode = ViewMode.THREAD;
                refreshPostsForThread(selected);
                showStatus("Viewing thread \"" + selected.getName() + "\"");
            }
        });

        Button newThreadButton = new Button("New Thread");
        newThreadButton.setOnAction(event -> createThread());

        Button resetViewButton = new Button("Reset View");
        resetViewButton.setOnAction(event -> resetView());

        row1.getChildren().addAll(
            userLabel, usernameField,
            new Separator(Orientation.VERTICAL),
            threadLabel, threadSelector, newThreadButton, resetViewButton
        );

        HBox row2 = new HBox(10);
        row2.setAlignment(Pos.CENTER_LEFT);

        Button searchButton = new Button("Search Posts");
        searchButton.setOnAction(event -> searchPosts());

        Button myPostsButton = new Button("My Posts");
        myPostsButton.setOnAction(event -> showPostsByCurrentUser());

        Button unreadButton = new Button("Unread Posts");
        unreadButton.setOnAction(event -> showUnreadPosts());

        row2.getChildren().addAll(searchButton, myPostsButton, unreadButton);

        container.getChildren().addAll(row1, row2);
        return container;
    }

    private SplitPane buildContentArea() {
        postListView = new ListView<>(postData);
        postListView.setCellFactory(list -> new PostListCell());
        postListView.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldValue, newValue) -> onPostSelected(newValue));
        postListView.setPlaceholder(new Label("No posts yet. Be the first to start the discussion!"));
        VBox.setVgrow(postListView, javafx.scene.layout.Priority.ALWAYS);

        editPostButton = new Button("Edit");
        editPostButton.setOnAction(event -> editSelectedPost());

        deletePostButton = new Button("Delete");
        deletePostButton.setOnAction(event -> deleteSelectedPost());

        markReadButton = new Button("Mark Read");
        markReadButton.setOnAction(event -> markSelectedPostAsRead());
        markReadButton.setDisable(true);

        HBox postActions = new HBox(8, editPostButton, deletePostButton, markReadButton);
        postActions.setAlignment(Pos.CENTER_LEFT);

        VBox postsPane = new VBox(10,
            new Label("Posts"),
            postListView,
            postActions
        );
        postsPane.setPrefWidth(420);

        replyListView = new ListView<>(replyData);
        replyListView.setCellFactory(list -> new ReplyListCell());
        replyListView.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldValue, newValue) -> onReplySelected(newValue));
        replyListView.setPlaceholder(new Label("Select a post to view replies."));
        VBox.setVgrow(replyListView, javafx.scene.layout.Priority.ALWAYS);

        editReplyButton = new Button("Edit");
        editReplyButton.setOnAction(event -> editSelectedReply());

        deleteReplyButton = new Button("Delete");
        deleteReplyButton.setOnAction(event -> deleteSelectedReply());

        HBox replyActions = new HBox(8, editReplyButton, deleteReplyButton);
        replyActions.setAlignment(Pos.CENTER_LEFT);

        VBox repliesPane = new VBox(10,
            new Label("Replies"),
            replyListView,
            replyActions
        );

        SplitPane splitPane = new SplitPane(postsPane, repliesPane);
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.5);

        return splitPane;
    }

    private VBox buildBottomComposer() {
        VBox container = new VBox(12);
        container.setPadding(new Insets(14, 0, 0, 0));

        VBox newPostBox = new VBox(8);
        newPostArea = new TextArea();
        newPostArea.setPromptText("Share something with the class...");
        newPostArea.setPrefRowCount(3);
        Button addPostButton = new Button("Post Message");
        addPostButton.setOnAction(event -> addNewPost());
        newPostBox.getChildren().addAll(new Label("Create a new post"), newPostArea, addPostButton);

        VBox newReplyBox = new VBox(8);
        newReplyArea = new TextArea();
        newReplyArea.setPromptText("Reply to the selected post...");
        newReplyArea.setPrefRowCount(3);
        addReplyButton = new Button("Reply");
        addReplyButton.setOnAction(event -> addReplyToSelectedPost());
        newReplyBox.getChildren().addAll(new Label("Reply to this post"), newReplyArea, addReplyButton);

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        container.getChildren().addAll(newPostBox, new Separator(), newReplyBox, statusLabel);

        bindButtonStates();
        return container;
    }

    private void bindButtonStates() {
        editPostButton.disableProperty().bind(postListView.getSelectionModel().selectedItemProperty().isNull());
        deletePostButton.disableProperty().bind(postListView.getSelectionModel().selectedItemProperty().isNull());
        addReplyButton.disableProperty().bind(postListView.getSelectionModel().selectedItemProperty().isNull());
        newReplyArea.disableProperty().bind(postListView.getSelectionModel().selectedItemProperty().isNull());

        editReplyButton.disableProperty().bind(replyListView.getSelectionModel().selectedItemProperty().isNull());
        deleteReplyButton.disableProperty().bind(replyListView.getSelectionModel().selectedItemProperty().isNull());
    }

    private void ensureDefaultThread() {
        if (allThreads.isEmpty()) {
            Thread defaultThread = new Thread(threadIdCounter++, "General Discussion");
            allThreads.add(defaultThread);
        }
    }

    private void refreshThreadSelector() {
        threadData.setAll(allThreads);
    }

    private void refreshPostsForThread(Thread thread) {
        if (thread == null) {
            postData.clear();
            replyData.clear();
            markReadButton.setDisable(true);
            return;
        }
        postData.setAll(thread.getPosts());
        postListView.getSelectionModel().clearSelection();
        replyData.clear();
        markReadButton.setDisable(true);
    }

    private void onPostSelected(Post post) {
        if (post == null) {
            replyData.clear();
            markReadButton.setDisable(true);
            return;
        }

        markReadButton.setDisable(post.hasBeenRead());

        Thread thread = getThreadForPost(post);
        String threadName = thread != null ? thread.getName() : "Unknown thread";
        String author = post.getAuthor() != null ? post.getAuthor() : "Unknown author";
        String status = post.hasBeenRead() ? "Read" : "Unread";

        replyData.setAll(post.getReplies());
        replyListView.refresh();
        replyListView.getSelectionModel().clearSelection();
        int replyCount = replyData.size();
        String suffix = replyCount == 0
            ? "No replies yet."
            : replyCount == 1 ? "1 reply." : replyCount + " replies.";
        showStatus(String.format("Post #%d • %s • %s • %s — %s",
            post.getId(), threadName, author, status, suffix));
    }

    private void onReplySelected(Reply reply) {
        if (reply == null) {
            return;
        }

        String author = reply.getAuthor() != null ? reply.getAuthor() : "Unknown author";
        String status = reply.isRead() ? "Read" : "Unread";

        showStatus(String.format("Reply #%d • %s • %s", reply.getId(), author, status));
    }

    private void addNewPost() {
        Thread thread = resolveActiveThread();
        if (thread == null) {
            showStatus("Select a thread before posting.");
            return;
        }

        String author = requireUsername("post");
        if (author == null) {
            return;
        }

        String content = extractText(newPostArea, "Post content cannot be empty.");
        if (content == null) {
            return;
        }

        try {
            Post post = new Post(postIdCounter++, author, content);
            thread.addPost(post);
            allPosts.add(post);
            postToThread.put(post, thread);
            if (viewMode != ViewMode.THREAD) {
                resetView();
            } else {
                refreshPostsForThread(thread);
            }
            postListView.getSelectionModel().select(post);
            newPostArea.clear();
            showStatus("Post published to \"" + thread.getName() + "\".");
        } catch (IllegalArgumentException ex) {
            showStatus("Error: " + ex.getMessage());
        }
    }

    private void addReplyToSelectedPost() {
        Post post = postListView.getSelectionModel().getSelectedItem();
        if (post == null) {
            showStatus("Select a post to reply to.");
            return;
        }

        String author = requireUsername("reply");
        if (author == null) {
            return;
        }

        String content = extractText(newReplyArea, "Reply content cannot be empty.");
        if (content == null) {
            return;
        }

        try {
            Reply reply = new Reply(replyIdCounter++, author, content);
            post.addReply(reply);
            replyData.add(reply);
            replyListView.refresh();
            replyListView.getSelectionModel().select(reply);
            newReplyArea.clear();
            showStatus("Reply added.");
        } catch (IllegalArgumentException ex) {
            showStatus("Error: " + ex.getMessage());
        }
    }

    private void editSelectedPost() {
        Post post = postListView.getSelectionModel().getSelectedItem();
        if (post == null) {
            return;
        }

        if (!canModify(post.getAuthor())) {
            return;
        }

        TextInputDialog dialog = new TextInputDialog(post.getMessage());
        dialog.setTitle("Edit Post");
        dialog.setHeaderText("Update the post content");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        String updated = result.get().trim();
        if (updated.isEmpty()) {
            showStatus("Post content cannot be empty.");
            return;
        }

        try {
            post.updateContent(updated);
            refreshAfterEdit(post);
            showStatus("Post updated.");
        } catch (IllegalArgumentException ex) {
            showStatus("Error: " + ex.getMessage());
        }
    }

    private void deleteSelectedPost() {
        Post post = postListView.getSelectionModel().getSelectedItem();
        if (post == null) {
            return;
        }

        if (!canModify(post.getAuthor())) {
            return;
        }

        if (!confirm("Delete Post", "Are you sure you want to delete this post?")) {
            return;
        }

        Thread thread = getThreadForPost(post);
        if (thread != null) {
            thread.removePost(post);
        }
        allPosts.remove(post);
        postToThread.remove(post);
        postData.remove(post);
        replyData.clear();
        markReadButton.setDisable(true);
        showStatus("Post deleted.");
    }

    private void markSelectedPostAsRead() {
        Post post = postListView.getSelectionModel().getSelectedItem();
        if (post == null) {
            return;
        }

        post.markAsRead();
        markReadButton.setDisable(true);
        postListView.refresh();
        refreshAfterEdit(post);
        showStatus("Marked post as read.");
    }

    private void editSelectedReply() {
        Reply reply = replyListView.getSelectionModel().getSelectedItem();
        if (reply == null) {
            return;
        }

        if (!canModify(reply.getAuthor())) {
            return;
        }

        TextInputDialog dialog = new TextInputDialog(reply.getContent());
        dialog.setTitle("Edit Reply");
        dialog.setHeaderText("Update the reply content");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        String updated = result.get().trim();
        if (updated.isEmpty()) {
            showStatus("Reply content cannot be empty.");
            return;
        }

        try {
            reply.updateContent(updated);
            replyListView.refresh();
            showStatus("Reply updated.");
        } catch (IllegalArgumentException ex) {
            showStatus("Error: " + ex.getMessage());
        }
    }

    private void deleteSelectedReply() {
        Reply reply = replyListView.getSelectionModel().getSelectedItem();
        if (reply == null) {
            return;
        }

        if (!canModify(reply.getAuthor())) {
            return;
        }

        if (!confirm("Delete Reply", "Are you sure you want to delete this reply?")) {
            return;
        }

        Post post = postListView.getSelectionModel().getSelectedItem();
        if (post != null) {
            post.removeReply(reply);
        }
        replyData.remove(reply);
        replyListView.refresh();
        showStatus("Reply deleted.");
    }

    private void searchPosts() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search Posts");
        dialog.setHeaderText("Enter a keyword to search");
        dialog.setContentText("Keyword:");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        String keyword = result.get().trim().toLowerCase();
        if (keyword.isEmpty()) {
            showStatus("Search term cannot be empty.");
            return;
        }

        List<Post> matches = allPosts.stream()
            .filter(post ->
                (post.getMessage() != null && post.getMessage().toLowerCase().contains(keyword)) ||
                (post.getAuthor() != null && post.getAuthor().toLowerCase().contains(keyword)))
            .collect(Collectors.toList());

        if (matches.isEmpty()) {
            showStatus("No posts found for \"" + keyword + "\".");
            return;
        }

        applyFilteredPosts(matches, "Showing results for \"" + keyword + "\".");
    }

    private void showPostsByCurrentUser() {
        String user = requireUsername("view your posts");
        if (user == null) {
            return;
        }

        List<Post> matches = allPosts.stream()
            .filter(post -> user.equals(post.getAuthor()))
            .collect(Collectors.toList());

        if (matches.isEmpty()) {
            showStatus("No posts found for " + user + ".");
            return;
        }

        applyFilteredPosts(matches, "Showing posts by " + user + ".");
    }

    private void showUnreadPosts() {
        List<Post> unread = allPosts.stream()
            .filter(post -> !post.hasBeenRead())
            .collect(Collectors.toList());

        if (unread.isEmpty()) {
            showStatus("You're caught up! No unread posts.");
            return;
        }

        applyFilteredPosts(unread, "Showing unread posts.");
    }

    private void applyFilteredPosts(List<Post> posts, String message) {
        viewMode = ViewMode.FILTERED;
        postData.setAll(posts);
        postListView.getSelectionModel().clearSelection();
        replyData.clear();
        threadSelector.getSelectionModel().clearSelection();
        showStatus(message + " Use Reset View to return to threads.");
    }

    private void resetView() {
        viewMode = ViewMode.THREAD;
        refreshThreadSelector();
        if (!threadData.isEmpty()) {
            threadSelector.getSelectionModel().selectFirst();
            refreshPostsForThread(threadSelector.getValue());
            showStatus("Back to thread view.");
        }
    }

    private void createThread() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Thread");
        dialog.setHeaderText("Create a new discussion thread");
        dialog.setContentText("Thread name:");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }

        String name = result.get().trim();
        if (name.isEmpty()) {
            showStatus("Thread name cannot be empty.");
            return;
        }

        Thread thread = new Thread(threadIdCounter++, name);
        allThreads.add(thread);
        refreshThreadSelector();
        threadSelector.getSelectionModel().select(thread);
        refreshPostsForThread(thread);
        showStatus("Thread \"" + name + "\" created.");
    }

    private String requireUsername(String action) {
        String value = usernameField.getText() != null ? usernameField.getText().trim() : "";
        if (value.isEmpty()) {
            showStatus("Enter your display name to " + action + ".");
            return null;
        }
        return value;
    }

    private String extractText(TextArea area, String errorMessage) {
        String value = area.getText() != null ? area.getText().trim() : "";
        if (value.isEmpty()) {
            showStatus(errorMessage);
            return null;
        }
        return value;
    }

    private boolean canModify(String author) {
        String user = requireUsername("modify content");
        if (user == null) {
            return false;
        }
        if (author == null || !author.equals(user)) {
            showStatus("You can only modify content posted by \"" + user + "\".");
            return false;
        }
        return true;
    }

    private Thread getThreadForPost(Post post) {
        return postToThread.get(post);
    }

    private Thread resolveActiveThread() {
        if (viewMode == ViewMode.FILTERED) {
            showStatus("Reset to a thread before posting.");
            return null;
        }
        return threadSelector.getValue();
    }

    private void refreshAfterEdit(Post post) {
        postListView.refresh();
        onPostSelected(post);
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        return result.filter(buttonType -> buttonType == javafx.scene.control.ButtonType.OK).isPresent();
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
    }

    private static String preview(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    private final class PostListCell extends ListCell<Post> {
        @Override
        protected void updateItem(Post post, boolean empty) {
            super.updateItem(post, empty);
            if (empty || post == null) {
                setText(null);
            } else {
                Thread thread = getThreadForPost(post);
                String threadName = thread != null ? thread.getName() : "Unknown thread";
                String author = post.getAuthor() != null ? post.getAuthor() : "Unknown";
                String status = post.hasBeenRead() ? "Read" : "Unread";
                String line = String.format(
                    "#%d • %s • %s%s%n%s",
                    post.getId(),
                    author,
                    status,
                    viewMode == ViewMode.FILTERED ? " • " + threadName : "",
                    preview(post.getMessage(), 90)
                );
                setText(line);
            }
        }
    }

    private static final class ReplyListCell extends ListCell<Reply> {
        @Override
        protected void updateItem(Reply reply, boolean empty) {
            super.updateItem(reply, empty);
            if (empty || reply == null) {
                setText(null);
            } else {
                String author = reply.getAuthor() != null ? reply.getAuthor() : "Unknown";
                String status = reply.isRead() ? "Read" : "Unread";
                String line = String.format(
                    "#%d • %s • %s%n%s",
                    reply.getId(),
                    author,
                    status,
                    preview(reply.getContent(), 80)
                );
                setText(line);
            }
        }
    }

    private static final class ThreadListCell extends ListCell<Thread> {
        @Override
        protected void updateItem(Thread thread, boolean empty) {
            super.updateItem(thread, empty);
            if (empty || thread == null) {
                setText(null);
            } else {
                setText(thread.getName() + " (" + thread.getPosts().size() + " posts)");
            }
        }
    }
}
