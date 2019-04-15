package systemtests;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.CLIENT_DESC_ZULU;
import static seedu.address.logic.commands.CommandTestUtil.DEADLINE_DESC_ZULU;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_CLIENT_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_DEADLINE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_START_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_ZULU;
import static seedu.address.logic.commands.CommandTestUtil.START_DESC_ZULU;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLIENT_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DEADLINE_ZULU;
import static seedu.address.logic.commands.CommandTestUtil.VALID_START_ZULU;
import static seedu.address.testutil.TypicalProjects.KEYWORD_MATCHING_YANKEE;
import static seedu.address.testutil.TypicalProjects.PROJECT_VICTOR;
import static seedu.address.testutil.TypicalProjects.PROJECT_WHISKEY;
import static seedu.address.testutil.TypicalProjects.PROJECT_XAVIER;
import static seedu.address.testutil.TypicalProjects.PROJECT_YANKEE;
import static seedu.address.testutil.TypicalProjects.PROJECT_ZULU;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.logic.commands.AddProjectCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.project.Client;
import seedu.address.model.project.Project;
import seedu.address.model.project.ProjectName;
import seedu.address.model.util.PocketProjectDate;
import seedu.address.testutil.ProjectBuilder;
import seedu.address.testutil.ProjectUtil;

public class AddProjectCommandSystemTest extends PocketProjectSystemTest {

