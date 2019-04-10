package systemtests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.GITHUB_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.GITHUB_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_GITHUB_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_SKILL_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.SKILL_DESC_C;
import static seedu.address.logic.commands.CommandTestUtil.SKILL_DESC_JAVA;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_SKILL_JAVA;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SKILL;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_EMPLOYEES;
import static seedu.address.testutil.TypicalEmployees.AMY;
import static seedu.address.testutil.TypicalEmployees.BOB;
import static seedu.address.testutil.TypicalEmployees.KEYWORD_MATCHING_MEIER;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_EMPLOYEE;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_EMPLOYEE;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditEmployeeCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.employee.Email;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeName;
import seedu.address.model.employee.GitHubAccount;
import seedu.address.model.employee.Phone;
import seedu.address.model.project.Project;
import seedu.address.model.skill.Skill;
import seedu.address.testutil.EmployeeBuilder;
import seedu.address.testutil.EmployeeUtil;

public class EditEmployeeCommandSystemTest extends PocketProjectSystemTest {

    @Test
    public void edit() {
        Model model = getModel();

        /* ----------------- Performing edit operation while an unfiltered list is being shown ---------------------- */

        /* Case: edit all fields, command with leading spaces, trailing spaces and multiple spaces between each field
         * -> edited
         */
        Index index = INDEX_FIRST_EMPLOYEE;
        String command = " " + EditEmployeeCommand.COMMAND_WORD + "  " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
            + " " + index.getOneBased() + "  " + NAME_DESC_BOB + "  " + PHONE_DESC_BOB + " " + EMAIL_DESC_BOB + "  "
            + GITHUB_DESC_BOB + " " + SKILL_DESC_JAVA + " ";
        Employee editedEmployee = new EmployeeBuilder(BOB).withSkills(VALID_SKILL_JAVA).build();
        assertCommandSuccess(command, index, editedEmployee);

        /* Case: undo editing the last employee in the list -> last employee restored */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: redo editing the last employee in the list -> last employee edited again */
        command = RedoCommand.COMMAND_WORD;
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        model.setEmployee(getModel().getFilteredEmployeeList().get(INDEX_FIRST_EMPLOYEE.getZeroBased()),
            editedEmployee);
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: edit a employee with new values same as existing values -> edited */
        command = EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD + " "
            + index.getOneBased() + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
            + GITHUB_DESC_BOB + SKILL_DESC_C + SKILL_DESC_JAVA;
        assertCommandSuccess(command, index, BOB);

        /* Case: edit a employee with new values same as another employee's values but with different name -> edited */
        assertTrue(getModel().getPocketProject().getEmployeeList().contains(BOB));
        index = INDEX_SECOND_EMPLOYEE;
        assertNotEquals(getModel().getFilteredEmployeeList().get(index.getZeroBased()), BOB);
        command = EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
            + " " + index.getOneBased() + NAME_DESC_AMY + PHONE_DESC_BOB + EMAIL_DESC_BOB
            + GITHUB_DESC_BOB + SKILL_DESC_C + SKILL_DESC_JAVA;
        editedEmployee = new EmployeeBuilder(BOB).withName(VALID_NAME_AMY).build();
        assertCommandSuccess(command, index, editedEmployee);

        /* Case: edit a employee with new values same as another employee's values but with different phone and email
         * -> edited
         */
        index = INDEX_SECOND_EMPLOYEE;
        command = EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
            + " " + index.getOneBased() + NAME_DESC_BOB + PHONE_DESC_AMY + EMAIL_DESC_AMY
            + GITHUB_DESC_BOB + SKILL_DESC_C + SKILL_DESC_JAVA;
        editedEmployee = new EmployeeBuilder(BOB).withPhone(VALID_PHONE_AMY).withEmail(VALID_EMAIL_AMY).build();
        assertCommandSuccess(command, index, editedEmployee);

        /* Case: clear skills -> cleared */
        index = INDEX_FIRST_EMPLOYEE;
        command = EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
            + " " + index.getOneBased() + " " + PREFIX_SKILL.getPrefix();
        Employee employeeToEdit = getModel().getFilteredEmployeeList().get(index.getZeroBased());
        editedEmployee = new EmployeeBuilder(employeeToEdit).withSkills().build();
        assertCommandSuccess(command, index, editedEmployee);

        /* ------------------ Performing edit operation while a filtered list is being shown ------------------------ */

        /* Case: filtered employee list, edit index within bounds of pocket project and employee list -> edited */
        showEmployeesWithName(KEYWORD_MATCHING_MEIER);
        index = INDEX_FIRST_EMPLOYEE;
        assertTrue(index.getZeroBased() < getModel().getFilteredEmployeeList().size());
        command = EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
            + " " + index.getOneBased() + " " + NAME_DESC_BOB;
        employeeToEdit = getModel().getFilteredEmployeeList().get(index.getZeroBased());
        editedEmployee = new EmployeeBuilder(employeeToEdit).withName(VALID_NAME_BOB).build();
        assertCommandSuccess(command, index, editedEmployee);

        /* Case: filtered employee list, edit index within bounds of pocket project but out of bounds of employee list
         * -> rejected
         */
        showEmployeesWithName(KEYWORD_MATCHING_MEIER);
        int invalidIndex = getModel().getPocketProject().getEmployeeList().size();
        assertCommandFailure(EditEmployeeCommand.COMMAND_WORD + " "
                + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD + " " + invalidIndex + NAME_DESC_BOB,
            Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);

        /* --------------------- Performing edit operation while a employee card is selected ----------------------- */

        /* Case: selects first card in the employee list, edit a employee -> edited, card selection remains unchanged
         * but browser url changes
         */
        showAllEmployees();
        index = INDEX_FIRST_EMPLOYEE;
        viewEmployee(index);
        command = EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
            + " " + index.getOneBased() + NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY
            + GITHUB_DESC_AMY + SKILL_DESC_C;
        // this can be misleading: card selection actually remains unchanged but the
        // browser's url is updated to reflect the new employee's name
        assertCommandSuccess(command, index, AMY, index);

        /* --------------------------------- Performing invalid edit operation -------------------------------------- */

        /* Case: invalid index (0) -> rejected */
        assertCommandFailure(EditEmployeeCommand.COMMAND_WORD + " "
                + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD + " 0" + NAME_DESC_BOB,
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditEmployeeCommand.MESSAGE_USAGE));

