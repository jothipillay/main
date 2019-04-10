package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLIENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DESCRIPTION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MILESTONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SKILL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.PocketProject;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeNameContainsKeywordsPredicate;
import seedu.address.model.project.Project;
import seedu.address.model.project.ProjectNameContainsKeywordsPredicate;
import seedu.address.testutil.EditEmployeeDescriptorBuilder;
import seedu.address.testutil.EditMilestoneDescriptorBuilder;
import seedu.address.testutil.EditProjectDescriptorBuilder;
import seedu.address.testutil.TypicalMilestones;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_NAME_AMY = "Amy Bee";
    public static final String VALID_NAME_BOB = "Bob Choo";
    public static final String VALID_NAME_ALICE = "Alice";
    public static final String VALID_PHONE_AMY = "11111111";
    public static final String VALID_PHONE_BOB = "22222222";
    public static final String VALID_EMAIL_AMY = "amy@example.com";
    public static final String VALID_EMAIL_BOB = "bob@example.com";
    public static final String VALID_GITHUB_AMY = "amyballer";
    public static final String VALID_GITHUB_BOB = "bobballer";
    public static final String VALID_SKILL_JAVA = "Java";
    public static final String VALID_SKILL_C = "Python";

    public static final String VALID_PROJECT_NAME_AMY = "Project Amy";
    public static final String VALID_PROJECT_NAME_BOB = "Project Bob";
    public static final String VALID_PROJECT_NAME_ALICE = "Project Alice";
    public static final String VALID_PROJECT_NAME_ALICE_HEY = "Project Alice hey";
    public static final String VALID_PROJECT_NAME_ZULU = "Zulu";
    public static final String VALID_PROJECT_TASK_ALPHA = "Task Alpha";
    public static final String VALID_PROJECT_TASK_BETA = "Task Beta";
    public static final String VALID_CLIENT_AMY = "SOC";
    public static final String VALID_CLIENT_BOB = "FOS";
    public static final String VALID_CLIENT_ALICE = "Dehui";
    public static final String VALID_CLIENT_ZULU = "Shunnizuka";
    public static final String VALID_DEADLINE_AMY = "11/11/2011";
    public static final String VALID_DEADLINE_BOB = "12/12/2012";
    public static final String VALID_DEADLINE_ALICE = "11/02/2019";
    public static final String VALID_DEADLINE_ZULU = "09/04/2019";
    public static final String VALID_FLEXIDATE_ZULU = "this month 9";
    public static final String VALID_DESCRIPTION = "This project is targeted towards youth.";
    public static final String VALID_START_AMY = "10/11/2011";
    public static final String VALID_START_BOB = "11/12/2012";
    public static final String VALID_START_ALICE = "10/02/2019";
    public static final String VALID_START_ZULU = "09/10/2010";
    public static final String VALID_LATE_DATE = "12/12/2099";
    public static final String VALID_LATE_DAY_ZULU = "10/04/2019";
    public static final String VALID_LATE_MONTH_ZULU = "09/05/2019";
    public static final String VALID_LATE_YEAR_ZULU = "09/04/2020";

    public static final String NAME_DESC_ALICE = " " + PREFIX_NAME + VALID_PROJECT_NAME_ALICE;
    public static final String NAME_DESC_ZULU = " " + PREFIX_NAME + VALID_PROJECT_NAME_ZULU;
    public static final String CLIENT_DESC_ALICE = " " + PREFIX_CLIENT + VALID_CLIENT_ALICE;
    public static final String CLIENT_DESC_ZULU = " " + PREFIX_CLIENT + VALID_CLIENT_ZULU;
    public static final String START_DESC_ALICE = " " + PREFIX_START_DATE + VALID_START_ALICE;
    public static final String START_DESC_ZULU = " " + PREFIX_START_DATE + VALID_START_ZULU;
    public static final String DEADLINE_DESC_ALICE = " " + PREFIX_DEADLINE + VALID_DEADLINE_ALICE;
    public static final String DEADLINE_DESC_ZULU = " " + PREFIX_DEADLINE + VALID_DEADLINE_ZULU;
    public static final String FLEXI_DEADLINE_DESC_ZULU = " " + PREFIX_DEADLINE + VALID_FLEXIDATE_ZULU;
    public static final String DESCRIPTION_DESC = " " + PREFIX_DESCRIPTION + VALID_DESCRIPTION;
    public static final String VALID_LATE_DATE_DESC = " " + PREFIX_START_DATE + VALID_LATE_DATE;
    public static final String VALID_LATE_DAY_ZULU_DESC = " " + PREFIX_START_DATE + VALID_LATE_DAY_ZULU;
    public static final String VALID_LATE_MONTH_ZULU_DESC = " " + PREFIX_START_DATE + VALID_LATE_MONTH_ZULU;
    public static final String VALID_LATE_YEAR_ZULU_DESC = " " + PREFIX_START_DATE + VALID_LATE_YEAR_ZULU;


    public static final String NAME_DESC_AMY = " " + PREFIX_NAME + VALID_NAME_AMY;
    public static final String NAME_DESC_BOB = " " + PREFIX_NAME + VALID_NAME_BOB;
    public static final String PHONE_DESC_AMY = " " + PREFIX_PHONE + VALID_PHONE_AMY;
    public static final String PHONE_DESC_BOB = " " + PREFIX_PHONE + VALID_PHONE_BOB;
    public static final String EMAIL_DESC_AMY = " " + PREFIX_EMAIL + VALID_EMAIL_AMY;
    public static final String EMAIL_DESC_BOB = " " + PREFIX_EMAIL + VALID_EMAIL_BOB;
    public static final String GITHUB_DESC_AMY = " " + PREFIX_GITHUB + VALID_GITHUB_AMY;
    public static final String GITHUB_DESC_BOB = " " + PREFIX_GITHUB + VALID_GITHUB_BOB;
    public static final String SKILL_DESC_C = " " + PREFIX_SKILL + VALID_SKILL_C;
    public static final String SKILL_DESC_JAVA = " " + PREFIX_SKILL + VALID_SKILL_JAVA;

    public static final String INVALID_NAME_DESC = " " + PREFIX_NAME + "James&"; // '&' not allowed in names
    public static final String INVALID_PHONE_DESC = " " + PREFIX_PHONE + "911a"; // 'a' not allowed in phones
    public static final String INVALID_EMAIL_DESC = " " + PREFIX_EMAIL + "bob!yahoo"; // missing '@' symbol
    public static final String INVALID_GITHUB_DESC = " " + PREFIX_GITHUB + "bob baller"; // no whitespace
    public static final String INVALID_GITHUB2_DESC = " " + PREFIX_GITHUB + ""; //no empty string
    public static final String INVALID_GITHUB3_DESC = " " + PREFIX_GITHUB + " kingcoder 96"; //combine 1 & 2
    public static final String INVALID_SKILL_DESC = " " + PREFIX_SKILL + "Java*"; // '*' not allowed in skills
    public static final String INVALID_MILESTONE_DESC = " " + PREFIX_MILESTONE + ""; //empty string not allowed

    public static final String INVALID_PROJECT_NAME_DESC = " " + PREFIX_NAME + "Alice##"; // '#' not allowed in names
    public static final String INVALID_CLIENT_DESC = " " + PREFIX_CLIENT + "Apollo#&"; // '#' not allowed in client
    public static final String INVALID_START_DESC = " " + PREFIX_START_DATE + "12/222/2123"; //Format DD/MM/YYYY
    public static final String INVALID_DEADLINE_DESC = " " + PREFIX_DEADLINE + "222/11/2019"; // Format: DD/MM/YYYY
    public static final String INVALID_FLEXI_DATE_DESC = " " + PREFIX_DEADLINE + "after month 3";
    public static final String INVALID_DESCRIPTION_DESC = " " + PREFIX_DESCRIPTION + "";
    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final EditEmployeeCommand.EditEmployeeDescriptor DESC_AMY;
    public static final EditEmployeeCommand.EditEmployeeDescriptor DESC_BOB;

    public static final EditProjectInfoCommand.EditProjectDescriptor DESC_PROJECT_1;
    public static final EditProjectInfoCommand.EditProjectDescriptor DESC_PROJECT_2;

    public static final EditProjectMilestoneCommand.EditMilestoneDescriptor DESC_MILESTONE_1;
    public static final EditProjectMilestoneCommand.EditMilestoneDescriptor DESC_MILESTONE_2;

    static {
        DESC_AMY = new EditEmployeeDescriptorBuilder().withName(VALID_NAME_AMY)
            .withPhone(VALID_PHONE_AMY).withEmail(VALID_EMAIL_AMY).withGithubAccount(VALID_GITHUB_AMY)
            .withSkills(VALID_SKILL_C).build();
        DESC_BOB = new EditEmployeeDescriptorBuilder().withName(VALID_NAME_BOB)
            .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).withGithubAccount(VALID_GITHUB_BOB)
            .withSkills(VALID_SKILL_JAVA, VALID_SKILL_C).build();
        DESC_PROJECT_1 = new EditProjectDescriptorBuilder().withName(VALID_NAME_BOB).withClient(VALID_CLIENT_BOB)
            .withDescription(VALID_DESCRIPTION).build();
        DESC_PROJECT_2 = new EditProjectDescriptorBuilder().withName(VALID_NAME_AMY).withClient(VALID_CLIENT_ALICE)
            .withDescription(VALID_DESCRIPTION).build();
        DESC_MILESTONE_1 = new EditMilestoneDescriptorBuilder(TypicalMilestones.TYPICAL_MILESTONE_START).build();
        DESC_MILESTONE_2 = new EditMilestoneDescriptorBuilder(TypicalMilestones.TYPICAL_MILESTONE_END).build();
    }


    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel} <br>
     * - the {@code actualCommandHistory} remains unchanged.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandHistory actualCommandHistory,
                                            CommandResult expectedCommandResult, Model expectedModel) {
        CommandHistory expectedCommandHistory = new CommandHistory(actualCommandHistory);
        try {
            CommandResult result = command.execute(actualModel, actualCommandHistory);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
            assertEquals(expectedCommandHistory, actualCommandHistory);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandHistory, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandHistory actualCommandHistory,
                                            String expectedMessage, Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, actualCommandHistory, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the pocket project, filtered employee list and selected employee in {@code actualModel} remain unchanged <br>
     * - {@code actualCommandHistory} remains unchanged.
     */
    public static void assertCommandFailure(Command command, Model actualModel, CommandHistory actualCommandHistory,
                                            String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        PocketProject expectedPocketProject = new PocketProject(actualModel.getPocketProject());
        List<Employee> expectedFilteredList = new ArrayList<>(actualModel.getFilteredEmployeeList());
        Employee expectedSelectedEmployee = actualModel.getSelectedEmployee();

        CommandHistory expectedCommandHistory = new CommandHistory(actualCommandHistory);

        try {
            command.execute(actualModel, actualCommandHistory);
            throw new AssertionError("The expected CommandException was not thrown.");
        } catch (CommandException e) {
            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedPocketProject, actualModel.getPocketProject());
            assertEquals(expectedFilteredList, actualModel.getFilteredEmployeeList());
            assertEquals(expectedSelectedEmployee, actualModel.getSelectedEmployee());
            assertEquals(expectedCommandHistory, actualCommandHistory);
        }
    }

    /**
     * Updates {@code model}'s filtered list to show only the employee at the given {@code targetIndex} in the
     * {@code model}'s employee list.
     */
    public static void showEmployeeAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredEmployeeList().size());

        Employee employee = model.getFilteredEmployeeList().get(targetIndex.getZeroBased());
        final String[] splitName = employee.getEmployeeName().fullName.split("\\s+");
        model.updateFilteredEmployeeList(new EmployeeNameContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertEquals(1, model.getFilteredEmployeeList().size());
    }

    /**
     * Updates {@code model}'s filtered list to show only the project at the given {@code targetIndex} in the
     * {@code model}'s project list.
     */
    public static void showProjectAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredProjectList().size());

        Project project = model.getFilteredProjectList().get(targetIndex.getZeroBased());
        final String[] splitName = project.getProjectName().projectName.split("\\s+");
        model.updateFilteredProjectList(new ProjectNameContainsKeywordsPredicate(Arrays.asList(splitName[1])));

        assertEquals(1, model.getFilteredProjectList().size());
    }

    /**
     * Deletes the first employee in {@code model}'s filtered list from {@code model}'s pocket project.
     */
    public static void deleteFirstEmployee(Model model) {
        Employee firstEmployee = model.getFilteredEmployeeList().get(0);
        model.deleteEmployee(firstEmployee);
        model.commitPocketProject();
    }

}