    @Test
    public void addProject() {

        Model model = getProjectModel();
        String command;

        /* ------------------------ Perform add operations on the shown unfiltered list ----------------------------- */

        /* Case: add a project with only name, client and date to a non-empty pocket project, command with leading
         * spaces and trailing spaces
         * -> added
         */

        Project toAdd = PROJECT_ZULU;
        assertCommandSuccess(toAdd);

        /* Case: undo adding Project Zulu to the list -> Project Zulu deleted */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: redo adding Project Zulu to the list -> Project Zulu added again */
        command = RedoCommand.COMMAND_WORD;
        model.addProject(toAdd);
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: add a employee with all fields same as another employee in the pocket project except name -> added */
        toAdd = new Project(new ProjectName("Zululu"), new Client("Shunnizuka"),
                new PocketProjectDate("10/10/2010"), new PocketProjectDate("04/09/2023"));
        assertCommandSuccess(toAdd);

        /* Case: add to empty pocket project -> added */
        deleteAllProjects();
        assertCommandSuccess(toAdd);

        /* -------------------------- Perform add operation on the shown filtered list ------------------------------ */
        /* Case: filters the project list before adding -> added */
        assertCommandSuccess(PROJECT_YANKEE);
        assertCommandSuccess(PROJECT_WHISKEY);

        showProjectsWithName(KEYWORD_MATCHING_YANKEE);
        assertCommandSuccess(PROJECT_XAVIER);
        assertCommandSuccess(PROJECT_VICTOR);

        /* ----------------------------------- Perform invalid add project operations ------------------------------ */

        /* Case: add a duplicate employee -> rejected */
        command = ProjectUtil.getAddProjectCommand(PROJECT_VICTOR);
        assertCommandFailure(command, Messages.MESSAGE_DUPLICATE_PROJECT);

        /* Case: add a duplicate project except with different client -> rejected */
        toAdd = new ProjectBuilder(PROJECT_VICTOR).withClient(VALID_CLIENT_AMY).build();
        command = ProjectUtil.getAddProjectCommand(toAdd);
        assertCommandFailure(command, Messages.MESSAGE_DUPLICATE_PROJECT);

        /* Case: add a duplicate project except with different start date -> rejected */
        toAdd = new ProjectBuilder(PROJECT_VICTOR).withStartDate(VALID_START_ZULU).build();
        command = ProjectUtil.getAddProjectCommand(toAdd);
        assertCommandFailure(command, Messages.MESSAGE_DUPLICATE_PROJECT);

        /* Case: add a duplicate project except with different deadline -> rejected */
        toAdd = new ProjectBuilder(PROJECT_VICTOR).withDeadline(VALID_DEADLINE_ZULU).build();
        command = ProjectUtil.getAddProjectCommand(toAdd);
        assertCommandFailure(command, Messages.MESSAGE_DUPLICATE_PROJECT);

        /* Case: missing name -> rejected */
        command = AddProjectCommand.COMMAND_WORD + " " + AddProjectCommand.ADD_PROJECT_KEYWORD + CLIENT_DESC_ZULU
            + START_DESC_ZULU + DEADLINE_DESC_ZULU;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddProjectCommand.MESSAGE_USAGE));

        /* Case: missing client -> rejected */
        command = AddProjectCommand.COMMAND_WORD + " " + AddProjectCommand.ADD_PROJECT_KEYWORD + NAME_DESC_ZULU
            + START_DESC_ZULU + DEADLINE_DESC_ZULU;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddProjectCommand.MESSAGE_USAGE));

        /* Case: missing start date -> rejected */
        command = AddProjectCommand.COMMAND_WORD + " " + AddProjectCommand.ADD_PROJECT_KEYWORD + NAME_DESC_ZULU
            + CLIENT_DESC_ZULU + DEADLINE_DESC_ZULU;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddProjectCommand.MESSAGE_USAGE));

        /* Case: missing deadline -> rejected */
        command = AddProjectCommand.COMMAND_WORD + " " + AddProjectCommand.ADD_PROJECT_KEYWORD + NAME_DESC_ZULU
            + CLIENT_DESC_ZULU + START_DESC_ZULU;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddProjectCommand.MESSAGE_USAGE));

        /* Case: invalid keyword -> rejected */
        command = "adds " + ProjectUtil.getProjectDetails(toAdd);
        assertCommandFailure(command, Messages.MESSAGE_UNKNOWN_COMMAND);

        /* Case: invalid name -> rejected */
        command = AddProjectCommand.COMMAND_WORD + " " + AddProjectCommand.ADD_PROJECT_KEYWORD + INVALID_NAME_DESC
            + CLIENT_DESC_ZULU + START_DESC_ZULU + DEADLINE_DESC_ZULU;
        assertCommandFailure(command, ProjectName.MESSAGE_CONSTRAINTS);

        /* Case: invalid client -> rejected */
        command = AddProjectCommand.COMMAND_WORD + " " + AddProjectCommand.ADD_PROJECT_KEYWORD + NAME_DESC_ZULU
            + INVALID_CLIENT_DESC + START_DESC_ZULU + DEADLINE_DESC_ZULU;
        assertCommandFailure(command, Client.MESSAGE_CONSTRAINTS);

        /* Case: invalid start date -> rejected */
        command = AddProjectCommand.COMMAND_WORD + " " + AddProjectCommand.ADD_PROJECT_KEYWORD + NAME_DESC_ZULU
            + CLIENT_DESC_ZULU + INVALID_START_DESC + DEADLINE_DESC_ZULU;
        assertCommandFailure(command, PocketProjectDate.MESSAGE_CONSTRAINTS);

        /* Case: invalid deadline -> rejected */
        command = AddProjectCommand.COMMAND_WORD + " " + AddProjectCommand.ADD_PROJECT_KEYWORD + NAME_DESC_ZULU
            + CLIENT_DESC_ZULU + START_DESC_ZULU + INVALID_DEADLINE_DESC;
        assertCommandFailure(command, PocketProjectDate.MESSAGE_CONSTRAINTS);
    }

    /**
     * Executes the {@code AddProjectCommand} that adds {@code toAdd} to the model and asserts that the,<br>
     * 1. Command box displays an empty string.<br>
     * 2. Command box has the default style class.<br>
     * 3. Result display box displays the success message of executing {@code AddProjectCommand} with the details of
     * {@code toAdd}.<br>
     * 4. {@code Storage} and {@code ProjectListPanel} equal to the corresponding components in
     * the current model added with {@code toAdd}.<br>
     * 5. Browser url and selected card remain unchanged.<br>
     * 6. Status bar's sync status changes.<br>
     * Verifications 1, 3 and 4 are performed by
     * {@code PocketProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see PocketProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(Project toAdd) {
        assertCommandSuccess(ProjectUtil.getAddProjectCommand(toAdd), toAdd);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(Employee)}. Executes {@code command}
     * instead.
     * @see AddProjectCommandSystemTest#assertCommandSuccess(Project)
     */
    private void assertCommandSuccess(String command, Project toAdd) {
        Model expectedModel = getProjectModel();
        expectedModel.addProject(toAdd);
        String expectedResultMessage = String.format(AddProjectCommand.MESSAGE_ADD_PROJECT_SUCCESS, toAdd);

        assertCommandSuccess(command, expectedModel, expectedResultMessage);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Project)} except asserts that
     * the,<br>
     * 1. Result display box displays {@code expectedResultMessage}.<br>
     * 2. {@code Storage} and {@code ProjectListPanel} equal to the corresponding components in
     * {@code expectedModel}.<br>
     * @see AddProjectCommandSystemTest#assertCommandSuccess(String, Project)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and asserts that the,<br>
     * 1. Command box displays {@code command}.<br>
     * 2. Command box has the error style class.<br>
     * 3. Result display box displays {@code expectedResultMessage}.<br>
     * 4. {@code Storage} and {@code ProjectListPanel} remain unchanged.<br>
     * 5. Browser url, selected card and status bar remain unchanged.<br>
     * Verifications 1, 3 and 4 are performed by
     * {@code PocketProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see PocketProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();
        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