        /* Case: invalid index (-1) -> rejected */
        assertCommandFailure(EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
                + " -1" + NAME_DESC_BOB,
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditEmployeeCommand.MESSAGE_USAGE));

        /* Case: invalid index (size + 1) -> rejected */
        invalidIndex = getModel().getFilteredEmployeeList().size() + 1;
        assertCommandFailure(EditEmployeeCommand.COMMAND_WORD + " "
                + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD + " " + invalidIndex + NAME_DESC_BOB,
            Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);

        /* Case: missing index -> rejected */
        assertCommandFailure(EditEmployeeCommand.COMMAND_WORD + " "
                + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD + " " + NAME_DESC_BOB,
            String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, EditEmployeeCommand.MESSAGE_USAGE));

        /* Case: missing all fields -> rejected */
        assertCommandFailure(EditEmployeeCommand.COMMAND_WORD + " "
                + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD + " " + INDEX_FIRST_EMPLOYEE.getOneBased(),
            EditEmployeeCommand.MESSAGE_NOT_EDITED);

        /* Case: invalid name -> rejected */
        assertCommandFailure(EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
            + " " + INDEX_FIRST_EMPLOYEE.getOneBased() + INVALID_NAME_DESC, EmployeeName.MESSAGE_CONSTRAINTS);

        /* Case: invalid phone -> rejected */
        assertCommandFailure(EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
            + " " + INDEX_FIRST_EMPLOYEE.getOneBased() + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS);

        /* Case: invalid email -> rejected */
        assertCommandFailure(EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
            + " " + INDEX_FIRST_EMPLOYEE.getOneBased() + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS);

        /* Case: invalid github -> rejected */
        assertCommandFailure(EditEmployeeCommand.COMMAND_WORD + " "
                + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD + " " + INDEX_FIRST_EMPLOYEE.getOneBased()
                + INVALID_GITHUB_DESC,
            GitHubAccount.MESSAGE_CONSTRAINTS);

        /* Case: invalid skill -> rejected */
        assertCommandFailure(EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
                + " " + INDEX_FIRST_EMPLOYEE.getOneBased() + INVALID_SKILL_DESC,
            Skill.MESSAGE_CONSTRAINTS);

        /* Case: edit a employee with new values same as another employee's values -> rejected */
        executeCommand(EmployeeUtil.getAddEmployeeCommand(BOB));
        assertTrue(getModel().getPocketProject().getEmployeeList().contains(BOB));
        index = INDEX_FIRST_EMPLOYEE;
        assertFalse(getModel().getFilteredEmployeeList().get(index.getZeroBased()).equals(BOB));
        command = EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD + " "
            + index.getOneBased() + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + GITHUB_DESC_BOB + SKILL_DESC_C
            + SKILL_DESC_JAVA;
        assertCommandFailure(command, EditEmployeeCommand.MESSAGE_DUPLICATE_EMPLOYEE);

        /* Case: edit a employee with new values same as another employee's values but with different skills ->
        rejected*/
        command = EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
            + " " + index.getOneBased() + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
            + GITHUB_DESC_BOB + SKILL_DESC_JAVA;
        assertCommandFailure(command, EditEmployeeCommand.MESSAGE_DUPLICATE_EMPLOYEE);

        /* Case: edit an employee with new values same as another employee's values but with different github ->
         * rejected
         */
        command = EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
            + " " + index.getOneBased() + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
            + GITHUB_DESC_AMY + SKILL_DESC_C + SKILL_DESC_JAVA;
        assertCommandFailure(command, EditEmployeeCommand.MESSAGE_DUPLICATE_EMPLOYEE);

        /* Case: edit a employee with new values same as another employee's values but with different phone -> rejected
         */
        command = EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
            + " " + index.getOneBased() + NAME_DESC_BOB + PHONE_DESC_AMY + EMAIL_DESC_BOB
            + GITHUB_DESC_BOB + SKILL_DESC_C + SKILL_DESC_JAVA;
        assertCommandFailure(command, EditEmployeeCommand.MESSAGE_DUPLICATE_EMPLOYEE);

        /* Case: edit a employee with new values same as another employee's values but with different email -> rejected
         */
        command = EditEmployeeCommand.COMMAND_WORD + " " + EditEmployeeCommand.EDIT_EMPLOYEE_KEYWORD
            + " " + index.getOneBased() + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_AMY
            + GITHUB_DESC_BOB + SKILL_DESC_C + SKILL_DESC_JAVA;
        assertCommandFailure(command, EditEmployeeCommand.MESSAGE_DUPLICATE_EMPLOYEE);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Index, Employee, Index)} except that
     * the browser url and selected card remain unchanged.
     * @param toEdit the index of the current model's filtered list
     * @see EditEmployeeCommandSystemTest#assertCommandSuccess(String, Index, Employee, Index)
     */
    private void assertCommandSuccess(String command, Index toEdit, Employee editedEmployee) {
        assertCommandSuccess(command, toEdit, editedEmployee, null);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String, Index)} and in addition,<br>
     * 1. Asserts that result display box displays the success message of executing {@code EditEmployeeCommand}.<br>
     * 2. Asserts that the model related components are updated to reflect the employee at index {@code toEdit} being
     * updated to values specified {@code editedEmployee}.<br>
     * @param toEdit the index of the current model's filtered list.
     * @see EditEmployeeCommandSystemTest#assertCommandSuccess(String, Model, String, Index)
     */
    private void assertCommandSuccess(String command, Index toEdit, Employee editedEmployee,
                                      Index expectedSelectedCardIndex) {
        Model expectedModel = getModel();
        Employee toEditEmployee = expectedModel.getFilteredEmployeeList().get(toEdit.getZeroBased());
        expectedModel.setEmployee(expectedModel.getFilteredEmployeeList().get(toEdit.getZeroBased()), editedEmployee);
        expectedModel.updateFilteredEmployeeList(PREDICATE_SHOW_ALL_EMPLOYEES);

        //if no change made to the employee then don't need to check
        if (!toEditEmployee.equals(editedEmployee)) {
            assertTrue(assertProjectEmployeeEditSuccess(expectedModel, toEditEmployee));
        }

        assertCommandSuccess(command, expectedModel,
            String.format(EditEmployeeCommand.MESSAGE_EDIT_EMPLOYEE_SUCCESS, editedEmployee),
            expectedSelectedCardIndex);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String, Index)} except that the
     * browser url and selected card remain unchanged.
     * @see EditEmployeeCommandSystemTest#assertCommandSuccess(String, Model, String, Index)
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
        expectedModel.updateFilteredEmployeeList(PREDICATE_SHOW_ALL_EMPLOYEES);
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
     * To check if the employee in the project's employee list is also edited
     * by checking if the old instance of the employee still exists
     */
    private boolean assertProjectEmployeeEditSuccess(Model expected, Employee toEdit) {

        for (Project project: expected.getProjectList()) {
            if (project.containsEmployee(toEdit)) {
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
