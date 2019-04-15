package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.FindAllCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.FindDeadlineCommand;
import seedu.address.logic.commands.FindEmployeeCommand;
import seedu.address.logic.commands.FindProjectCommand;
import seedu.address.logic.commands.FindSkillCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.employee.EmployeeNameContainsKeywordsPredicate;
import seedu.address.model.project.ProjectContainsDeadlinePredicate;
import seedu.address.model.project.ProjectContainsKeywordsPredicate;
import seedu.address.model.project.ProjectNameContainsKeywordsPredicate;
import seedu.address.model.skill.EmployeeSkillContainsKeywordsPredicate;
import seedu.address.model.util.PocketProjectDate;

/**
 * Parses input arguments and creates a new FindEmployeeCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static final Pattern FIND_COMMAND_FORMAT = Pattern.compile("(?<keyword>\\S+)(?<arguments>.*)");

    /**
     * Parses the given {@code String} of arguments in the context of the FindEmployeeCommand
     * and returns an FindEmployeeCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */

    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        final Matcher matcher = FIND_COMMAND_FORMAT.matcher(trimmedArgs);

        if (!matcher.matches()) {
            throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        final String keyword = matcher.group("keyword").toLowerCase();
        final String arguments = matcher.group("arguments");
        String[] nameKeywords = arguments.trim().split("\\s+");

        if (arguments.isEmpty()) {
            throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        if (keyword.equals(FindEmployeeCommand.FIND_EMPLOYEE_KEYWORD)) {
            return new FindEmployeeCommand(new EmployeeNameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
        } else if (keyword.equals(FindProjectCommand.FIND_PROJECT_KEYWORD)) {
            return new FindProjectCommand(new ProjectNameContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
        } else if (keyword.equals(FindSkillCommand.FIND_SKILL_KEYWORD)) {
            return new FindSkillCommand(new EmployeeSkillContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
        } else if (keyword.equals(FindAllCommand.FIND_ALL_KEYWORD)) {
            return new FindAllCommand(new ProjectContainsKeywordsPredicate(Arrays.asList(nameKeywords)));
        } else if (keyword.equals(FindDeadlineCommand.FIND_DEADLINE_KEYWORD)) {
            PocketProjectDate inputDeadline = ParserUtil.parseDate(arguments);
            return new FindDeadlineCommand(new ProjectContainsDeadlinePredicate(inputDeadline.date));
        } else {
            throw new ParseException(
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindEmployeeCommand.MESSAGE_USAGE));
        }
    }
}
