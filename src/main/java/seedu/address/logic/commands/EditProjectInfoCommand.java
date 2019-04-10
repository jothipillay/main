package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import static seedu.address.logic.parser.CliSyntax.PREFIX_CLIENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PROJECTS;

import java.util.Optional;

import seedu.address.commons.core.Messages;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.project.Client;
import seedu.address.model.project.Project;
import seedu.address.model.project.ProjectDescription;
import seedu.address.model.project.ProjectName;
import seedu.address.model.util.PocketProjectDate;


/**
 * Edits the information (name, deadline, client, description) of an existing project in the pocket project.
 */
public class EditProjectInfoCommand extends EditProjectCommand {

    public static final String EDIT_INFO_KEYWORD = "info";

    public static final String MESSAGE_USAGE = "Parameters: " + COMMAND_WORD + " PROJECT_NAME" + EDIT_INFO_KEYWORD
        + "[" + PREFIX_NAME + "NAME] "
        + "[" + PREFIX_DEADLINE + "11/12/2019] "
        + "[" + PREFIX_CLIENT + "John] "
        + "[" + PREFIX_DESCRIPTION + "An application to manage projects] "
        + "Please note that project start date cannot be edited.";

    public static final String MESSAGE_EDIT_PROJECT_SUCCESS = "Project edited successfully!";
    public static final String MESSAGE_NOT_EDITED = "Project is not edited";
    public static final String MESSAGE_DUPLICATE_PROJECT = "The project already exists.";

    private final ProjectName projectName;
    private final EditProjectDescriptor editProjectDescriptor;

    /**
     * @param projectName of the project in the project list to edit
     * @param editProjectDescriptor details to edit the project with
     */
    public EditProjectInfoCommand(ProjectName projectName, EditProjectDescriptor editProjectDescriptor) {
        requireNonNull(projectName);
        requireNonNull(editProjectDescriptor);

        this.projectName = projectName;
        this.editProjectDescriptor = new EditProjectDescriptor(editProjectDescriptor);
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        Project projectToEdit = model.getProjectWithName(projectName);

        if (projectToEdit == null) {
            throw new CommandException(Messages.MESSAGE_INVALID_PROJECT_NAME);
        }

        Project editedProject = createEditedProject(projectToEdit, editProjectDescriptor);

        if (PocketProjectDate.isEarlierThan(editedProject.getDeadline(), editedProject.getStartDate())) {
            throw new CommandException(PocketProjectDate.START_END_DATE_CONSTRAINTS);
        }

        if (!editedProject.isSameProject(projectToEdit) && model.hasProject(editedProject)) {
            throw new CommandException(MESSAGE_DUPLICATE_PROJECT);
        }

        model.setProject(projectToEdit, editedProject);
        model.updateFilteredProjectList(PREDICATE_SHOW_ALL_PROJECTS);
        model.commitPocketProject();
        return new CommandResult(String.format(MESSAGE_EDIT_PROJECT_SUCCESS, editedProject));
    }

    /**
     * Creates and returns a {@code project} with the details of {@code projectToEdit}
     * edited with {@code editProjectDescriptor}.
     */
    private static Project createEditedProject(Project projectToEdit, EditProjectDescriptor editProjectDescriptor) {
        assert projectToEdit != null;

        ProjectName updatedName = editProjectDescriptor.getProjectName().orElse(projectToEdit.getProjectName());
        PocketProjectDate updatedDeadline = editProjectDescriptor.getDeadline().orElse(projectToEdit.getDeadline());
        ProjectDescription updatedDescription = editProjectDescriptor.getDescription()
            .orElse(projectToEdit.getDescription());
        Client updatedClient = editProjectDescriptor.getClient().orElse(projectToEdit.getClient());

        return projectToEdit.editProject(updatedName, updatedClient, projectToEdit.getStartDate(), updatedDeadline,
            updatedDescription);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditProjectInfoCommand)) {
            return false;
        }

        // state check
        EditProjectInfoCommand e = (EditProjectInfoCommand) other;
        return projectName.equals(e.projectName)
            && editProjectDescriptor.equals(e.editProjectDescriptor);
    }

    /**
     * Stores the details to edit the project with. Each non-empty field value will replace the
     * corresponding field value of the project.
     */
    public static class EditProjectDescriptor {

        private ProjectName projectName;
        private PocketProjectDate deadline;
        private ProjectDescription description;
        private Client client;

        public EditProjectDescriptor() {
        }

        /**
         * Copy constructor.
         */
        public EditProjectDescriptor(EditProjectDescriptor toCopy) {
            setProjectName(toCopy.projectName);
            setDeadline(toCopy.deadline);
            setDescription(toCopy.description);
            setClient(toCopy.client);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(projectName, deadline, description, client);
        }

        public void setProjectName(ProjectName projectName) {
            this.projectName = projectName;
        }

        public Optional<ProjectName> getProjectName() {
            return Optional.ofNullable(projectName);
        }

        public void setDeadline(PocketProjectDate deadline) {
            this.deadline = deadline;
        }

        public Optional<PocketProjectDate> getDeadline() {
            return Optional.ofNullable(deadline);
        }

        public void setDescription(ProjectDescription description) {
            this.description = description;
        }

        public Optional<ProjectDescription> getDescription() {
            return Optional.ofNullable(description);
        }

        public void setClient(Client client) {
            this.client = client;
        }

        public Optional<Client> getClient() {
            return Optional.ofNullable(client);
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditProjectDescriptor)) {
                return false;
            }

            // state check
            EditProjectDescriptor e = (EditProjectDescriptor) other;

            return getProjectName().equals(e.getProjectName())
                && getDeadline().equals(e.getDeadline())
                && getDescription().equals(e.getDescription())
                && getClient().equals(e.getClient());
        }
    }
}

