package seedu.address.model.employee;

import static java.util.Objects.requireNonNull;
import static java.util.logging.Level.FINE;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.employee.exceptions.DuplicateEmployeeException;
import seedu.address.model.employee.exceptions.EmployeeNotFoundException;
import seedu.address.model.project.UniqueMilestoneList;

/**
 * A list of employees that enforces uniqueness between its elements and does not allow nulls.
 * An employee is considered unique by comparing using {@code Employee#isSameEmployee(Employee)}. As such, adding and
 * updating of employees uses Employee#isSameEmployee(Employee) for equality so as to ensure that the employee being
 * added or updated is unique in terms of identity in the UniqueEmployeeList. However, the removal of an employee uses
 * Employee#equals(Object) so as to ensure that the employee with exactly the same fields will be removed.
 *
 * Supports a minimal set of list operations.
 *
 * @see Employee#isSameEmployee(Employee)
 */
public class UniqueEmployeeList implements Iterable<Employee> {

    private static final Logger logger = LogsCenter.getLogger(UniqueEmployeeList.class);

    private final ObservableList<Employee> internalList = FXCollections.observableArrayList();
    private final ObservableList<Employee> internalUnmodifiableList =
            FXCollections.unmodifiableObservableList(internalList);

    /**
     * Returns true if the list contains an equivalent employee as the given argument.
     * To keep the employeeList unique, same name allowed
     */
    public boolean contains(Employee toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::isSameEmployee);
    }

    /**
     * Return true if the list contains the exact same employee.
     * To identify if the employee exists in the list
     */
    public boolean containsExactSameEmployee(Employee toCheck) {
        requireNonNull(toCheck);
        return internalList.stream().anyMatch(toCheck::equals);
    }

    /**
     * Adds an employee to the list.
     * The employee must not already exist in the list.
     */
    public void add(Employee toAdd) {
        requireNonNull(toAdd);
        if (contains(toAdd)) {
            throw new DuplicateEmployeeException();
        }
        internalList.add(toAdd);
        logger.log(FINE, "Added new employee: " + toAdd.getEmployeeName() + toAdd.getPhone()
            + toAdd.getGithub() + toAdd.getEmail() + toAdd.getSkills());
    }

    /**
     * Replaces the employee {@code target} in the list with {@code editedEmployee}.
     * {@code target} must exist in the list.
     * The employee identity of {@code editedEmployee} must not be the same as another existing employee in the list.
     */
    public void setEmployee(Employee target, Employee editedEmployee) {
        requireAllNonNull(target, editedEmployee);

        int index = internalList.indexOf(target);
        if (index == -1) {
            throw new EmployeeNotFoundException();
        }

        if (!target.isSameEmployee(editedEmployee) && contains(editedEmployee)) {
            throw new DuplicateEmployeeException();
        }

        internalList.set(index, editedEmployee);
    }

    /**
     * Removes the equivalent employee from the list.
     * The employee must exist in the list.
     */
    public void remove(Employee toRemove) {
        requireNonNull(toRemove);
        if (!internalList.remove(toRemove)) {
            throw new EmployeeNotFoundException();
        }
    }

    public void setEmployees(UniqueEmployeeList replacement) {
        requireNonNull(replacement);
        internalList.setAll(replacement.internalList);
    }

    /**
     * Replaces the contents of this list with {@code employees}.
     * {@code employees} must not contain duplicate employees.
     */
    public void setEmployees(List<Employee> employees) {
        requireAllNonNull(employees);
        if (!employeesAreUnique(employees)) {
            throw new DuplicateEmployeeException();
        }

        internalList.setAll(employees);
    }

    /**
     * Returns the backing list as an unmodifiable {@code ObservableList}.
     */
    public ObservableList<Employee> asUnmodifiableObservableList() {
        return internalUnmodifiableList;
    }

    /**
     * Returns a clone of this UniqueEmployeeList object.
     */
    public UniqueEmployeeList clone() {
        UniqueEmployeeList newList = new UniqueEmployeeList();
        for (Employee e: internalList) {
            newList.add(e.clone());
        }
        return newList;
    }

    @Override
    public Iterator<Employee> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueEmployeeList // instanceof handles nulls
                        && internalList.equals(((UniqueEmployeeList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }

    /**
     * Returns true if {@code employees} contains only unique employees.
     */
    private boolean employeesAreUnique(List<Employee> employees) {
        for (int i = 0; i < employees.size() - 1; i++) {
            for (int j = i + 1; j < employees.size(); j++) {
                if (employees.get(i).isSameEmployee(employees.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }
}
