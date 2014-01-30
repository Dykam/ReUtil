package nl.dykam.dev.reutil.commands.parsing;

import nl.dykam.dev.reutil.commands.ParseResult;
import org.bukkit.command.CommandSender;

public class ExecutionList {
    Node first;
    public void execute(CommandSender commandSender, String[] arguments) {
        first.execute(commandSender, arguments, 0);
    }

    class Node {
        public Node next;
        public ParsedMethodParam param;
        public ArgumentParser<?> parser;

        /**
         * Try to applies the parser to the current argument. If optional, try working match first before skipping.
         * @param commandSender
         * @param arguments
         * @param i
         */
        public boolean execute(CommandSender commandSender, String[] arguments, int i) {
            String argument = arguments[i];
            ParseResult<?> parseResult = parser.parse(null, argument);
            if (parseResult.isSuccess() && proceed(commandSender, arguments, i + 1)) return true;
            if(parseResult.isFailure() && param.isOptional())
                return proceed(commandSender, arguments, i);
            return false;
        }

        private boolean proceed(CommandSender commandSender, String[] arguments, int i) {
            if(next == null) {
                // Reached the on both the arguments and the parser chain
                return arguments.length == i;
            }
            // Not enough arguments
            if(arguments.length <= i)
                return false;
            return next.execute(commandSender, arguments, i);
        }
    }
}
