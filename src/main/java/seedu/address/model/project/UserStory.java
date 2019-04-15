package seedu.address.model.project;

import static java.util.Objects.requireNonNull;

/**
 * Represents a user story stored in pocket project.
 */
public class UserStory implements Comparable<UserStory> {

    private UserStoryImportance importance;
    private UserStoryUser user;
    private UserStoryFunction function;
    private UserStoryReason reason;
    private Status status;

    /**
     * Constructor for a user story
     */
    public UserStory(UserStoryImportance importance, UserStoryUser user, UserStoryFunction function,
                     UserStoryReason reason) {
        requireNonNull(importance);
        requireNonNull(user);
        requireNonNull(function);
        requireNonNull(reason);

        this.importance = importance;
        this.user = user;
        this.function = function;
        this.reason = reason;
        this.status = new Status();
    }

    /**
     * Constructor for a user story including its status
     */
    public UserStory(UserStoryImportance importance, UserStoryUser user, UserStoryFunction function,
                     UserStoryReason reason, Status status) {
        this(importance, user, function, reason);
        this.status = status;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else { //check all the fields for equality
            return other instanceof UserStory
                    && importance.equals(((UserStory) other).importance)
                    && user.equals(((UserStory) other).user)
                    && function.equals(((UserStory) other).function)
                    && reason.equals(((UserStory) other).reason)
                    && status.equals(((UserStory) other).status);
        }
    }

    public String getUserStoryImportance() {
        return importance.getImportance();
    }

    public String getUserStoryReason() {
        return reason.getReason();
    }

    public String getUserStoryFunction() {
        return function.getFunction();
    }

    public String getUserStoryUser() {
        return user.getUser();
    }

    public String getUserStoryStatus() {
        return status.getStatus();
    }

    /**
     * Comparison between user stories. If two user stories are identical in all the string fields, then
     * it should be considered as the same story even if the importance level or status is different.
     * @param story
     */
    public boolean isSameUserStory(UserStory story) {
        if (story == this) {
            return true;
        } else { //check the string fields
            return user.equals(story.user)
                    && function.equals(story.function)
                    && reason.equals(story.reason);
        }
    }

    /**
     * Returns a clone of this user story.
     */
    public UserStory clone() {
        return new UserStory(this.importance.clone(), this.user.clone(), this.function.clone(),
                this.reason.clone(), this.status.clone());
    }

    /**
     * Compares the {@code UserStoryImportance} of this story and the {@code other} and returns true if higher
     */
    public boolean isHigherImportance(UserStory other) {
        return this.importance.isHigherImportance(other.importance);
    }

    /**
     * Utility method to sort user story by the user's name. If the two strings are equal then this method returns 0.
     * Else if the other string is lexicographically smaller, then return 1, else return -1.
     */
    public int isUserLexicographicallySmaller(UserStory other) {
        return getUserStoryUser().compareTo(other.getUserStoryUser());
    }

    @Override
    public int compareTo(UserStory other) {
        if (this.isHigherImportance(other)) {
            return -1;
        } else if (other.isHigherImportance(this)) {
            return 1;
        } else {
            return this.isUserLexicographicallySmaller(other);
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("User: ")
                .append(user.getUser())
                .append(" Function: ")
                .append(function.getFunction())
                .append(" Reason: ")
                .append(reason.getReason())
                .append(" Importance: ")
                .append(importance.getImportance());
        return builder.toString();
    }

    /**
     * Checks if the user story has the valid format by checking the relevant fields
     */
    public static boolean isValidUserStory(UserStory story) {
        return UserStoryUser.isValidName(story.user.getUser())
                && UserStoryImportance.isValidImportanceLevel(story.importance.getImportance())
                && UserStoryFunction.isValidDescription(story.function.getFunction())
                && Status.isValidStatus(story.status.getStatus());
    }

    /**
     * Updates the status of the user story to the new status
     */
    public void updateStatus(Status newStatus) {
        this.status = newStatus;
    }
}
