package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.ArgumentMultimap.areAllPrefixesPresent;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLIENT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DEADLINE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_GITHUB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SKILL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_START_DATE;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddEmployeeCommand;
import seedu.address.logic.commands.AddProjectCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.employee.Email;
import seedu.address.model.employee.Employee;
import seedu.address.model.employee.EmployeeName;
import seedu.address.model.employee.GitHubAccount;
import seedu.address.model.employee.Phone;
import seedu.address.model.project.Client;
import seedu.address.model.project.Project;
import seedu.address.model.project.ProjectName;
import seedu.address.model.skill.Skill;
import seedu.address.model.util.PocketProjectDate;


/**
 * Parses input arguments and creates a new AddEmployeeCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Used for separation of add type word and args.
     */
    private static final Pattern ADD_COMMAND_FORMAT = Pattern.compile("(?<keyword>\\S+)(?<arguments>.*)");

    /**
     * Parses the given {@code String} of arguments in the context of the AddEmployeeCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {

        final Matcher matcher = ADD_COMMAND_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        final String keyword = matcher.group("keyword").toLowerCase();
        final String arguments = matcher.group("arguments");

        if (keyword.equals(AddEmployeeCommand.ADD_EMPLOYEE_KEYWORD)) {
            ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(arguments, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_GITHUB,
                    PREFIX_SKILL);

            if (!areAllPrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_GITHUB, PREFIX_PHONE, PREFIX_EMAIL)
                || !argMultimap.getPreamble().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddEmployeeCommand.MESSAGE_USAGE));
            }

            EmployeeName employeeName = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
            Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
            Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
            GitHubAccount gitHubAccount = ParserUtil.parseAccount(argMultimap.getValue(PREFIX_GITHUB).get());
            Set<Skill> skillList = ParserUtil.parseSkills(argMultimap.getAllValues(PREFIX_SKILL));

            Employee employee = new Employee(employeeName, phone, email, gitHubAccount, skillList);

            return new AddEmployeeCommand(employee);

        } else if (keyword.equals(AddProjectCommand.ADD_PROJECT_KEYWORD)) {
            ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(arguments, PREFIX_NAME, PREFIX_CLIENT, PREFIX_START_DATE, PREFIX_DEADLINE);

            if (!areAllPrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_CLIENT, PREFIX_START_DATE, PREFIX_DEADLINE)
                || !argMultimap.getPreamble().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddProjectCommand.MESSAGE_USAGE));
            }

            ProjectName projectName = ParserUtil.parseProjectName(argMultimap.getValue(PREFIX_NAME).get());
            Client client = ParserUtil.parseClient(argMultimap.getValue(PREFIX_CLIENT).get());
            PocketProjectDate startDate = ParserUtil.parseDate(argMultimap.getValue(PREFIX_START_DATE).get());
            PocketProjectDate deadline = ParserUtil.parseDate(argMultimap.getValue(PREFIX_DEADLINE).get());

            if (!startDate.isEarlierThan(startDate, deadline)) {
                throw new ParseException(String.format(PocketProjectDate.START_END_DATE_CONSTRAINTS));
            }

            Project project = new Project(projectName, client, startDate, deadline);

            return new AddProjectCommand(project);

        } else {
            throw new ParseException (String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

    }

}
