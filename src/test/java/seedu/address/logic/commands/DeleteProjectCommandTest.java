package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalProjects.getTypicalAddressBookWithProjects;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.project.Project;
import seedu.address.testutil.TypicalProjectNames;
import seedu.address.testutil.TypicalProjects;

/**
 * Contains integration tests (interaction with the Model, UndoCommand and RedoCommand) and unit tests for
 * {@code DeleteProjectCommand}.
 */
public class DeleteProjectCommandTest {

    private Model model = new ModelManager(getTypicalAddressBookWithProjects(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_validName_success() {
        Project projectToDelete = model.getProjectList().get(0);
        DeleteProjectCommand deleteProjectCommand = new DeleteProjectCommand(projectToDelete.getProjectName());

        String expectedMessage = String.format(DeleteProjectCommand.MESSAGE_DELETE_PROJECT_SUCCESS, projectToDelete);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteProject(projectToDelete);
        expectedModel.commitAddressBook();

        assertCommandSuccess(deleteProjectCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidName_throwsCommandException() {
        DeleteProjectCommand deleteProjectCommand = new DeleteProjectCommand(
                TypicalProjectNames.NON_EXISTENT_PROJECT_NAME);

        assertCommandFailure(deleteProjectCommand, model, commandHistory,
                Messages.MESSAGE_INVALID_PROJECT_NAME);
    }


    @Test
    public void executeUndoRedo_validName_success() throws Exception {
        Project projectToDelete = model.getProjectList().get(0);
        DeleteProjectCommand deleteProjectCommand = new DeleteProjectCommand(projectToDelete.getProjectName());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteProject(projectToDelete);
        expectedModel.commitAddressBook();

        // delete -> first project deleted
        deleteProjectCommand.execute(model, commandHistory);

        // undo -> reverts addressbook back to previous state and filtered project list to show all projects
        expectedModel.undoAddressBook();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first project deleted again
        expectedModel.redoAddressBook();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executeUndoRedo_invalidIndexUnfilteredList_failure() {
        DeleteProjectCommand deleteProjectCommand = new DeleteProjectCommand(
                TypicalProjectNames.NON_EXISTENT_PROJECT_NAME);

        // execution failed -> address book state not added into model
        assertCommandFailure(deleteProjectCommand, model, commandHistory,
                Messages.MESSAGE_INVALID_PROJECT_NAME);

        // single address book state in model -> undoCommand and redoCommand fail
        assertCommandFailure(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_FAILURE);
        assertCommandFailure(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_FAILURE);
    }


    @Test
    public void equals() {
        DeleteProjectCommand deleteAliceCommand = new DeleteProjectCommand(
                TypicalProjects.PROJECT_ALICE.getProjectName());
        DeleteProjectCommand deleteBensonCommand = new DeleteProjectCommand(
                TypicalProjects.PROJECT_BENSON.getProjectName());

        // same object -> returns true
        assertTrue(deleteAliceCommand.equals(deleteAliceCommand));

        // same values -> returns true
        DeleteProjectCommand deleteAliceCommandCopy = new DeleteProjectCommand(
                TypicalProjects.PROJECT_ALICE.getProjectName());
        assertTrue(deleteAliceCommand.equals(deleteAliceCommandCopy));

        // different types -> returns false
        assertFalse(deleteAliceCommand.equals(1));

        // null -> returns false
        assertFalse(deleteAliceCommand.equals(null));

        // different project -> returns false
        assertFalse(deleteAliceCommand.equals(deleteBensonCommand));
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoProject(Model model) {
        model.updateFilteredProjectList(p -> false);

        assertTrue(model.getFilteredProjectList().isEmpty());
    }
}
