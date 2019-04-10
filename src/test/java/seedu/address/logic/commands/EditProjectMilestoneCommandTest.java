package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_MILESTONE_1;
import static seedu.address.logic.commands.CommandTestUtil.DESC_MILESTONE_2;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CLIENT_ALICE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DEADLINE_ALICE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PROJECT_NAME_ALICE_HEY;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalEmployees.BENSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PROJECT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PROJECT_MILESTONE;
import static seedu.address.testutil.TypicalIndexes.INDEX_INVALID_PROJECT_MILESTONE;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PROJECT_MILESTONE;
import static seedu.address.testutil.TypicalMilestones.TYPICAL_MILESTONE_COMPLETED_UG;
import static seedu.address.testutil.TypicalMilestones.TYPICAL_MILESTONE_END;
import static seedu.address.testutil.TypicalMilestones.TYPICAL_MILESTONE_START;
import static seedu.address.testutil.TypicalProjectNames.TYPICAL_PROJECT_NAME_INDEX_1;
import static seedu.address.testutil.TypicalProjects.getTypicalPocketProjectWithProjects;

import java.util.Arrays;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.PocketProject;
import seedu.address.model.UserPrefs;
import seedu.address.model.project.Description;
import seedu.address.model.project.Milestone;
import seedu.address.model.project.Project;
import seedu.address.model.project.ProjectName;
import seedu.address.testutil.EditMilestoneDescriptorBuilder;
import seedu.address.testutil.ProjectBuilder;
import seedu.address.testutil.TypicalEmployees;
import seedu.address.testutil.TypicalUserStories;

public class EditProjectMilestoneCommandTest {

