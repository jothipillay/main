package systemtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.ui.StatusBarFooter.SYNC_STATUS_INITIAL;
import static seedu.address.ui.StatusBarFooter.SYNC_STATUS_UPDATED;
import static seedu.address.ui.testutil.GuiTestAssert.assertListMatching;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;

import guitests.guihandles.CommandBoxHandle;
import guitests.guihandles.EmployeeListPanelHandle;
import guitests.guihandles.MainMenuHandle;
import guitests.guihandles.MainWindowHandle;
import guitests.guihandles.ProjectListPanelHandle;
import guitests.guihandles.ResultDisplayHandle;
import guitests.guihandles.StatusBarFooterHandle;

import seedu.address.TestApp;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.FindEmployeeCommand;
import seedu.address.logic.commands.FindProjectCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListEmployeeCommand;
import seedu.address.logic.commands.ListProjectCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.commands.ViewEmployeeCommand;
import seedu.address.model.Model;
import seedu.address.model.PocketProject;
import seedu.address.testutil.TestUtil;
import seedu.address.ui.CommandBox;

/**
 * A system test class for PocketProject, which provides access to handles of GUI components and helper methods
 * for test verification.
 */
public abstract class PocketProjectSystemTest {
    @ClassRule
    public static ClockRule clockRule = new ClockRule();

    private static final List<String> COMMAND_BOX_DEFAULT_STYLE = Arrays.asList("text-input", "text-field");
    private static final List<String> COMMAND_BOX_ERROR_STYLE =
        Arrays.asList("text-input", "text-field", CommandBox.ERROR_STYLE_CLASS);

    private MainWindowHandle mainWindowHandle;
    private TestApp testApp;
    private SystemTestSetupHelper setupHelper;

    @BeforeClass
    public static void setupBeforeClass() {
        SystemTestSetupHelper.initialize();
    }

    @Before
    public void setUp() {
        setupHelper = new SystemTestSetupHelper();
        testApp = setupHelper.setupApplication(this::getInitialData, getDataFileLocation());
        mainWindowHandle = setupHelper.setupMainWindowHandle();

        assertApplicationStartingStateIsCorrect();
    }

    @After
    public void tearDown() {
        setupHelper.tearDownStage();
    }

    /**
     * Returns the data to be loaded into the file in {@link #getDataFileLocation()}.
     */
    protected PocketProject getInitialData() {
        return TestUtil.typicalPocketProject();
    }

    /**
     * Returns the directory of the data file.
     */
    protected Path getDataFileLocation() {
        return TestApp.SAVE_LOCATION_FOR_TESTING;
    }

    public MainWindowHandle getMainWindowHandle() {
        return mainWindowHandle;
    }

    public CommandBoxHandle getCommandBox() {
        return mainWindowHandle.getCommandBox();
    }

    public EmployeeListPanelHandle getEmployeeListPanel() {
        return mainWindowHandle.getEmployeeListPanel();
    }

    public MainMenuHandle getMainMenu() {
        return mainWindowHandle.getMainMenu();
    }

    public StatusBarFooterHandle getStatusBarFooter() {
        return mainWindowHandle.getStatusBarFooter();
    }

    public ResultDisplayHandle getResultDisplay() {
        return mainWindowHandle.getResultDisplay();
    }

    public ProjectListPanelHandle getProjectListPanel() {
        return mainWindowHandle.getProjectListPanel();
    }

    /**
     * Executes {@code command} in the application's {@code CommandBox}.
     * Method returns after UI components have been updated.
     */
    protected void executeCommand(String command) {
        rememberStates();
        // Injects a fixed clock before executing a command so that the time stamp shown in the status bar
        // after each command is predictable and also different from the previous command.
        clockRule.setInjectedClockToCurrentTime();

        mainWindowHandle.getCommandBox().run(command);

    }

    /**
     * Displays all employees in the pocket project
     */
    protected void showAllEmployees() {
        executeCommand(ListCommand.COMMAND_WORD + " " + ListEmployeeCommand.LIST_EMPLOYEE_KEYWORD);
        assertEquals(getModel().getPocketProject().getEmployeeList().size(),
            getModel().getFilteredEmployeeList().size());
    }

