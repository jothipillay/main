package systemtests;

import static seedu.address.logic.commands.CommandTestUtil.INVALID_DEADLINE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_MILESTONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PROJECT_NAME_ALICE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PROJECT_NAME_ALICE_HEY;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PROJECTS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PROJECT_MILESTONE;
import static seedu.address.testutil.TypicalProjectNames.TYPICAL_PROJECT_NAME_INDEX_1;
import static seedu.address.testutil.TypicalProjects.PROJECT_ALICE;

import java.util.Arrays;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditProjectCommand;
import seedu.address.logic.commands.EditProjectMilestoneCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.project.Description;
import seedu.address.model.project.Milestone;
import seedu.address.model.project.Project;
import seedu.address.model.project.ProjectName;
import seedu.address.model.util.PocketProjectDate;
import seedu.address.testutil.ProjectBuilder;
import seedu.address.testutil.TypicalMilestones;

public class EditProjectMilestoneCommandSystemTest extends PocketProjectSystemTest {

    @Test
    public void edit() {
        Model model = getModel();

        /* ----------------- Performing edit operation while an unfiltered list is being shown ---------------------- */

        /* Case: edit all fields, command with leading spaces, trailing spaces and multiple spaces between each field
         * -> edited
         */
        ProjectName name = TYPICAL_PROJECT_NAME_INDEX_1;
        String command = EditProjectMilestoneCommand.COMMAND_WORD + " "
            + EditProjectMilestoneCommand.EDIT_PROJECT_KEYWORD + " " + VALID_PROJECT_NAME_ALICE_HEY + " "
            + EditProjectMilestoneCommand.EDIT_MILESTONE_KEYWORD + "   " + INDEX_FIRST_PROJECT_MILESTONE.getOneBased()
            + "   m/" + TypicalMilestones.TYPICAL_MILESTONE_COMPLETED_UG.getMilestoneDescription() + "    d/"
            + TypicalMilestones.TYPICAL_MILESTONE_COMPLETED_UG.getDate();
        Project editedProject = new ProjectBuilder(PROJECT_ALICE).withMilestones(Arrays.asList(
            TypicalMilestones.TYPICAL_MILESTONE_COMPLETED_UG , TypicalMilestones.TYPICAL_MILESTONE_END)).build();

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
        command = EditProjectMilestoneCommand.COMMAND_WORD + " "
            + EditProjectMilestoneCommand.EDIT_PROJECT_KEYWORD + " " + VALID_PROJECT_NAME_ALICE_HEY + " "
            + EditProjectMilestoneCommand.EDIT_MILESTONE_KEYWORD + " " + INDEX_FIRST_PROJECT_MILESTONE.getOneBased()
            + " m/" + TypicalMilestones.TYPICAL_MILESTONE_START.getMilestoneDescription();
        editedProject = new ProjectBuilder(PROJECT_ALICE).withMilestones(Arrays.asList(
            new Milestone(TypicalMilestones.TYPICAL_MILESTONE_START.getMilestoneDescription(),
                TypicalMilestones.TYPICAL_MILESTONE_COMPLETED_UG.getDate()),
            TypicalMilestones.TYPICAL_MILESTONE_END)).build();
        assertCommandSuccess(command, name, editedProject);

        /* --------------------------------- Performing invalid edit operation -------------------------------------- */

        /* Case: invalid project name, project does not exist -> rejected */
        assertCommandFailure(EditProjectMilestoneCommand.COMMAND_WORD + " "
                + EditProjectMilestoneCommand.EDIT_PROJECT_KEYWORD + " " + VALID_PROJECT_NAME_ALICE + " "
                + EditProjectMilestoneCommand.EDIT_MILESTONE_KEYWORD + " " + INDEX_FIRST_PROJECT_MILESTONE.getOneBased()
                + " m/" + TypicalMilestones.TYPICAL_MILESTONE_START.getMilestoneDescription(),
            Messages.MESSAGE_INVALID_PROJECT_NAME);

        /* Case: missing projectName -> rejected */
        assertCommandFailure(EditProjectMilestoneCommand.COMMAND_WORD + " "
                + EditProjectMilestoneCommand.EDIT_PROJECT_KEYWORD + " "
                + EditProjectMilestoneCommand.EDIT_MILESTONE_KEYWORD + " " + INDEX_FIRST_PROJECT_MILESTONE.getOneBased()
                + " m/" + TypicalMilestones.TYPICAL_MILESTONE_START.getMilestoneDescription(),
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditProjectCommand.MESSAGE_USAGE));

