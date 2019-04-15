package seedu.address.model.project;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.INFO;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.project.exceptions.DuplicateUserStoryException;
import seedu.address.model.project.exceptions.UserStoryNotFoundException;

/**
 * A list of user stories that enforces uniqueness between its elements and does not allow nulls.
 * A user story is considered unique by comparing using {@code UserStory#isSameUserStory(UserStory)}. As such, adding
 * and updating of user stories uses UserStory#isSameUserStory(Story) for equality so as to ensure that the user story
 * being added or updated is unique in terms of identity in the SortedUserStoryList. However, the removal of a user
 * story uses UserStory#equals(Object) so as to ensure that the user story with exactly the same fields will be removed.
 *
 * In addition, the user stories stored in SortedUserStoryList is sorted by descending importance level upon addition.
 * This is to facilitate operations on the list which requires the stories to be ordered by importance level.
 *
 * Supports a minimal set of list operations.
 *
 * @see UserStory#isSameUserStory(UserStory)
 */
public class SortedUserStoryList implements Iterable<UserStory> {

    private static final Logger logger = LogsCenter.getLogger(SortedUserStoryList.class);

    private final ObservableList<UserStory> internalList = FXCollections.observableArrayList();
    private final ObservableList<UserStory> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent user story as the given argument.
     */
    public boolean contains(UserStory toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameUserStory);
    }

    /**
     * Adds a user story to the list based on its importance level.
     * Uses UserStoryImportance#isHigherImportance(UserStoryImportance) for comparison
     * The user story must not already exist in the list.
     */
    public void add(UserStory toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateUserStoryException();
        }

        internalList.add(toAdd);
        Collections.sort(internalList);
        logger.log(INFO, "Added new user story: " + toAdd.getUserStoryUser() + toAdd.getUserStoryFunction()
                + toAdd.getUserStoryReason() + toAdd.getUserStoryImportance() + toAdd.getUserStoryStatus());
    }

    /**
     * Removes the equivalent user story from the list.
     * The user story must exist in the list.
     */
    public void remove(UserStory toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new UserStoryNotFoundException();
        }
        logger.log(INFO, "Removed user story: " + toRemove.getUserStoryUser() + toRemove.getUserStoryFunction()
                + toRemove.getUserStoryReason() + toRemove.getUserStoryImportance() + toRemove.getUserStoryStatus());
    }

    public void setUserStories(SortedUserStoryList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
        Collections.sort(internalList);
    }

    /**
     * Replaces the contents of this list with {@code user stories}.
     * {@code stories} must not contain duplicate user stories.
     */
    public void setUserStories(List<UserStory> stories) {
        requireAllNonNull(stories);
        if (!userStoriesAreUnique(stories)) {
            throw new DuplicateUserStoryException();
        }

        internalList.setAll(stories);
        Collections.sort(internalList);
    }

    /**
     * Replaces the {@code targetStory} with the new {@code story}
     * {@code stories} must not contain duplicate user stories.
     */
    public void setUserStory(UserStory targetStory, UserStory story) {
        if (!this.contains(targetStory)) {
            throw new UserStoryNotFoundException();
        }
        remove(targetStory);
        add(story);
        Collections.sort(internalList);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<UserStory> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    /**
     * Returns a clone of this SortedUserStoryList object.
     */
    public SortedUserStoryList clone() {
        SortedUserStoryList newList = new SortedUserStoryList();
        for (UserStory story : internalList) {
            newList.add(story.clone());
        }
        return newList;
    }

    @Override
    public Iterator<UserStory> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof SortedUserStoryList // instanceof handles nulls
                && internalList.equals(((SortedUserStoryList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code stories} contains only unique user stories.
     */
    private boolean userStoriesAreUnique(List<UserStory> stories) {
        for (int i = 0; i < stories.size() - 1; i++) {
            for (int j = i + 1; j < stories.size(); j++) {
                if (stories.get(i).isSameUserStory(stories.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

}
