package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.model.project.Project;
import seedu.address.model.project.ProjectName;
import seedu.address.testutil.TestUtil;
import seedu.address.testutil.TypicalProjects;


/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code AddEmployeeToCommand}.
 */
public class AddEmployeeToCommandTest {

    private Model model = new ModelManager(TestUtil.typicalPocketProject(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_validProjectNameValidIndex_success() {
        Project targetProject = model.getProjectWithName(TypicalProjects.PROJECT_ALICE.getProjectName());
        Index validIndex = Index.fromOneBased(model.getFilteredEmployeeList().size() - 1);
        AddEmployeeToCommand addEmployeeToCommand = new AddEmployeeToCommand(validIndex,
            targetProject.getProjectName());
        Employee targetEmployee = model.getFilteredEmployeeList().get(validIndex.getZeroBased());
        String expectedMessage = String.format(AddEmployeeToCommand.MESSAGE_ADDTOPROJECT_EMPLOYEE_SUCCESS,
            targetEmployee, targetProject.getProjectName());

        ModelManager expectedModel = new ModelManager(model.getPocketProject(), new UserPrefs());
        expectedModel.addEmployeeTo(targetProject, targetEmployee);
        expectedModel.commitPocketProject();
        assertCommandSuccess(addEmployeeToCommand, model, commandHistory, expectedMessage, expectedModel);
    }


    @Test
    public void execute_invalidProjName_throwsCommandException() {
        AddEmployeeToCommand addEmployeeToCommand = new AddEmployeeToCommand(Index.fromOneBased(1),
            new ProjectName("INVALID"));
        assertCommandFailure(addEmployeeToCommand, model, commandHistory,
            Messages.MESSAGE_INVALID_PROJECT_NAME);
    }

    @Test
    public void execute_invalidIndexValidProjectName_throwsCommandException() {
        Project targetProject = model.getProjectWithName(TypicalProjects.PROJECT_ALICE.getProjectName());
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredEmployeeList().size() + 1);
        AddEmployeeToCommand addEmployeeToCommand = new AddEmployeeToCommand(outOfBoundIndex,
            targetProject.getProjectName());

        assertCommandFailure(addEmployeeToCommand, model, commandHistory,
            Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
    }

    @Test
    public void execute_duplicateEmployee_throwsCommandException() {

        Project project = model.getProjectWithName(TypicalProjects.PROJECT_ALICE.getProjectName());
        Index targetIndex = Index.fromOneBased(model.getFilteredEmployeeList().size() - 1);
        AddEmployeeToCommand addEmployeeToCommand = new AddEmployeeToCommand(targetIndex,
            project.getProjectName());
        Employee employeeToAdd = model.getFilteredEmployeeList().get(targetIndex.getZeroBased());
        String expectedMessage = String.format(AddEmployeeToCommand.MESSAGE_ADDTOPROJECT_EMPLOYEE_SUCCESS,
            employeeToAdd, project.getProjectName());

        ModelManager expectedModel = new ModelManager(model.getPocketProject(), new UserPrefs());
        expectedModel.addEmployeeTo(project, employeeToAdd);
        expectedModel.commitPocketProject();
        assertCommandSuccess(addEmployeeToCommand, model, commandHistory, expectedMessage, expectedModel);

        AddEmployeeToCommand addEmployeeToCommand2 = new AddEmployeeToCommand(targetIndex,
            project.getProjectName());
        assertCommandFailure(addEmployeeToCommand2, model, commandHistory,
            Messages.MESSAGE_DUPLICATE_PROJ_EMPLOYEE);
    }

    @Test
    public void equals() {
        AddEmployeeToCommand addEmployeeToCommandOne = new AddEmployeeToCommand(Index.fromOneBased(1),
            TypicalProjects.PROJECT_ALICE.getProjectName());
        AddEmployeeToCommand addEmployeeToCommandTwo = new AddEmployeeToCommand(Index.fromOneBased(1),
            TypicalProjects.PROJECT_BENSON.getProjectName());

        // same object -> returns true
        assertTrue(addEmployeeToCommandOne.equals(addEmployeeToCommandOne));

        // same values -> returns true
        AddEmployeeToCommand addEmployeeToCommandOneCopy = new AddEmployeeToCommand(Index.fromOneBased(1),
            TypicalProjects.PROJECT_ALICE.getProjectName());
        assertTrue(addEmployeeToCommandOne.equals(addEmployeeToCommandOneCopy));

        // different types -> returns false
        assertFalse(addEmployeeToCommandOneCopy.equals(1));

        // null -> returns false
        assertFalse(addEmployeeToCommandOneCopy.equals(null));

        // different projects -> returns false
        assertFalse(addEmployeeToCommandOneCopy.equals(addEmployeeToCommandTwo));

        // different indices -> returns false
        AddEmployeeToCommand addEmployeeToCommandThree = new AddEmployeeToCommand(Index.fromOneBased(2),
            TypicalProjects.PROJECT_ALICE.getProjectName());
        assertFalse(addEmployeeToCommandOne.equals(addEmployeeToCommandThree));
    }
}