        /* Case: missing all fields -> rejected */
        assertCommandFailure(EditProjectMilestoneCommand.COMMAND_WORD + " "
            + EditProjectMilestoneCommand.EDIT_PROJECT_KEYWORD + " m/"
            + TypicalMilestones.TYPICAL_MILESTONE_START.getMilestoneDescription(), String.format(
            Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditProjectCommand.MESSAGE_USAGE));

        /* Case: invalid milestone description in the argument -> rejected */
        assertCommandFailure(EditProjectMilestoneCommand.COMMAND_WORD + " "
            + EditProjectMilestoneCommand.EDIT_PROJECT_KEYWORD + " " + VALID_PROJECT_NAME_ALICE_HEY + " "
            + EditProjectMilestoneCommand.EDIT_MILESTONE_KEYWORD + " " + INDEX_FIRST_PROJECT_MILESTONE.getOneBased()
            + INVALID_MILESTONE_DESC, Description.MESSAGE_CONSTRAINTS);

        /* Case: invalid date -> rejected */
        assertCommandFailure(EditProjectMilestoneCommand.COMMAND_WORD + " "
            + EditProjectMilestoneCommand.EDIT_PROJECT_KEYWORD + " " + VALID_PROJECT_NAME_ALICE_HEY + " "
            + EditProjectMilestoneCommand.EDIT_MILESTONE_KEYWORD + " " + INDEX_FIRST_PROJECT_MILESTONE.getOneBased()
            + INVALID_DEADLINE_DESC, PocketProjectDate.MESSAGE_CONSTRAINTS);

        /* Case: edit a Project with new values same as another Project's values -> rejected */
        command = EditProjectMilestoneCommand.COMMAND_WORD + " "
            + EditProjectMilestoneCommand.EDIT_PROJECT_KEYWORD + " " + VALID_PROJECT_NAME_ALICE_HEY + " "
            + EditProjectMilestoneCommand.EDIT_MILESTONE_KEYWORD + " " + INDEX_FIRST_PROJECT_MILESTONE.getOneBased()
            + " m/" + TypicalMilestones.TYPICAL_MILESTONE_END.getMilestoneDescription() + " d/"
            + TypicalMilestones.TYPICAL_MILESTONE_END.getDate();
        assertCommandFailure(command, EditProjectMilestoneCommand.MESSAGE_DUPLICATE_MILESTONE);

    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Index, Project, Index)} except that
     * the browser url and selected card remain unchanged.
     * @param toEdit the projectName of a project inside the current model's list
     * @see systemtests.EditProjectMilestoneCommandSystemTest#assertCommandSuccess(String, ProjectName, Project, Index)
     */
    private void assertCommandSuccess(String command, ProjectName toEdit, Project editedProject) {
        assertCommandSuccess(command, toEdit, editedProject, null);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String, Index)} and in addition,<br>
     * 1. Asserts that result display box displays the success message of executing
     * {@code EditProjectMilestoneCommand}.<br>
     * 2. Asserts that the model related components are updated to reflect the Project with ProjectName {@code toEdit}
     * being updated to values specified {@code editedProject}.<br>
     * @param toEdit the index of the current model's filtered list.
     * @see systemtests.EditProjectMilestoneCommandSystemTest#assertCommandSuccess(String, Model, String, Index)
     */
    private void assertCommandSuccess(String command, ProjectName toEdit, Project editedProject,
                                      Index expectedSelectedCardIndex) {
        Model expectedModel = getModel();
        Project toEditProject = expectedModel.getProjectWithName(toEdit);
        expectedModel.setProject(toEditProject, editedProject);
        expectedModel.updateFilteredProjectList(PREDICATE_SHOW_ALL_PROJECTS);

        assertCommandSuccess(command, expectedModel,
            String.format(EditProjectMilestoneCommand.MESSAGE_EDIT_MILESTONE_SUCCESS,
                editedProject.getMilestones().get(0)), expectedSelectedCardIndex);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String, Index)} except that the
     * browser url and selected card remain unchanged.
     * @see systemtests.EditProjectMilestoneCommandSystemTest#assertCommandSuccess(String, Model, String, Index)
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

