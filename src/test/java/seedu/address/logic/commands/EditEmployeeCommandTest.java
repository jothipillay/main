package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SKILL_JAVA;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showEmployeeAtIndex;
import static seedu.address.testutil.TypicalEmployees.getTypicalPocketProjectWithEmployees;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_EMPLOYEE;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_EMPLOYEE;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.EditEmployeeCommand.EditEmployeeDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.PocketProject;
import seedu.address.model.UserPrefs;
import seedu.address.model.employee.Employee;
import seedu.address.testutil.EditEmployeeDescriptorBuilder;
import seedu.address.testutil.EmployeeBuilder;
import seedu.address.testutil.PocketProjectBuilder;
import seedu.address.testutil.TypicalEmployees;
import seedu.address.testutil.TypicalProjects;

/**
 * Contains integration tests (interaction with the Model, UndoCommand and RedoCommand) and unit tests
 * for EditEmployeeCommand.
 */
public class EditEmployeeCommandTest {

    private Model model = new ModelManager(getTypicalPocketProjectWithEmployees(), new UserPrefs());
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Employee editedEmployee = new EmployeeBuilder().build();
        EditEmployeeCommand.EditEmployeeDescriptor descriptor = new EditEmployeeDescriptorBuilder(editedEmployee)
            .build();
        EditEmployeeCommand editEmployeeCommand = new EditEmployeeCommand(INDEX_FIRST_EMPLOYEE, descriptor);

        String expectedMessage = String.format(EditEmployeeCommand.MESSAGE_EDIT_EMPLOYEE_SUCCESS, editedEmployee);

        Model expectedModel = new ModelManager(new PocketProject(model.getPocketProject()), new UserPrefs());
        expectedModel.setEmployee(model.getFilteredEmployeeList().get(0), editedEmployee);
        expectedModel.commitPocketProject();