    /**
     * Displays all projects in the pocket project.
     */
    protected void showAllProjects() {
        executeCommand(ListCommand.COMMAND_WORD + " " + ListProjectCommand.LIST_PROJECT_KEYWORD);
        assertEquals(getModel().getPocketProject().getProjectList().size(),
            getModel().getFilteredProjectList().size());
    }

    /**
     * Displays all employees with any parts of their names matching {@code keyword} (case-insensitive).
     */
    protected void showEmployeesWithName(String keyword) {
        executeCommand(FindEmployeeCommand.COMMAND_WORD + " " + FindEmployeeCommand.FIND_EMPLOYEE_KEYWORD
            + " " + keyword);
        assertTrue(getModel().getFilteredEmployeeList().size()
            < getModel().getPocketProject().getEmployeeList().size());
    }

    /**
     * Displays all projects with any parts of their names matching {@code keyword} (case-insensitive).
     */
    protected void showProjectsWithName(String keyword) {
        executeCommand(FindProjectCommand.COMMAND_WORD + " " + FindProjectCommand.FIND_PROJECT_KEYWORD
            + " " + keyword);
        assertTrue(getModel().getFilteredProjectList().size()
            < getModel().getPocketProject().getProjectList().size());
    }

    /**
     * Views the employee at {@code index} of the displayed list.
     */
    protected void viewEmployee(Index index) {
        executeCommand(ViewCommand.COMMAND_WORD + " " + ViewEmployeeCommand.VIEW_EMPLOYEE_KEYWORD + " "
            + index.getOneBased());
        assertEquals(index.getZeroBased(), getEmployeeListPanel().getSelectedCardIndex());
    }

    //TODO SET UP GUI CLASSES AND VARIABLES FOR TESTING
    /**
     * Views the project at {@code index} of the displayed list.
     */
    protected void viewProject(Index index) {
        //executeCommand(ViewCommand.COMMAND_WORD + " " + ViewProjectCommand.VIEW_PROJECT_KEYWORD + " "
        //        + index.getOneBased());
        //assertEquals(index.getZeroBased(), getProjectListPanel().getSelectedCardIndex());
    }

    /**
     * * Deletes all employees in the pocket project.
     */
    protected void deleteAllEmployees() {
        executeCommand(ClearCommand.COMMAND_WORD);
        assertEquals(0, getModel().getPocketProject().getEmployeeList().size());
    }

    /**
     * Deletes all projects in the pocket project.
     */
    protected void deleteAllProjects() {
        executeCommand(ClearCommand.COMMAND_WORD);
        assertEquals(0, getModel().getPocketProject().getProjectList().size());
    }

    /**
     * Asserts that the {@code CommandBox} displays {@code expectedCommandInput}, the {@code ResultDisplay} displays
     * {@code expectedResultMessage}, the storage contains the same objects as {@code expectedModel}
     * and the employee list panel displays the employees in the model correctly.
     */
    protected void assertApplicationDisplaysExpected(String expectedCommandInput, String expectedResultMessage,
                                                     Model expectedModel) {
        assertEquals(expectedCommandInput, getCommandBox().getInput());
        assertEquals(expectedResultMessage, getResultDisplay().getText());
        assertEquals(new PocketProject(expectedModel.getPocketProject()), testApp.readStoragePocketProject());
        assertListMatching(getEmployeeListPanel(), expectedModel.getFilteredEmployeeList());
    }

    /**
     * Calls {@code EmployeeListPanelHandle} and {@code StatusBarFooterHandle} to remember
     * their current state.
     */
    private void rememberStates() {
        StatusBarFooterHandle statusBarFooterHandle = getStatusBarFooter();
        statusBarFooterHandle.rememberSaveLocation();
        statusBarFooterHandle.rememberSyncStatus();
        getEmployeeListPanel().rememberSelectedEmployeeCard();
        getProjectListPanel().rememberSelectedProjectCard();
    }

    /**
     * Asserts that the previously selected card is now deselected and the browser's url is now displaying the
     * default page.
     */
    protected void assertSelectedCardDeselected() {
        assertFalse(getEmployeeListPanel().isAnyCardSelected());
    }

