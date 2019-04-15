package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.exceptions.EmployeeNotFoundException;
import seedu.address.model.project.Milestone;
import seedu.address.model.project.Project;
import seedu.address.model.project.ProjectName;
import seedu.address.model.project.ProjectTask;
import seedu.address.model.project.Status;
import seedu.address.model.project.UserStory;
import seedu.address.model.project.exceptions.ProjectNotFoundException;
import seedu.address.model.util.PocketProjectDate;
import seedu.address.model.util.StatsUtil;

/**
 * Represents the in-memory model of the pocket project data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final VersionedPocketProject versionedPocketProject;
    private final UserPrefs userPrefs;
    private final FilteredList<Employee> filteredEmployees;
    private final FilteredList<Project> filteredProjects;
    private final SimpleObjectProperty<Employee> selectedEmployee = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Project> selectedProject = new SimpleObjectProperty<>();

    /**
     * Initializes a ModelManager with the given pocketProject and userPrefs.
     */
    public ModelManager(ReadOnlyPocketProject pocketProject, ReadOnlyUserPrefs userPrefs) {
        super();
        requireAllNonNull(pocketProject, userPrefs);

        logger.fine("Initializing with pocket project: " + pocketProject + " and user prefs " + userPrefs);

        versionedPocketProject = new VersionedPocketProject(pocketProject);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredEmployees = new FilteredList<>(versionedPocketProject.getEmployeeList());
        filteredEmployees.addListener(this::ensureSelectedEmployeeIsValid);

        filteredProjects = new FilteredList<>(versionedPocketProject.getProjectList());
        filteredProjects.addListener(this::ensureSelectedProjectIsValid);

    }

    public ModelManager() {
        this(new PocketProject(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getPocketProjectFilePath() {
        return userPrefs.getPocketProjectFilePath();
    }

    @Override
    public void setPocketProjectFilePath(Path pocketProjectFilePath) {
        requireNonNull(pocketProjectFilePath);
        userPrefs.setPocketProjectFilePath(pocketProjectFilePath);
    }

    //=========== PocketProject ================================================================================

    @Override
    public void setPocketProject(ReadOnlyPocketProject pocketProject) {
        versionedPocketProject.resetData(pocketProject);
    }

    @Override
    public ReadOnlyPocketProject getPocketProject() {
        return versionedPocketProject;
    }

    @Override
    public boolean hasEmployee(Employee employee) {
        requireNonNull(employee);
        return versionedPocketProject.hasEmployee(employee);
    }

    @Override
    public boolean hasProject(Project project) {
        requireNonNull(project);
        return versionedPocketProject.hasProject(project);
    }

    @Override
    public void deleteEmployee(Employee target) {
        versionedPocketProject.removeEmployee(target);
    }

    @Override
    public void deleteProject(Project target) {
        versionedPocketProject.removeProject(target);
    }

    @Override
    public void addEmployee(Employee employee) {
        versionedPocketProject.addEmployee(employee);
        updateFilteredEmployeeList(PREDICATE_SHOW_ALL_EMPLOYEES);
    }

    @Override
    public void addProject(Project project) {
        versionedPocketProject.addProject(project);
        updateFilteredProjectList(PREDICATE_SHOW_ALL_PROJECTS);
    }

    @Override
    public void setEmployee(Employee target, Employee editedEmployee) {
        requireAllNonNull(target, editedEmployee);

        versionedPocketProject.setEmployee(target, editedEmployee);
    }

    @Override
    public void setProject(Project target, Project editedProject) {
        requireAllNonNull(target, editedProject);

        versionedPocketProject.setProject(target, editedProject);
    }
    @Override
    public Project getProjectWithName(ProjectName targetProjectName) {
        Project targetProject = null;
        for (Project p: versionedPocketProject.getProjectList()) {
            if (p.hasProjectName(targetProjectName)) {
                targetProject = p;
            }
        }
        return targetProject;
    }
    @Override
    public void completeProject(Project project, PocketProjectDate completionDate) {
        versionedPocketProject.completeProject(project, completionDate);
    }

    @Override
    public void removeEmployeeFrom(Project targetProject, Employee targetEmployee) {
        versionedPocketProject.removeEmployeeFrom(targetProject, targetEmployee);
    }
    @Override
    public void removeMilestoneFrom(Project targetProject, Milestone targetMilestone) {
        versionedPocketProject.removeMilestoneFrom(targetProject, targetMilestone);
    }
    @Override
    public void removeUserStoryFrom(Project targetProject, UserStory targetUserStory) {
        versionedPocketProject.removeUserStoryFrom(targetProject, targetUserStory);
    }
    @Override
    public void removeProjectTaskFrom(Project targetProject, Milestone targetMilestone, ProjectTask targetProjectTask) {
        versionedPocketProject.removeProjectTaskFrom(targetProject, targetMilestone, targetProjectTask);
    }

    @Override
    public void addEmployeeTo(Project targetProject, Employee targetEmployee) {
        versionedPocketProject.addEmployeeTo(targetProject, targetEmployee);
    }
    @Override
    public void addMilestoneTo(Project targetProject, Milestone milestone) {
        versionedPocketProject.addMilestoneTo(targetProject, milestone);
    }
    @Override
    public List<Project> getProjectsContaining(Employee employee) {
        List<Project> list = new ArrayList<>();
        for (Project p: versionedPocketProject.getProjectList()) {
            if (p.containsEmployee(employee)) {
                list.add(p);
            }
        }
        return list;
    }
    @Override
    public ObservableList<Employee> getEmployeeList() {
        return versionedPocketProject.getEmployeeList();
    }

    @Override
    public void addUserStoryTo(Project targetProject, UserStory targetUserStory) {
        versionedPocketProject.addUserStoryTo(targetProject, targetUserStory);
    }
    @Override
    public String overallStats() {
        return StatsUtil.overAllStatsString(getEmployeeList(), getProjectList(), getCompletedProjectList());
    }
    @Override
    public String individualStats(Project project) {
        return StatsUtil.individualStatsString(project);
    }

    @Override
    public void addProjectTaskTo(Project targetProject, Milestone milestone, ProjectTask task) {
        versionedPocketProject.addProjectTaskTo(targetProject, milestone, task);
    }

    @Override
    public void updateUserStory(Project targetProject, UserStory targetStory, Status newStatus) {
        versionedPocketProject.updateUserStory(targetProject, targetStory, newStatus);
    }

    @Override
    public void updateProjectTask(Project targetProject, Milestone targetMilestone, ProjectTask targetTask,
                                  Status newStatus) {
        versionedPocketProject.updateProjectTask(targetProject, targetMilestone, targetTask, newStatus);
    }


    //=========== Filtered Employee List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Employee} backed by the internal list of
     * {@code versionedPocketProject}
     */
    @Override
    public ObservableList<Employee> getFilteredEmployeeList() {
        return filteredEmployees;
    }

    @Override
    public void updateFilteredEmployeeList(Predicate<Employee> predicate) {
        requireNonNull(predicate);
        filteredEmployees.setPredicate(predicate);
    }

    //=========== Filtered Project List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Project} backed by the internal list of
     * {@code versionedPocketProject}
     */
    @Override
    public ObservableList<Project> getFilteredProjectList() {
        return filteredProjects;
    }

    @Override
    public void updateFilteredProjectList(Predicate<Project> predicate) {
        requireNonNull(predicate);
        filteredProjects.setPredicate(predicate);
    }

    //=========== Whole Project List Accessors ==============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Project} backed by the internal list of
     * {@code versionedPocketProject}
     */
    @Override
    public ObservableList<Project> getProjectList() {
        return versionedPocketProject.getProjectList();
    }

    /**
     * Returns an unmodifiable view of the list of completed {@code Project} backed by the internal list of
     * {@code versionedPocketProject}
     */
    @Override
    public ObservableList<Project> getCompletedProjectList() {
        return versionedPocketProject.getCompletedProjectList();
    }


    //=========== Undo/Redo =================================================================================

    @Override
    public boolean canUndoPocketProject() {
        return versionedPocketProject.canUndo();
    }

    @Override
    public boolean canRedoPocketProject() {
        return versionedPocketProject.canRedo();
    }

    @Override
    public void undoPocketProject() {
        versionedPocketProject.undo();
    }

    @Override
    public void redoPocketProject() {
        versionedPocketProject.redo();
    }

    @Override
    public void commitPocketProject() {
        versionedPocketProject.commit();
    }

    //=========== Selected employee ===========================================================================

    @Override
    public ReadOnlyProperty<Employee> selectedEmployeeProperty() {
        return selectedEmployee;
    }

    @Override
    public Employee getSelectedEmployee() {
        return selectedEmployee.getValue();
    }

    @Override
    public void setSelectedEmployee(Employee employee) {
        if (employee != null && !filteredEmployees.contains(employee)) {
            throw new EmployeeNotFoundException();
        }
        selectedProject.setValue((Project) null);
        selectedEmployee.setValue(employee);
    }

    /**
     * Ensures {@code selectedEmployee} is a valid employee in {@code filteredEmployees}.
     */
    private void ensureSelectedEmployeeIsValid(ListChangeListener.Change<? extends Employee> change) {
        while (change.next()) {
            if (selectedEmployee.getValue() == null) {
                // null is always a valid selected employee, so we do not need to check that it is valid anymore.
                return;
            }

            boolean wasSelectedEmployeeReplaced = change.wasReplaced()
                && change.getAddedSize() == change.getRemovedSize()
                && change.getRemoved().contains(selectedEmployee.getValue());
            if (wasSelectedEmployeeReplaced) {
                // Update selectedEmployee to its new value.
                int index = change.getRemoved().indexOf(selectedEmployee.getValue());
                selectedEmployee.setValue(change.getAddedSubList().get(index));
                continue;
            }

            boolean wasSelectedEmployeeRemoved = change.getRemoved().stream()
                .anyMatch(removedEmployee -> selectedEmployee.getValue().equals(removedEmployee));
            if (wasSelectedEmployeeRemoved) {
                // Select the employee that came before it in the list,
                // or clear the selection if there is no such employee.
                selectedEmployee.setValue(change.getFrom() > 0 ? change.getList().get(change.getFrom() - 1) : null);
            }
        }
    }

    //========= Selected project ==================================================================================

    @Override
    public ReadOnlyProperty<Project> selectedProjectProperty() {
        return selectedProject;
    }

    @Override
    public Project getSelectedProject() {
        return selectedProject.getValue();
    }

    @Override
    public void setSelectedProject(Project project) {
        if (project != null && !filteredProjects.contains(project)) {
            throw new ProjectNotFoundException();
        }
        selectedEmployee.setValue((Employee) null);
        selectedProject.setValue(project);
    }

    /**
     * Ensures {@code selectedProject} is a valid project in {@code filteredProject}.
     */
    private void ensureSelectedProjectIsValid(ListChangeListener.Change<? extends Project> change) {
        while (change.next()) {
            if (selectedProject.getValue() == null) {
                // null is always a valid selected project, so we do not need to check that it is valid anymore.
                return;
            }

            boolean wasSelectedProjectReplaced = change.wasReplaced()
                && change.getAddedSize() == change.getRemovedSize()
                && change.getRemoved().contains(selectedProject.getValue());
            if (wasSelectedProjectReplaced) {
                // Update selectedProject to its new value.
                int index = change.getRemoved().indexOf(selectedProject.getValue());
                selectedProject.setValue(change.getAddedSubList().get(index));
                continue;
            }

            boolean wasSelectedProjectRemoved = change.getRemoved().stream()
                .anyMatch(removedProject -> selectedProject.getValue().isSameProject(removedProject));
            if (wasSelectedProjectRemoved) {
                // Select the project that came before it in the list,
                // or clear the selection if there is no such project.
                selectedProject.setValue(change.getFrom() > 0 ? change.getList().get(change.getFrom() - 1) : null);
            }
        }
    }

    //===================================================================================================

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return versionedPocketProject.equals(other.versionedPocketProject)
            && userPrefs.equals(other.userPrefs)
            && filteredEmployees.equals(other.filteredEmployees)
            && filteredProjects.equals(other.filteredProjects)
            && Objects.equals(selectedEmployee.get(), other.selectedEmployee.get())
            && Objects.equals(selectedProject.get(), other.selectedProject.get());
    }

}