        assertCommandSuccess(editEmployeeCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastEmployee = Index.fromOneBased(model.getFilteredEmployeeList().size());
        Employee lastEmployee = model.getFilteredEmployeeList().get(indexLastEmployee.getZeroBased());

        EmployeeBuilder employeeInList = new EmployeeBuilder(lastEmployee);
        Employee editedEmployee = employeeInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
            .withSkills(VALID_SKILL_JAVA).build();

        EditEmployeeDescriptor descriptor = new EditEmployeeDescriptorBuilder().withName(VALID_NAME_BOB)
            .withPhone(VALID_PHONE_BOB).withSkills(VALID_SKILL_JAVA).build();
        EditEmployeeCommand editEmployeeCommand = new EditEmployeeCommand(indexLastEmployee, descriptor);

        String expectedMessage = String.format(EditEmployeeCommand.MESSAGE_EDIT_EMPLOYEE_SUCCESS, editedEmployee);

        Model expectedModel = new ModelManager(new PocketProject(model.getPocketProject()), new UserPrefs());
        expectedModel.setEmployee(lastEmployee, editedEmployee);
        expectedModel.commitPocketProject();

        assertCommandSuccess(editEmployeeCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditEmployeeCommand editEmployeeCommand = new EditEmployeeCommand(INDEX_FIRST_EMPLOYEE,
            new EditEmployeeDescriptor());
        Employee editedEmployee = model.getFilteredEmployeeList().get(INDEX_FIRST_EMPLOYEE.getZeroBased());

        String expectedMessage = String.format(EditEmployeeCommand.MESSAGE_EDIT_EMPLOYEE_SUCCESS, editedEmployee);

        Model expectedModel = new ModelManager(new PocketProject(model.getPocketProject()), new UserPrefs());
        expectedModel.commitPocketProject();

        assertCommandSuccess(editEmployeeCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showEmployeeAtIndex(model, INDEX_FIRST_EMPLOYEE);

        Employee employeeInFilteredList = model.getFilteredEmployeeList().get(INDEX_FIRST_EMPLOYEE.getZeroBased());
        Employee editedEmployee = new EmployeeBuilder(employeeInFilteredList).withName(VALID_NAME_BOB).build();
        EditEmployeeCommand editEmployeeCommand = new EditEmployeeCommand(INDEX_FIRST_EMPLOYEE,
            new EditEmployeeDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditEmployeeCommand.MESSAGE_EDIT_EMPLOYEE_SUCCESS, editedEmployee);

        Model expectedModel = new ModelManager(new PocketProject(model.getPocketProject()), new UserPrefs());
        expectedModel.setEmployee(model.getFilteredEmployeeList().get(0), editedEmployee);
        expectedModel.commitPocketProject();

        assertCommandSuccess(editEmployeeCommand, model, commandHistory, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateEmployeeUnfilteredList_failure() {
        Employee firstEmployee = model.getFilteredEmployeeList().get(INDEX_FIRST_EMPLOYEE.getZeroBased());
        EditEmployeeDescriptor descriptor = new EditEmployeeDescriptorBuilder(firstEmployee).build();
        EditEmployeeCommand editEmployeeCommand = new EditEmployeeCommand(INDEX_SECOND_EMPLOYEE, descriptor);

        assertCommandFailure(editEmployeeCommand, model, commandHistory,
            EditEmployeeCommand.MESSAGE_DUPLICATE_EMPLOYEE);
    }

    @Test
    public void execute_duplicateEmployeeFilteredList_failure() {
        showEmployeeAtIndex(model, INDEX_FIRST_EMPLOYEE);

        // edit employee in filtered list into a duplicate in pocket project
        Employee employeeInList = model.getPocketProject().getEmployeeList().get(INDEX_SECOND_EMPLOYEE.getZeroBased());
        EditEmployeeCommand editEmployeeCommand = new EditEmployeeCommand(INDEX_FIRST_EMPLOYEE,
            new EditEmployeeDescriptorBuilder(employeeInList).build());

        assertCommandFailure(editEmployeeCommand, model, commandHistory,
            EditEmployeeCommand.MESSAGE_DUPLICATE_EMPLOYEE);
    }

    @Test
    public void execute_invalidEmployeeIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredEmployeeList().size() + 1);
        EditEmployeeCommand.EditEmployeeDescriptor descriptor =
            new EditEmployeeDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditEmployeeCommand editEmployeeCommand = new EditEmployeeCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editEmployeeCommand, model, commandHistory,
            Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of pocket project
     */
    @Test
    public void execute_invalidEmployeeIndexFilteredList_failure() {
        showEmployeeAtIndex(model, INDEX_FIRST_EMPLOYEE);
        Index outOfBoundIndex = INDEX_SECOND_EMPLOYEE;
        // ensures that outOfBoundIndex is still in bounds of pocket project list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getPocketProject().getEmployeeList().size());

        EditEmployeeCommand editEmployeeCommand = new EditEmployeeCommand(outOfBoundIndex,
            new EditEmployeeDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editEmployeeCommand, model, commandHistory,
            Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
    }

    @Test
    public void executeUndoRedo_validIndexUnfilteredList_success() throws Exception {
        Employee editedEmployee = new EmployeeBuilder().build();
        Employee employeeToEdit = model.getFilteredEmployeeList().get(INDEX_FIRST_EMPLOYEE.getZeroBased());
        EditEmployeeDescriptor descriptor = new EditEmployeeDescriptorBuilder(editedEmployee).build();
        EditEmployeeCommand editEmployeeCommand = new EditEmployeeCommand(INDEX_FIRST_EMPLOYEE, descriptor);
        Model expectedModel = new ModelManager(new PocketProject(model.getPocketProject()), new UserPrefs());
        expectedModel.setEmployee(employeeToEdit, editedEmployee);
        expectedModel.commitPocketProject();

        // edit -> first employee edited
        editEmployeeCommand.execute(model, commandHistory);

        // undo -> reverts pocket project back to previous state and filtered employee list to show all employees
        expectedModel.undoPocketProject();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        // redo -> same first employee edited again
        expectedModel.redoPocketProject();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void executeUndoRedo_invalidIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredEmployeeList().size() + 1);
        EditEmployeeDescriptor descriptor = new EditEmployeeDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditEmployeeCommand editEmployeeCommand = new EditEmployeeCommand(outOfBoundIndex, descriptor);

        // execution failed -> pocket project state not added into model
        assertCommandFailure(editEmployeeCommand, model, commandHistory,
            Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);

        // single pocket project state in model -> undoCommand and redoCommand fail
        assertCommandFailure(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_FAILURE);
        assertCommandFailure(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_FAILURE);
    }

    /**
     * 1. Edits a {@code Employee} from a filtered list.
     * 2. Undo the edit.
     * 3. The unfiltered list should be shown now. Verify that the index of the previously edited employee in the
     * unfiltered list is different from the index at the filtered list.
     * 4. Redo the edit. This ensures {@code RedoCommand} edits the employee object regardless of indexing.
     */
    @Test
    public void executeUndoRedo_validIndexFilteredList_sameEmployeeEdited() throws Exception {
        Employee editedEmployee = new EmployeeBuilder().build();
        EditEmployeeCommand.EditEmployeeDescriptor descriptor = new EditEmployeeDescriptorBuilder(editedEmployee)
            .build();
        EditEmployeeCommand editEmployeeCommand = new EditEmployeeCommand(INDEX_FIRST_EMPLOYEE, descriptor);
        Model expectedModel = new ModelManager(new PocketProject(model.getPocketProject()), new UserPrefs());

        showEmployeeAtIndex(model, INDEX_SECOND_EMPLOYEE);
        Employee employeeToEdit = model.getFilteredEmployeeList().get(INDEX_FIRST_EMPLOYEE.getZeroBased());
        expectedModel.setEmployee(employeeToEdit, editedEmployee);
        expectedModel.commitPocketProject();

        // edit -> edits second employee in unfiltered employee list / first employee in filtered employee list
        editEmployeeCommand.execute(model, commandHistory);

        // undo -> reverts pocket project back to previous state and filtered employee list to show all employees
        expectedModel.undoPocketProject();
        assertCommandSuccess(new UndoCommand(), model, commandHistory, UndoCommand.MESSAGE_SUCCESS, expectedModel);

        assertNotEquals(model.getFilteredEmployeeList().get(INDEX_FIRST_EMPLOYEE.getZeroBased()), employeeToEdit);
        // redo -> edits same second employee in unfiltered employee list
        expectedModel.redoPocketProject();
        assertCommandSuccess(new RedoCommand(), model, commandHistory, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    /**
     * Test if the employees in the projects are updated when editCommand is executed
     */
    @Test
    public void executeUndoRedo_sameEmployeeInProjectEdited() throws CommandException {

        PocketProjectBuilder builder = new PocketProjectBuilder().withProject(TypicalProjects.PROJECT_ALICE)
            .withEmployee(TypicalEmployees.BENSON).withEmployee(TypicalEmployees.CARL);
        model = new ModelManager(builder.build(), new UserPrefs());

        Employee editedEmployee = new EmployeeBuilder().build();
        EditEmployeeCommand.EditEmployeeDescriptor descriptor = new EditEmployeeDescriptorBuilder(editedEmployee)
            .build();
        EditEmployeeCommand editEmployeeCommand = new EditEmployeeCommand(INDEX_FIRST_EMPLOYEE, descriptor);

        editEmployeeCommand.execute(model, commandHistory);

        assertTrue(model.getPocketProject().getProjectList().get(0).getEmployees().get(0)
            .equals(editedEmployee));

        model.undoPocketProject();

        assertTrue(model.getPocketProject().getProjectList().get(0).getEmployees().get(0)
            .equals(TypicalEmployees.BENSON));

        model.redoPocketProject();

        assertTrue(model.getPocketProject().getProjectList().get(0).getEmployees().get(0)
            .equals(editedEmployee));
    }

    @Test
    public void equals() {
        final EditEmployeeCommand standardCommand = new EditEmployeeCommand(INDEX_FIRST_EMPLOYEE, DESC_AMY);

        // same values -> returns true
        EditEmployeeDescriptor copyDescriptor = new EditEmployeeDescriptor(DESC_AMY);
        EditEmployeeCommand commandWithSameValues = new EditEmployeeCommand(INDEX_FIRST_EMPLOYEE, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditEmployeeCommand(INDEX_SECOND_EMPLOYEE, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditEmployeeCommand(INDEX_FIRST_EMPLOYEE, DESC_BOB)));
    }

}
