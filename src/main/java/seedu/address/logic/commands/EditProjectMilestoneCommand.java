package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.project.Description;
import seedu.address.model.project.Milestone;
import seedu.address.model.project.Project;
import seedu.address.model.project.ProjectName;
import seedu.address.model.project.exceptions.DateNotInRangeException;
import seedu.address.model.util.PocketProjectDate;

/**
 * Edit the existing milestone of an existing project in the pocket project.
 */
public class EditProjectMilestoneCommand extends EditProjectCommand {

    public static final String EDIT_MILESTONE_KEYWORD = "milestone";

    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + EDIT_PROJECT_KEYWORD + " PROJ_NAME "
        + EDIT_MILESTONE_KEYWORD + " INDEX [m/MILESTONE] [d/DATE]\n"
        + "edits the milstone at INDEX in the project";

    public static final String MESSAGE_EDIT_MILESTONE_SUCCESS = "Edited Milestone: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit the milestone must be provided";
    public static final String MESSAGE_DUPLICATE_MILESTONE = "The milestone already exist";

    private final ProjectName projectName;
    private final Index milestoneIndex;
    private final EditMilestoneDescriptor editMilestoneDescriptor;

    public EditProjectMilestoneCommand(ProjectName name, Index index, EditMilestoneDescriptor editMilestoneDescriptor) {
        requireNonNull(name);
        requireNonNull(index);
        requireNonNull(editMilestoneDescriptor);

        this.projectName = name;
        this.milestoneIndex = index;
        this.editMilestoneDescriptor = editMilestoneDescriptor;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {

        requireNonNull(model);

        Project projectToEdit = model.getProjectWithName(projectName);

        if (projectToEdit == null) {
            throw new CommandException(Messages.MESSAGE_INVALID_PROJECT_NAME);
        }

        ObservableList<Milestone> milestonesList = projectToEdit.getMilestones();

        if (milestoneIndex.getZeroBased() >= milestonesList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_MILESTONE_DISPLAYED_INDEX);
        }

        Milestone milestoneToEdit = milestonesList.get(milestoneIndex.getZeroBased());
        Milestone editedMilestone = createEditedMilestone(milestoneToEdit, editMilestoneDescriptor);

        try {
            if (!projectToEdit.isValidMilestoneDate(editedMilestone.getDate())) {
                throw new DateNotInRangeException();
            }
        } catch (DateNotInRangeException e) {
            throw new CommandException(Messages.INVALID_MILESTONE_DATE);
        }

        if (!milestoneToEdit.isSameMilestone(editedMilestone) && milestonesList.contains(editedMilestone)) {
            throw new CommandException(MESSAGE_DUPLICATE_MILESTONE);
        }

        projectToEdit.setMilestone(milestoneToEdit, editedMilestone);

        model.commitPocketProject();

        return new CommandResult(String.format(MESSAGE_EDIT_MILESTONE_SUCCESS, editedMilestone));
    }

    /**
     * Creates and returns a {@code Milestone} with the details of {@code milestoneToedit} edited
     * with {@code editMilestoneDescriptor}.
     */
    private static Milestone createEditedMilestone(Milestone milestoneToedit,
                                                   EditMilestoneDescriptor editMilestoneDescriptor) {

        assert milestoneToedit != null;

        Description milestoneDesc = editMilestoneDescriptor.getMilestoneDesc()
            .orElse(milestoneToedit.getMilestoneDescription());
        PocketProjectDate date = editMilestoneDescriptor.getDate().orElse(milestoneToedit.getDate());

        return milestoneToedit.editMilestone(milestoneDesc, date);

    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditProjectMilestoneCommand)) {
            return false;
        }

        // state check
        EditProjectMilestoneCommand e = (EditProjectMilestoneCommand) other;
        return projectName.equals(e.projectName)
            && milestoneIndex.equals(e.milestoneIndex)
            && editMilestoneDescriptor.equals(e.editMilestoneDescriptor);
    }

    /**
     * Stores the details to edit the milestone with. Each non-empty field will replace the corresponding field value
     * of the milestone.
     */
    public static class EditMilestoneDescriptor {
        private Description milestoneDesc;
        private PocketProjectDate date;

        public EditMilestoneDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code skills} is used internally.
         */
        public EditMilestoneDescriptor(EditProjectMilestoneCommand.EditMilestoneDescriptor toCopy) {
            setDate(toCopy.date);
            setMilestoneDesc(toCopy.milestoneDesc);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(milestoneDesc, date);
        }

        public void setMilestoneDesc(Description milestone) {
            this.milestoneDesc = milestone;
        }

        public Optional<Description> getMilestoneDesc() {
            return Optional.ofNullable(milestoneDesc);
        }

        public void setDate(PocketProjectDate date) {
            this.date = date;
        }

        public Optional<PocketProjectDate> getDate() {
            return Optional.ofNullable(date);
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditProjectMilestoneCommand.EditMilestoneDescriptor)) {
                return false;
            }

            // state check
            EditProjectMilestoneCommand.EditMilestoneDescriptor e =
                (EditProjectMilestoneCommand.EditMilestoneDescriptor) other;

            return getMilestoneDesc().equals(e.getMilestoneDesc())
                && getDate().equals(e.getDate());
        }
    }
}

