package nl.dykam.dev.reutil.commands.parsing;

import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import org.bukkit.entity.Player;

public class ArgumentParserList {
    private Node first;

    public ExecuteResult parse(CommandExecuteContext context) {
        return first.execute(context, 0);
    }

    public static ArgumentParserList fromParsedMethod(ParsedMethod parsedMethod) {
        ArgumentParserList list = new ArgumentParserList();
        ParsedMethodParam[] params = parsedMethod.getParams();
        Node previous = list.first = new Node(params[0], 0);
        for (int i = 1; i < params.length; i++) {
            ParsedMethodParam parsedMethodParam = params[i];
            Node current = new Node(parsedMethodParam, i);
            previous.next = current;
            previous = current;
        }
        return list;
    }

    static class Node {
        public Node next;
        public ParsedMethodParam param;
        public int index;

        Node(ParsedMethodParam param, int index) {
            this.param = param;
            this.index = index;
        }

        /**
         * Try to applies the parser to the current argument. If optional, try working match first before skipping.
         * @param context
         * @param i
         */
        public ExecuteResult execute(CommandExecuteContext context, int i) {
            ExecuteResult success = apply(context, context.getArguments()[i]);
            if (success.isSuccess() && (success = proceed(context, i + 1)).isSuccess()) return success;
            if (param.isSender() && applySender(context) && (success = proceed(context, i)).isSuccess()) return success;
            if (param.isOptional() && (success = proceed(context, i)).isSuccess()) return success;
            return success;
        }

        private boolean applySender(CommandExecuteContext context) {
            if(!(context.getSender() instanceof Player))
                return false;
            Player player = (Player) context.getSender();
            context.getResult().set(index, player);
            context.setTarget(player);
            return true;
        }

        protected ExecuteResult apply(CommandExecuteContext context, String argument) {
            ParseResult<?> parseResult = param.getParser().parse(context, argument);
            if(parseResult.isSuccess()) {
                Object value = parseResult.getValue();
                context.getResult().set(index, value);
                if(param.isSender() & value instanceof Player)
                    context.setTarget((Player) value);;
            }
            return parseResult;
        }

        protected ExecuteResult proceed(CommandExecuteContext context, int i) {
            if(next == null) {
                // Reached the on both the arguments and the parser chain
                if (context.getArguments().length == i) return ExecuteResult.esuccess();
                else return ExecuteResult.efailure("Too many arguments");
            }
            // Not enough arguments
            if(context.getArguments().length <= i)
                return ExecuteResult.efailure("Not enough arguments");
            return next.execute(context, i);
        }
    }
}
