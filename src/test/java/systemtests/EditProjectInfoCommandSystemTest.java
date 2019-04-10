package systemtests;

import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.CLIENT_DESC_ZULU;
import static seedu.address.logic.commands.CommandTestUtil.DEADLINE_DESC_ZULU;
import static seedu.address.logic.commands.CommandTestUtil.DESCRIPTION_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_CLIENT_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_DEADLINE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_DESCRIPTION_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PROJECT_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_ALICE;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_ZULU;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLIENT_ZULU;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DEADLINE_ZULU;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DESCRIPTION;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PROJECT_NAME_ALICE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PROJECT_NAME_ALICE_HEY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PROJECT_NAME_ZULU;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PROJECTS;
import static seedu.address.testutil.TypicalEmployees.BENSON;
import static seedu.address.testutil.TypicalProjectNames.TYPICAL_PROJECT_NAME_INDEX_1;
import static seedu.address.testutil.TypicalProjects.PROJECT_ZULU;

import java.util.Arrays;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditProjectCommand;
import seedu.address.logic.commands.EditProjectInfoCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.employee.Employee;
import seedu.address.model.project.Client;
import seedu.address.model.project.Description;
import seedu.address.model.project.Project;
import seedu.address.model.project.ProjectName;
import seedu.address.model.util.PocketProjectDate;
import seedu.address.testutil.ProjectBuilder;
import seedu.address.testutil.TypicalEmployees;
import seedu.address.testutil.TypicalMilestones;
import seedu.address.testutil.TypicalUserStories;

public class EditProjectInfoCommandSystemTest extends PocketProjectSystemTest {

    @Test
    public void edit() {
        Model model = getModel();

        /* ----------------- Performing edit operation while an unfiltered list is being shown ---------------------- */

        /* Case: edit all fields, command with leading spaces, trailing spaces and multiple spaces between each field
         * -> edited
         */
        ProjectName name = TYPICAL_PROJECT_NAME_INDEX_1;
        String command = EditProjectInfoCommand.COMMAND_WORD + " " + EditProjectInfoCommand.EDIT_PROJECT_KEYWORD
            + " " + VALID_PROJECT_NAME_ALICE_HEY + " " + EditProjectInfoCommand.EDIT_INFO_KEYWORD + "   "
            + NAME_DESC_ZULU + "     " + CLIENT_DESC_ZULU + "  " + DEADLINE_DESC_ZULU + " " + DESCRIPTION_DESC + " ";
        Project editedProject = new ProjectBuilder().withProjectName(VALID_PROJECT_NAME_ZULU)
            .withClient(VALID_CLIENT_ZULU).withStartDate("10/10/2010").withDeadline(VALID_DEADLINE_ZULU)
            .withDescrption(VALID_DESCRIPTION).withEmployees(Arrays.asList(BENSON, TypicalEmployees.CARL))
            .withMilestones(Arrays.asList(TypicalMilestones.TYPICAL_MILESTONE_START,
                TypicalMilestones.TYPICAL_MILESTONE_END))
            .withUserStories(Arrays.asList(TypicalUserStories.USER_STORY_FIRST_MANAGER)).build();

        assertCommandSuccess(command, name, editedProject);

        /* Case: undo editing the last Project in the list -> last Project restored */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: redo editing the last Project in the list -> last Project edited again */
        command = RedoCommand.COMMAND_WORD;
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        model.setProject(getModel().getProjectWithName(name), editedProject);
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: edit a Project with new values same as existing values -> edited */
        name = PROJECT_ZULU.getProjectName();
        command = EditProjectInfoCommand.COMMAND_WORD + " " + EditProjectInfoCommand.EDIT_PROJECT_KEYWORD + " "
            + VALID_PROJECT_NAME_ZULU + " " + EditProjectInfoCommand.EDIT_INFO_KEYWORD + NAME_DESC_ALICE
            + CLIENT_DESC_ZULU + DEADLINE_DESC_ZULU + DESCRIPTION_DESC;
        editedProject = new ProjectBuilder(editedProject).withProjectName(VALID_PROJECT_NAME_ALICE).build();
        assertCommandSuccess(command, name, editedProject);

        /* --------------------------------- Performing invalid edit operation -------------------------------------- */

        /* Case: invalid projectName -> rejected */
        assertCommandFailure(EditProjectInfoCommand.COMMAND_WORD + " "
                + EditProjectInfoCommand.EDIT_PROJECT_KEYWORD + " #@!#" + EditProjectInfoCommand.EDIT_INFO_KEYWORD
                + NAME_DESC_AMY ,
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditProjectCommand.MESSAGE_USAGE));

