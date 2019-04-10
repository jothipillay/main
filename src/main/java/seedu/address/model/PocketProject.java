package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import seedu.address.commons.util.InvalidationListenerManager;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.UniqueEmployeeList;
import seedu.address.model.project.Milestone;
import seedu.address.model.project.Project;
import seedu.address.model.project.ProjectTask;
import seedu.address.model.project.UniqueProjectList;
import seedu.address.model.project.UserStory;
import seedu.address.model.project.UserStoryStatus;
import seedu.address.model.util.PocketProjectDate;

/**
 * Wraps all data at the pocket-project level
 * Duplicates are not allowed (by .isSameEmployee comparison)
 */
public class PocketProject implements ReadOnlyPocketProject {

    private final UniqueEmployeeList employees;
    private final UniqueProjectList projects;
    private final UniqueProjectList completedProjects;
    private final InvalidationListenerManager invalidationListenerManager = new InvalidationListenerManager();

    /*
     * The 'unusual' code block below is an non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        employees = new UniqueEmployeeList();
        projects = new UniqueProjectList();
        completedProjects = new UniqueProjectList();
    }

    public PocketProject() {}

    /**
     * Creates an PocketProject using the Employees in the {@code toBeCopied}
     */
    public PocketProject(ReadOnlyPocketProject toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the employee list with {@code employees}.
     * {@code employees} must not contain duplicate employees.
     */
    public void setEmployees(List<Employee> employees) {
        this.employees.setEmployees(employees);
        indicateModified();
    }

    /**
     * Replaces the contents of the project list with {@code projects}.
     * {@code projects} must not contain duplicate projects.
     */
    public void setProjects(List<Project> projects) {
        this.projects.setProjects(projects);
        indicateModified();
    }

    /**
     * Replaces the contents of the completed project list with {@code projects}.
     * {@code projects} must not contain duplicate projects.
     */
    public void setCompletedProjects(List<Project> projects) {
        this.completedProjects.setProjects(projects);
    }

    /**
     * Resets the existing data of this {@code PocketProject} with {@code newData}.
     */
    public void resetData(ReadOnlyPocketProject newData) {
        requireNonNull(newData);
        List<Employee> employeeList = new ArrayList<>();
        List<Project> projectList = new ArrayList<>();
        List<Project> completedProjectList = new ArrayList<>();
        for (Employee e: newData.getEmployeeList()) {
            employeeList.add(e.clone());
        }
        for (Project p: newData.getProjectList()) {
            projectList.add(p.clone());
        }
        for (Project p: newData.getCompletedProjectList()) {
            completedProjectList.add(p.clone());
        }

        setEmployees(employeeList);
        setProjects(projectList);
        setCompletedProjects(completedProjectList);
    }



    //// employee-level and project-level operations

    /**
     * Returns true if a employee with the same identity as {@code employee} exists in the pocket project.
     */
    public boolean hasEmployee(Employee employee) {
        requireNonNull(employee);
        return employees.contains(employee);
    }

    /**
     * Returns true if a project with the same name as {@code project} exists in the pocket project.
     */
    public boolean hasProject(Project project) {
        requireNonNull(project);
        return projects.contains(project);
    }

    /**
     * Returns true if a completed project with the same name as {@code project} exists in the pocket project.
     */
    public boolean hasCompletedProject(Project project) {
        requireNonNull(project);
        return completedProjects.contains(project);
    }

    /**
     * Adds a employee to the pocket project.
     * The employee must not already exist in the pocket project.
     */
    public void addEmployee(Employee p) {
        employees.add(p);
        indicateModified();
    }

    /**
     * Adds a project to the pocket project.
     * The project must not already exist in the pocket project.
     */
    public void addProject(Project p) {
        projects.add(p);
        indicateModified();
    }

    /**
     * Replaces the given employee {@code target} in the list with {@code editedEmployee}.
     * {@code target} must exist in the employee list.
     * The employee identity of {@code editedEmployee} must not be the same as another existing employee in the pocket
     * project.
     */
    public void setEmployee(Employee target, Employee editedEmployee) {
        requireNonNull(editedEmployee);

        employees.setEmployee(target, editedEmployee);

        for (Project p: projects) {
            if (p.containsEmployee(target)) {
                p.setEmployee(target, editedEmployee);
            }
        }
        indicateModified();
    }

    /**
     * Replaces the given project {@code target} in the list with {@code editedProject}.
     * {@code target} must exist in the pocket project.
     * The project name of {@code editedProject} must not be the same as another existing project in the pocket
     * project.
     */
    public void setProject(Project target, Project editedProject) {
        requireNonNull(editedProject);

        //if the project name is changed, update the project names in the employee
        if (!target.getProjectName().equals(editedProject.getProjectName())) {
            ObservableList<Employee> employeeListInProject = target.getEmployees();
            employees.forEach((em) -> {
                if (employeeListInProject.contains(em)) {
                    em.updateProjectName(target, editedProject);
                }
            });
        }

        projects.setProject(target, editedProject);
        indicateModified();
    }

    /**
     * Removes {@code project} from the list of ongoing projects and add it to the list of completed projects.
     * {@code project} must exist in the pocket project.
     */
    public void completeProject(Project project, PocketProjectDate completionDate) {
        removeProject(project);
        project.setCompletionDate(completionDate);
        completedProjects.add(project);
    }

    /**
     * Add a {@code project} into the list of completed projects.
     */
    public void addCompletedProject(Project project) {
        completedProjects.add(project);
    }

    /**
     * Removes {@code employee} from this {@code PocketProject}.
     * Will also remove from current projects that the {@code employee} is working on.
     * {@code employee} must exist in the pocket project.
     */
    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        for (Project p: projects) {
            if (p.containsEmployee(employee)) {
                p.removeEmployee(employee);
            }
        }
        indicateModified();
    }

