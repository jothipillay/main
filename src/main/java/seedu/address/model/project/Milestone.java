package seedu.address.model.project;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.text.DateFormat;
import java.text.ParseException;

import javafx.collections.ObservableList;
import seedu.address.model.util.PocketProjectDate;

/**
 * Milestone achieved in the project timeline.
 */

public class Milestone {

    public static final String MESSAGE_CONSTRAINTS = "The milestone info must not be empty or consisting of only spaces"
            + " and the date given must be in DD/MM/YYYY format";
    public static final String MESSAGE_INVALID_STRING = "The milestone info must not be empty or consisting "
        + "of only spaces";

    public final Description milestone;
    public final PocketProjectDate date;
    public final UniqueProjectTaskList projectTasks;

    public Milestone(Description milestone, PocketProjectDate date) {
        this(milestone, date, new UniqueProjectTaskList());
    }

    public Milestone(Description milestone, PocketProjectDate date, UniqueProjectTaskList projectTasks) {
        requireAllNonNull(milestone, date, projectTasks);
        this.milestone = milestone;
        this.date = date;
        this.projectTasks = projectTasks;
    }

    /**
     * Adds the given project task to this milestone.
     */
    public void addTask(ProjectTask task) {
        this.projectTasks.add(task);
    }

    /**
     * Returns a clone of this Milestone object.
     */
    public Milestone clone() {

        return new Milestone(this.milestone, this.date, this.projectTasks.clone());
    }

    public Description getMilestone() {
        return milestone;
    }

    public PocketProjectDate getDate() {
        return date;
    }

    public ObservableList<ProjectTask> getProjectTaskList() {
        return this.projectTasks.asUnmodifiableObservableList();
    }

    /**
     * Returns true if both milestones have the same name and date.
     * This defines a weaker notion of equality between two milestones.
     */
    public boolean isSameMilestone(Milestone otherMilestone) {
        if (otherMilestone == this) {
            return true;
        }
        return otherMilestone != null
            && otherMilestone.getMilestone().equals(getMilestone())
            && otherMilestone.getDate().equals(getDate());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Milestone // instanceof handles nulls
                && milestone.equals(((Milestone) other).milestone)
                && date.equals(((Milestone) other).date)
                && projectTasks.equals(((Milestone) other).projectTasks)); // state check
    }
    @Override
    public String toString() {
        return this.milestone + " " + this.date;
    }

}


