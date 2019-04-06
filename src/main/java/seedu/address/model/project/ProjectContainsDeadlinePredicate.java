package seedu.address.model.project;

import seedu.address.model.util.PocketProjectDate;

import java.util.function.Predicate;

/**
 * Tests that a {@code Project]'s {@code deadline} is same or earlier than the deadline keyword given.
 */
public class ProjectContainsDeadlinePredicate implements Predicate<Project> {

    private final String keyword;

    public ProjectContainsDeadlinePredicate(String deadline) {
        assert(PocketProjectDate.isValidDate(deadline));
        this.keyword = deadline;
    }
    
    @Override
    public boolean test(Project project) {
        return project.getDeadline().isSameOrEarlierDate(keyword);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof ProjectContainsDeadlinePredicate // instanceof handles nulls
            && keyword.equals(((ProjectContainsDeadlinePredicate) other).keyword)); // state check
    }
}