    /**
     * Removes {@code project} from this {@code PocketProject}.
     * {@code project} must exist in the pocket project.
     */
    public void removeProject(Project project) {
        for (Employee e: employees) {
            e.getCurrentProjects().remove(project.getProjectName());
        }
        projects.remove(project);
        indicateModified();
    }

    /**
     * Removes {@code targetEmployee} from the {@code targetProject} from this {@code PocketProject}.
     *  {@code targetProject} and {@code targetEmployee} must exist.
     */
    public void removeEmployeeFrom(Project targetProject, Employee targetEmployee) {
        projects.removeEmployeeFrom(targetProject, targetEmployee);
        indicateModified();
    }

    /**
     * Removes {@code targetMilestone} from the {@code targetProject} from this {@code PocketProject}.
     *  {@code targetProject} and {@code targetMilestone} must exist.
     */
    public void removeMilestoneFrom(Project targetProject, Milestone targetMilestone) {
        projects.removeMilestoneFrom(targetProject, targetMilestone);
        indicateModified();
    }

    /**
     * Removes {@code targetUserStory} from the {@code targetProject} from this {@code PocketProject}.
     *  {@code targetProject} and {@code targetUserStory} must exist.
     */
    public void removeUserStoryFrom(Project targetProject, UserStory targetUserStory) {
        projects.removeUserStoryFrom(targetProject, targetUserStory);
        indicateModified();
    }

    /**
     *
     */
    public void updateUserStory(Project targetProject, UserStory targetStory, UserStoryStatus newStatus) {
        projects.updateUserStory(targetProject, targetStory, newStatus);
        indicateModified();
    }

    /**
     * Removes {@code targetEmployee} from the {@code targetProject} from this {@code PocketProject}.
     *  {@code targetProject} and {@code targetEmployee} must exist.
     */
    public void addEmployeeTo(Project targetProject, Employee targetEmployee) {
        Project localTargetProject = null;
        Employee localTargetEmployee = null;
        for (Project p : projects) {
            if (p.isSameProject(targetProject)) {
                localTargetProject = p;
            }
        }
        for (Employee e : employees) {
            if (e.isSameEmployee(targetEmployee)) {
                localTargetEmployee = e;
            }
        }
        assert localTargetEmployee != null;
        assert localTargetProject != null;
        localTargetProject.addEmployee(localTargetEmployee);
        localTargetEmployee.join(localTargetProject);
        indicateModified();
    }

    /**
     * Adds {@code targetMilestone} from the {@code targetProject} to this {@code PocketProject}.
     *  {@code targetProject} and {@code targetMilestone} must exist.
     */
    public void addMilestoneTo(Project targetProject, Milestone targetMilestone) {
        projects.addMilestoneTo(targetProject, targetMilestone);
        indicateModified();
    }

    /**
     * Adds {@code targetUserStory} from the {@code targetProject} to this {@code PocketProject}.
     *  {@code targetProject} and {@code targetUserStory} must exist.
     */
    public void addUserStoryTo(Project targetProject, UserStory userStory) {
        projects.addUserStoryTo(targetProject, userStory);
        indicateModified();
    }

    /**
     * Adds {@code task} to the {@code milestone} in this {@code PocketProject}.
     *  {@code task} and {@code milestone} must exist.
     */
    public void addProjectTaskTo(Project targetProject, Milestone milestone, ProjectTask task) {
        projects.addProjectTaskTo(targetProject, milestone, task);
        indicateModified();
    }

    @Override
    public void addListener(InvalidationListener listener) {
        invalidationListenerManager.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        invalidationListenerManager.removeListener(listener);
    }

    /**
     * Notifies listeners that the pocket project has been modified.
     */
    protected void indicateModified() {
        invalidationListenerManager.callListeners(this);
    }

    //// util methods

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("employees:\n");
        for (Employee e: employees) {
            builder.append(e);
            builder.append("\n");
        }
        builder.append("projects:\n");
        for (Project p: projects) {
            builder.append(p);
            builder.append("\n");
        }
        builder.append("completed projects:\n");
        for (Project p: completedProjects) {
            builder.append(p);
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public ObservableList<Employee> getEmployeeList() {
        return employees.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Project> getProjectList() {
        return projects.asUnmodifiableObservableList();
    }
    @Override
    public ObservableList<Project> getCompletedProjectList() {
        return completedProjects.asUnmodifiableObservableList();
    }
    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof PocketProject // instanceof handles nulls
            && employees.equals(((PocketProject) other).employees)
            && projects.equals(((PocketProject) other).projects)
            && completedProjects.equals(((PocketProject) other).completedProjects));
    }

    @Override
    public int hashCode() {
        Object[] array = {this.projects, this.employees, this.completedProjects};
        return Arrays.hashCode(array);
    }


}
