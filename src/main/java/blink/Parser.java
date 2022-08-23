package blink;

import blink.command.*;

/**
 * Reads the user input to figure out the command written.
 */
public class Parser {

    /**
     * Parses the input to check if it is a valid command.
     *
     * @param input User input which contains the command
     * and its other parameters
     * @return Command that user is trying to call
     * @throws BlinkException Thrown when there is error with
     * command input
     */
    public static Command parse(String input)  {
        String[] info = input.split(" ", 2);
        String command = info[0].strip().toUpperCase();
        switch (command) {
            case "BYE":
                return new ByeCommand();
            case "DEADLINE":
                if (info.length == 1) {
                    throw new BlinkException("Missing description for deadline");
                }
                return new DeadlineCommand(info[1]);
            case "DELETE":
                if (info.length == 1) {
                    throw new BlinkException("Missing number input");
                }
                int pos = Integer.parseInt(info[1].strip());
                return new DeleteCommand(pos);
            case "EVENT":
                if (info.length == 1) {
                    throw new BlinkException("Missing description for event");
                }
                return new EventCommand(info[1]);
            case "FILTER":
                if (info.length == 1) {
                    throw new BlinkException("Missing date input");
                }
                return new FilterCommand(info[1].strip());
            case "LIST":
                return new ListCommand();
            case "MARK":
                if (info.length == 1) {
                    throw new BlinkException("Missing number input");
                }
                int num = Integer.parseInt(info[1].strip());
                return new MarkCommand(num);
            case "TODO":
                if (info.length == 1) {
                    throw new BlinkException("Missing description for todo");
                }
                return new TodoCommand(info[1]);
            case "UNMARK":
                if (info.length == 1) {
                    throw new BlinkException("Missing number input");
                }
                int val = Integer.parseInt(info[1].strip());
                return new UnmarkCommand(val);
            default:
                if (info[0].isBlank()) {
                    throw new BlinkException("Missing command input");
                } else {
                    throw new BlinkException("Unknown command found");
                }
        }
    }
}