        /* Case: missing projectName -> rejected */
        assertCommandFailure(EditProjectInfoCommand.COMMAND_WORD + " "
                + EditProjectInfoCommand.EDIT_PROJECT_KEYWORD + " " + EditProjectInfoCommand.EDIT_INFO_KEYWORD
                + NAME_DESC_BOB,
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditProjectCommand.MESSAGE_USAGE));

        /* Case: missing all fields -> rejected */
        assertCommandFailure(EditProjectInfoCommand.COMMAND_WORD + " "
            + EditProjectInfoCommand.EDIT_PROJECT_KEYWORD + " " + VALID_PROJECT_NAME_ALICE + " "
            + EditProjectInfoCommand.EDIT_INFO_KEYWORD, String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT,
            EditProjectCommand.MESSAGE_USAGE));

        /* Case: invalid projectName in the argument -> rejected */
        assertCommandFailure(EditProjectInfoCommand.COMMAND_WORD + " "
            + EditProjectInfoCommand.EDIT_PROJECT_KEYWORD + " " + VALID_PROJECT_NAME_ALICE + " "
            + EditProjectInfoCommand.EDIT_INFO_KEYWORD + INVALID_PROJECT_NAME_DESC, ProjectName.MESSAGE_CONSTRAINTS);

        /* Case: invalid description -> rejected */
        assertCommandFailure(EditProjectInfoCommand.COMMAND_WORD + " "
            + EditProjectInfoCommand.EDIT_PROJECT_KEYWORD + " " + VALID_PROJECT_NAME_ALICE + " "
            + EditProjectInfoCommand.EDIT_INFO_KEYWORD + INVALID_DESCRIPTION_DESC, Description.MESSAGE_CONSTRAINTS);

        /* Case: invalid client -> rejected */
        assertCommandFailure(EditProjectInfoCommand.COMMAND_WORD + " "
                + EditProjectInfoCommand.EDIT_PROJECT_KEYWORD + " " + VALID_PROJECT_NAME_ALICE + " "
                + EditProjectInfoCommand.EDIT_INFO_KEYWORD + INVALID_CLIENT_DESC,
            Client.MESSAGE_CONSTRAINTS);

        /* Case: invalid deadline -> rejected */
        assertCommandFailure(EditProjectInfoCommand.COMMAND_WORD + " "
                + EditProjectInfoCommand.EDIT_PROJECT_KEYWORD + " " + VALID_PROJECT_NAME_ALICE + " "
                + EditProjectInfoCommand.EDIT_INFO_KEYWORD + INVALID_DEADLINE_DESC,
            PocketProjectDate.MESSAGE_CONSTRAINTS);

        /* Case: edit a Project with new values same as another Project's values -> rejected */
        command = EditProjectInfoCommand.COMMAND_WORD + " " + EditProjectInfoCommand.EDIT_PROJECT_KEYWORD + " "
            + "" + "Project Benson " + EditProjectInfoCommand.EDIT_INFO_KEYWORD + NAME_DESC_ALICE + CLIENT_DESC_ZULU
            + DEADLINE_DESC_ZULU + DESCRIPTION_DESC;
        assertCommandFailure(command, EditProjectInfoCommand.MESSAGE_DUPLICATE_PROJECT);

    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Index, Project, Index)} except that
     * the browser url and selected card remain unchanged.
     * @param toEdit the projectName of a project inside the current model's list
     * @see EditProjectInfoCommandSystemTest#assertCommandSuccess(String, ProjectName, Project, Index)
     */
    private void assertCommandSuccess(String command, ProjectName toEdit, Project editedProject) {
        assertCommandSuccess(command, toEdit, editedProject, null);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String, Index)} and in addition,<br>
     * 1. Asserts that result display box displays the success message of executing {@code EditProjectInfoCommand}.<br>
     * 2. Asserts that the model related components are updated to reflect the Project with ProjectName {@code toEdit}
     * being updated to values specified {@code editedProject}.<br>
     * @param toEdit the index of the current model's filtered list.
     * @see EditProjectInfoCommandSystemTest#assertCommandSuccess(String, Model, String, Index)
     */
    private void assertCommandSuccess(String command, ProjectName toEdit, Project editedProject,
                                      Index expectedSelectedCardIndex) {
        Model expectedModel = getModel();
        Project toEditProject = expectedModel.getProjectWithName(toEdit);
        expectedModel.setProject(toEditProject, editedProject);
        expectedModel.updateFilteredProjectList(PREDICATE_SHOW_ALL_PROJECTS);

        //if no change made to the Project then don't need to check
        if (!toEditProject.equals(editedProject)) {
            assertTrue(assertProjectNameInEmployeeEditSuccess(expectedModel, toEditProject));
        }

        assertCommandSuccess(command, expectedModel,
            EditProjectInfoCommand.MESSAGE_EDIT_PROJECT_SUCCESS, expectedSelectedCardIndex);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String, Index)} except that the
     * browser url and selected card remain unchanged.
     * @see EditProjectInfoCommandSystemTest#assertCommandSuccess(String, Model, String, Index)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        assertCommandSuccess(command, expectedModel, expectedResultMessage, null);
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays an empty string.<br>
     * 2. Asserts that the result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the browser url and selected card update accordingly depending on the card at
     * {@code expectedSelectedCardIndex}.<br>
     * 4. Asserts that the status bar's sync status changes.<br>
     * 5. Asserts that the command box has the default style class.<br>
     * Verifications 1 and 2 are performed by
     * {@code PocketProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see PocketProjectSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     * @see PocketProjectSystemTest#assertSelectedCardChanged(Index)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage,
                                      Index expectedSelectedCardIndex) {
        executeCommand(command);
        expectedModel.updateFilteredProjectList(PREDICATE_SHOW_ALL_PROJECTS);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertCommandBoxShowsDefaultStyle();
        if (expectedSelectedCardIndex != null) {
            assertSelectedCardChanged(expectedSelectedCardIndex);
        } else {
            assertSelectedCardUnchanged();
        }
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * To check if the ProjectName in the employee's Project Name list is also edited
     * by checking if the old instance of the ProjectName still exists
     */
    private boolean assertProjectNameInEmployeeEditSuccess(Model expected, Project toEdit) {

        for (Employee em: expected.getEmployeeList()) {
            if (toEdit.getEmployees().contains(em) && em.getCurrentProjects().contains(toEdit.getProjectName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays {@code command}.<br>
     * 2. Asserts that result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the browser url, selected card and status bar remain unchanged.<br>
     * 4. Asserts that the command box has the error style.<br>
     * Verifications 1 and 2 are performed by
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
