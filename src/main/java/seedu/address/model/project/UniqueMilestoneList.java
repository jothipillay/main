package seedu.address.model.project;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.project.exceptions.DuplicateMilestoneException;
import seedu.address.model.project.exceptions.MilestoneNotFoundException;
import seedu.address.model.util.PocketProjectDate;

/**
 * A list of Milestones that enforces uniqueness between its elements and does not allow nulls.
 * A Milestone is considered unique by comparing using {@code Milestone#isSameMilestone(Milestone)}. As such, adding
 * and updating of Milestones uses Milestone#isSameMilestone(Milestone) for equality so as to ensure that the Milestone
 * being added or updated is unique in terms of identity in the UniqueMilestoneList. However, the removal of a
 * Milestone uses Milestone#equals(Object) so as to ensure that the Milestone with exactly the same fields will
 * be removed.
 *
 * Supports a minimal set of list operations.
 *
 * //@see Milestone#isSameMilestone(Milestone)
 */
public class UniqueMilestoneList implements Iterable<Milestone> {

    private final ObservableList<Milestone> internalList = FXCollections.observableArrayList();
    private final ObservableList<Milestone> internalUnmodifiableList =
        FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent Milestone as the given argument.
     */
    public boolean contains(Milestone toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameMilestone);
    }

    /**
     * Adds a Milestone to the list.
     * The Milestone must not already exist in the list.
     */
    public void add(Milestone toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateMilestoneException();
        }
        internalList.add(toAdd);
        Comparator<? super Milestone> comparator = new Comparator<Milestone>() {
            @Override
            public int compare(Milestone m1, Milestone m2) {
                return PocketProjectDate.DATE_STRING_COMPARATOR.compare(m1.getDate().date, m2.getDate().date);
            }
        };
        internalList.sort(comparator);
    }

    /**
     * Returns a clone of this UniqueMilestoneList object.
     */
    public UniqueMilestoneList clone() {
        UniqueMilestoneList newList = new UniqueMilestoneList();
        for (Milestone m: internalList) {
            newList.add(m.clone());
        }
        return newList;
    }

    /**
     * Removes the equivalent Milestone from the list.
     * The Milestone must exist in the list.
     */
    public void remove(Milestone toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new MilestoneNotFoundException();
        }
    }

    /**
     * Replace the {@code targetMilestone} in the list with the new {@code milestone}
     */
    public void setMilestone(Milestone targetMilestone, Milestone milestone) {
        if (!this.contains(targetMilestone)) {
            throw new MilestoneNotFoundException();
        }
        remove(targetMilestone);
        add(milestone);
    }

    public void setMilestones(UniqueMilestoneList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code milestones}.
     * {@code milestones} must not contain duplicate Milestones.
     */
    public void setMilestones(List<Milestone> milestones) {
        requireAllNonNull(milestones);
        if (!milestonesAreUnique(milestones)) {
            throw new DuplicateMilestoneException();
        }

        internalList.setAll(milestones);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Milestone> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    @Override
    public Iterator<Milestone> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof UniqueMilestoneList // instanceof handles nulls
            && internalList.equals(((UniqueMilestoneList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }


    /**
     * Returns true if {@code milestones} contains only unique milestones.
     */
    private boolean milestonesAreUnique(List<Milestone> milestones) {
        for (int i = 0; i < milestones.size() - 1; i++) {
            for (int j = i + 1; j < milestones.size(); j++) {
                if (milestones.get(i).isSameMilestone(milestones.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
