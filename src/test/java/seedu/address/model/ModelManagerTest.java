package seedu.address.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_EMPLOYEES;
import static seedu.address.testutil.TypicalEmployees.ALICE;
import static seedu.address.testutil.TypicalEmployees.BENSON;
import static seedu.address.testutil.TypicalEmployees.BOB;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeNameContainsKeywordsPredicate;
import seedu.address.model.employee.exceptions.EmployeeNotFoundException;
import seedu.address.model.project.Project;
import seedu.address.testutil.EmployeeBuilder;
import seedu.address.testutil.PocketProjectBuilder;
import seedu.address.testutil.TypicalEmployees;
import seedu.address.testutil.TypicalProjects;

public class ModelManagerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new PocketProject(), new PocketProject(modelManager.getPocketProject()));
        assertEquals(null, modelManager.getSelectedEmployee());
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.setUserPrefs(null);
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setPocketProjectFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setPocketProjectFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.setGuiSettings(null);
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setPocketProjectFilePath_nullPath_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.setPocketProjectFilePath(null);
    }

    @Test
    public void setPocketProjectFilePath_validPath_setsPocketProjectFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setPocketProjectFilePath(path);
        assertEquals(path, modelManager.getPocketProjectFilePath());
    }

    @Test
    public void hasEmployee_nullEmployee_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        modelManager.hasEmployee(null);
    }

    @Test
    public void hasEmployee_employeeNotInPocketProject_returnsFalse() {
        assertFalse(modelManager.hasEmployee(ALICE));
    }

    @Test
    public void hasEmployee_employeeInPocketProject_returnsTrue() {
        modelManager.addEmployee(ALICE);
        assertTrue(modelManager.hasEmployee(ALICE));
    }

    @Test
    public void deleteEmployee_employeeIsSelectedAndFirstEmployeeInFilteredEmployeeList_selectionCleared() {
        modelManager.addEmployee(ALICE);
        modelManager.setSelectedEmployee(ALICE);
        modelManager.deleteEmployee(ALICE);
        assertEquals(null, modelManager.getSelectedEmployee());
    }

    @Test
    public void deleteEmployee_employeeIsSelectedAndSecondEmployeeInFilteredEmployeeList_firstEmployeeSelected() {
        modelManager.addEmployee(ALICE);
        modelManager.addEmployee(BOB);
        assertEquals(Arrays.asList(ALICE, BOB), modelManager.getFilteredEmployeeList());
        modelManager.setSelectedEmployee(BOB);
        modelManager.deleteEmployee(BOB);
        assertEquals(ALICE, modelManager.getSelectedEmployee());
    }

    @Test
    public void getProjectsContaining_noProjectContains_returnsEmptyList() {
        List<Project> list = modelManager.getProjectsContaining(TypicalEmployees.IDA);
        assertEquals(list, Arrays.asList());
    }
    @Test
    public void getProjectsContaining_projectContains_returnProjects() {
        Project projectToAdd1 = TypicalProjects.PROJECT_IDA;
        projectToAdd1.addEmployee(TypicalEmployees.IDA);
        Project projectToAdd2 = TypicalProjects.PROJECT_HOON;
        projectToAdd2.addEmployee(TypicalEmployees.IDA);
        modelManager.addProject(projectToAdd1);
        modelManager.addProject(projectToAdd2);
        List<Project> list = modelManager.getProjectsContaining(TypicalEmployees.IDA);
        assertTrue(list.containsAll(Arrays.asList(projectToAdd1, projectToAdd2)));
    }

    @Test
    public void setEmployee_employeeIsSelected_selectedEmployeeUpdated() {
        modelManager.addEmployee(ALICE);
        modelManager.setSelectedEmployee(ALICE);
        Employee updatedAlice = new EmployeeBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        modelManager.setEmployee(ALICE, updatedAlice);
        assertEquals(updatedAlice, modelManager.getSelectedEmployee());
    }

    @Test
    public void getFilteredEmployeeList_modifyList_throwsUnsupportedOperationException() {
        thrown.expect(UnsupportedOperationException.class);
        modelManager.getFilteredEmployeeList().remove(0);
    }

    @Test
    public void setSelectedEmployee_employeeNotInFilteredEmployeeList_throwsEmployeeNotFoundException() {
        thrown.expect(EmployeeNotFoundException.class);
        modelManager.setSelectedEmployee(ALICE);
    }

    @Test
    public void setSelectedEmployee_employeeInFilteredEmployeeList_setsSelectedEmployee() {
        modelManager.addEmployee(ALICE);
        assertEquals(Collections.singletonList(ALICE), modelManager.getFilteredEmployeeList());
        modelManager.setSelectedEmployee(ALICE);
        assertEquals(ALICE, modelManager.getSelectedEmployee());
    }

    @Test
    public void equals() {
        PocketProject pocketProject = new PocketProjectBuilder().withEmployee(ALICE).withEmployee(BENSON).build();
        PocketProject differentPocketProject = new PocketProject();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(pocketProject, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(pocketProject, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different pocketProject -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentPocketProject, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getEmployeeName().fullName.split("\\s+");
        modelManager.updateFilteredEmployeeList(new EmployeeNameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(pocketProject, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredEmployeeList(PREDICATE_SHOW_ALL_EMPLOYEES);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setPocketProjectFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(pocketProject, differentUserPrefs)));
    }
}