    private Model model = new ModelManager(getTypicalPocketProjectWithProjects(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    //default builder with all the fields except milestones, to be used in the tests to build edited project
    private ProjectBuilder defaultProjBuilder = new ProjectBuilder().withProjectName(VALID_PROJECT_NAME_ALICE_HEY)
        .withClient(VALID_CLIENT_ALICE).withDeadline(VALID_DEADLINE_ALICE).withDescrption(
            "An application for Alice software hello").withEmployees(Arrays.asList(BENSON, TypicalEmployees.CARL))
        .withUserStories(Arrays.asList(TypicalUserStories.USER_STORY_FIRST_MANAGER));

    @Test
    public void execute_allFieldsSpecified_success() {

        Project editedProject = defaultProjBuilder.withMilestones(Arrays.asList(
            TYPICAL_MILESTONE_COMPLETED_UG, TYPICAL_MILESTONE_END)).build();

        EditProjectMilestoneCommand.EditMilestoneDescriptor descriptor = new EditMilestoneDescriptorBuilder(
            TYPICAL_MILESTONE_COMPLETED_UG).build();
        EditProjectMilestoneCommand editMilestoneCommand = new EditProjectMilestoneCommand(
            new ProjectName(VALID_PROJECT_NAME_ALICE_HEY) , INDEX_FIRST_PROJECT_MILESTONE, descriptor);

        Model expectedModel = new ModelManager(new PocketProject(model.getPocketProject()), new UserPrefs());
        expectedModel.setProject(model.getFilteredProjectList().get(0), editedProject);
        expectedModel.commitPocketProject();

        String expectedMessage = String.format(EditProjectMilestoneCommand.MESSAGE_EDIT_MILESTONE_SUCCESS ,
            TYPICAL_MILESTONE_COMPLETED_UG);

        assertCommandSuccess(editMilestoneCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldSpecified_success() {

        Milestone editedMilestone = new Milestone(new Description("hello"),
            TYPICAL_MILESTONE_START.getDate());

        Project editedProject = defaultProjBuilder.withMilestones(Arrays.asList(editedMilestone,
            TYPICAL_MILESTONE_END)).build();
        EditProjectMilestoneCommand.EditMilestoneDescriptor descriptor = new EditMilestoneDescriptorBuilder(
            editedMilestone).build();
        EditProjectMilestoneCommand editMilestoneCommand = new EditProjectMilestoneCommand(
            TYPICAL_PROJECT_NAME_INDEX_1, INDEX_FIRST_PROJECT_MILESTONE, descriptor);

        Model expectedModel = new ModelManager(new PocketProject(model.getPocketProject()), new UserPrefs());
        expectedModel.setProject(model.getFilteredProjectList().get(0), editedProject);
        expectedModel.commitPocketProject();

        String expectedMessage = String.format(EditProjectMilestoneCommand.MESSAGE_EDIT_MILESTONE_SUCCESS ,
            editedMilestone);

        assertCommandSuccess(editMilestoneCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecified_success() {
        EditProjectMilestoneCommand editMilestoneCommand = new EditProjectMilestoneCommand(TYPICAL_PROJECT_NAME_INDEX_1,
            INDEX_FIRST_PROJECT_MILESTONE, new EditProjectMilestoneCommand.EditMilestoneDescriptor());

        String expectedMessage = String.format(EditProjectMilestoneCommand.MESSAGE_EDIT_MILESTONE_SUCCESS,
            TYPICAL_MILESTONE_START);

        Model expectedModel = new ModelManager(new PocketProject(model.getPocketProject()), new UserPrefs());
        expectedModel.commitPocketProject();

        assertCommandSuccess(editMilestoneCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateMilestone_failure() {

        Project firstProject = model.getFilteredProjectList().get(INDEX_FIRST_PROJECT.getZeroBased());
        Milestone firstMilestone = firstProject.getMilestones().get(INDEX_FIRST_PROJECT_MILESTONE.getZeroBased());

        EditProjectMilestoneCommand.EditMilestoneDescriptor descriptor =
            new EditMilestoneDescriptorBuilder(firstMilestone).build();
        EditProjectMilestoneCommand editProjectMilestoneCommand = new EditProjectMilestoneCommand(firstProject
            .getProjectName(), INDEX_SECOND_PROJECT_MILESTONE, descriptor);

        assertCommandFailure(editProjectMilestoneCommand, model, commandHistory,
            EditProjectMilestoneCommand.MESSAGE_DUPLICATE_MILESTONE);
    }

    @Test
    public void execute_invalidProjectName_failure() {
        EditProjectMilestoneCommand.EditMilestoneDescriptor descriptor = new EditMilestoneDescriptorBuilder()
            .withMilestoneDesc("Project starts").build();
        EditProjectMilestoneCommand editProjectMilestoneCommand = new EditProjectMilestoneCommand(
            new ProjectName("wheee"), INDEX_FIRST_PROJECT_MILESTONE, descriptor);

        assertCommandFailure(editProjectMilestoneCommand, model, commandHistory,
            Messages.MESSAGE_INVALID_PROJECT_NAME);
    }

    @Test
    public void execute_invalidMilestoneIndex_failure() {
        EditProjectMilestoneCommand.EditMilestoneDescriptor descriptor = new EditMilestoneDescriptorBuilder()
            .withMilestoneDesc("Project starts").build();
        EditProjectMilestoneCommand editProjectMilestoneCommand = new EditProjectMilestoneCommand(
            new ProjectName(VALID_PROJECT_NAME_ALICE_HEY), INDEX_INVALID_PROJECT_MILESTONE, descriptor);

        assertCommandFailure(editProjectMilestoneCommand, model, commandHistory,
            Messages.MESSAGE_INVALID_MILESTONE_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidMilestoneDate_failure() {
        EditProjectMilestoneCommand.EditMilestoneDescriptor descriptor = new EditMilestoneDescriptorBuilder()
            .withDate("01/01/2001").build();
        EditProjectMilestoneCommand editProjectMilestoneCommand = new EditProjectMilestoneCommand(
            new ProjectName(VALID_PROJECT_NAME_ALICE_HEY), INDEX_FIRST_PROJECT_MILESTONE, descriptor);

        assertCommandFailure(editProjectMilestoneCommand, model, commandHistory,
            Messages.INVALID_MILESTONE_DATE);
    }

    @Test
    public void executeUndoRedo_validFields_success() throws CommandException {

        Project editedProject = defaultProjBuilder.withMilestones(Arrays.asList(
            TYPICAL_MILESTONE_COMPLETED_UG, TYPICAL_MILESTONE_END)).build();

        EditProjectMilestoneCommand.EditMilestoneDescriptor descriptor = new EditMilestoneDescriptorBuilder(
            TYPICAL_MILESTONE_COMPLETED_UG).build();
        EditProjectMilestoneCommand editMilestoneCommand = new EditProjectMilestoneCommand(
            new ProjectName(VALID_PROJECT_NAME_ALICE_HEY) , INDEX_FIRST_PROJECT_MILESTONE, descriptor);

        Model expectedModel = new ModelManager(new PocketProject(model.getPocketProject()), new UserPrefs());
        expectedModel.setProject(model.getFilteredProjectList().get(0), editedProject);
        expectedModel.commitPocketProject();

        editMilestoneCommand.execute(model, commandHistory);

        expectedModel.undoPocketProject();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        expectedModel.redoPocketProject();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);

    }

    @Test
    public void executeUndoRedo_invalidFields_failure() {

        EditProjectMilestoneCommand.EditMilestoneDescriptor descriptor = new EditMilestoneDescriptorBuilder()
            .withMilestoneDesc("Project starts").build();
        EditProjectMilestoneCommand editProjectMilestoneCommand = new EditProjectMilestoneCommand(
            new ProjectName("wheee"), INDEX_FIRST_PROJECT_MILESTONE, descriptor);

        assertCommandFailure(editProjectMilestoneCommand, model, commandHistory,
            Messages.MESSAGE_INVALID_PROJECT_NAME);

        assertCommandFailure(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_FAILURE);
        assertCommandFailure(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_FAILURE);
    }

    @Test
    public void equals() {
        final EditProjectMilestoneCommand standardCommand = new EditProjectMilestoneCommand(
            new ProjectName(VALID_PROJECT_NAME_ALICE_HEY), INDEX_FIRST_PROJECT_MILESTONE, DESC_MILESTONE_1);

        // same values -> returns true
        EditProjectMilestoneCommand.EditMilestoneDescriptor copyDescriptor =
            new EditProjectMilestoneCommand.EditMilestoneDescriptor(DESC_MILESTONE_1);
        EditProjectMilestoneCommand commandWithSameValues = new EditProjectMilestoneCommand(
            new ProjectName(VALID_PROJECT_NAME_ALICE_HEY), INDEX_FIRST_PROJECT_MILESTONE, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditProjectMilestoneCommand(
            new ProjectName(VALID_PROJECT_NAME_ALICE_HEY),
            INDEX_SECOND_PROJECT_MILESTONE, DESC_MILESTONE_1)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditProjectMilestoneCommand(
            new ProjectName(VALID_PROJECT_NAME_ALICE_HEY),
            INDEX_SECOND_PROJECT_MILESTONE, DESC_MILESTONE_2)));
    }
}
