package seedu.address.logic;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.PocketProjectParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyPocketProject;
import seedu.address.model.employee.Employee;
import seedu.address.model.project.Project;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_MESSAGE = "Could not save data to file: ";
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final CommandHistory history;
    private final PocketProjectParser pocketProjectParser;
    private boolean pocketProjectModified;

    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        history = new CommandHistory();
        pocketProjectParser = new PocketProjectParser();

        // Set pocketProjectModified to true whenever the models' pocket project is modified.
        model.getPocketProject().addListener(observable -> pocketProjectModified = true);
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        pocketProjectModified = false;

        CommandResult commandResult;
        try {
            Command command = pocketProjectParser.parseCommand(commandText);
            commandResult = command.execute(model, history);
        } finally {
            history.add(commandText);
        }

        if (pocketProjectModified) {
            logger.info("Pocket project modified, saving to file.");
            try {
                storage.savePocketProject(model.getPocketProject());
            } catch (IOException ioe) {
                throw new CommandException(FILE_OPS_ERROR_MESSAGE + ioe, ioe);
            }
        }
        logger.info(commandText);
        return commandResult;
    }

    @Override
    public ReadOnlyPocketProject getPocketProject() {
        return model.getPocketProject();
    }

    @Override
    public ObservableList<Employee> getFilteredEmployeeList() {
        return model.getFilteredEmployeeList();
    }

    @Override
    public ObservableList<Project> getFilteredProjectList() {
        return model.getFilteredProjectList();
    }

    @Override
    public ObservableList<String> getHistory() {
        return history.getHistory();
    }

    @Override
    public Path getPocketProjectFilePath() {
        return model.getPocketProjectFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    @Override
    public ReadOnlyProperty<Employee> selectedEmployeeProperty() {
        return model.selectedEmployeeProperty();
    }

    @Override
    public void setSelectedEmployee(Employee employee) {
        model.setSelectedEmployee(employee);
    }

    @Override
    public ReadOnlyProperty<Project> selectedProjectProperty() {
        return model.selectedProjectProperty();
    }

    @Override
    public void setSelectedProject(Project project) {
        model.setSelectedProject(project);
    }
}