    /**
     * Asserts that the browser's url is changed to display the details of the employee in the employee list panel at
     * {@code expectedSelectedCardIndex}, and only the card at {@code expectedSelectedCardIndex} is selected.
     * @see EmployeeListPanelHandle#isSelectedEmployeeCardChanged()
     */
    protected void assertSelectedCardChanged(Index expectedSelectedCardIndex) {
        getEmployeeListPanel().navigateToCard(getEmployeeListPanel().getSelectedCardIndex());
        String selectedCardName = getEmployeeListPanel().getHandleToSelectedCard().getName();
        assertEquals(expectedSelectedCardIndex.getZeroBased(), getEmployeeListPanel().getSelectedCardIndex());
    }

    /**
     * Asserts that the browser's url is changed to display the details of the project in the project list panel at
     * {@code expectedSelectedCardIndex}, and only the card at {@code expectedSelectedCardIndex} is selected.
     */
    protected void assertSelectedProjectCardChanged(Index expectedSelectedCardIndex) {
        getProjectListPanel().navigateToCard(getProjectListPanel().getSelectedCardIndex());
        String selectedCardName = getProjectListPanel().getHandleToSelectedCard().getProjectName();
        assertEquals(expectedSelectedCardIndex.getZeroBased(), getProjectListPanel().getSelectedCardIndex());
    }

    /**
     * Asserts that the browser's url and the selected card in the employee list panel remain unchanged.
     * @see EmployeeListPanelHandle#isSelectedEmployeeCardChanged()
     */
    protected void assertSelectedCardUnchanged() {
        assertFalse(getEmployeeListPanel().isSelectedEmployeeCardChanged());
    }

    protected void assertSelectedProjectCardUnchanged() {
        assertFalse(getProjectListPanel().isSelectedProjectCardChanged());
    }

    /**
     * Asserts that the command box's shows the default style.
     */
    protected void assertCommandBoxShowsDefaultStyle() {
        assertEquals(COMMAND_BOX_DEFAULT_STYLE, getCommandBox().getStyleClass());
    }

    /**
     * Asserts that the command box's shows the error style.
     */
    protected void assertCommandBoxShowsErrorStyle() {
        assertEquals(COMMAND_BOX_ERROR_STYLE, getCommandBox().getStyleClass());
    }

    /**
     * Asserts that the entire status bar remains the same.
     */
    protected void assertStatusBarUnchanged() {
        StatusBarFooterHandle handle = getStatusBarFooter();
        assertFalse(handle.isSaveLocationChanged());
        assertFalse(handle.isSyncStatusChanged());
    }

    /**
     * Asserts that only the sync status in the status bar was changed to the timing of
     * {@code ClockRule#getInjectedClock()}, while the save location remains the same.
     */
    protected void assertStatusBarUnchangedExceptSyncStatus() {
        StatusBarFooterHandle handle = getStatusBarFooter();
        String timestamp = new Date(clockRule.getInjectedClock().millis()).toString();
        String expectedSyncStatus = String.format(SYNC_STATUS_UPDATED, timestamp);
        assertEquals(expectedSyncStatus, handle.getSyncStatus());
        assertFalse(handle.isSaveLocationChanged());
    }

    /**
     * Asserts that the starting state of the application is correct.
     */
    private void assertApplicationStartingStateIsCorrect() {
        assertEquals("", getCommandBox().getInput());
        assertEquals("", getResultDisplay().getText());
        assertListMatching(getEmployeeListPanel(), getModel().getFilteredEmployeeList());
        assertEquals(Paths.get(".").resolve(testApp.getStorageSaveLocation()).toString(),
            getStatusBarFooter().getSaveLocation());
        assertEquals(SYNC_STATUS_INITIAL, getStatusBarFooter().getSyncStatus());
    }

    /**
     * Returns a defensive copy of the current model.
     */
    protected Model getModel() {
        return testApp.getModel();
    }

    /**
     * Returns a defensive copy of the current project model.
     */
    protected Model getProjectModel() {
        return testApp.getProjectModel();
    }

}
