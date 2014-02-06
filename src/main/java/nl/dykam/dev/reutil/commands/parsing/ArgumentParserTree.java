package nl.dykam.dev.reutil.commands.parsing;

import nl.dykam.dev.reutil.commands.CommandExecuteContext;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ArgumentParserTree {
    private Map<Type, Map<Node, Node>> children;

    public ArgumentParserTree() {
        children = new EnumMap<>(Type.class);
    }

    public Node add(Type type, ParsedMethodParam param) {
        Map<Node, Node> nodesForType = getNodesForType(type);
        Node e = new Node(param);
        if(nodesForType.containsKey(e))
            return nodesForType.get(e);
        nodesForType.put(e, e);
        return e;
    }

    private Map<Node, Node> getNodesForType(Type type) {
        if(!children.containsKey(type))
            children.put(type, new HashMap<Node, Node>());
        return children.get(type);
    }

    public TreeParseResult parse(CommandExecuteContext context) {
        return getNextParseResult(context, 0);
    }

    private TreeParseResult getNextParseResult(CommandExecuteContext context, int depth) {
        TreeParseResult deepestNextParseResult = null;
        for (Map<Node, Node> nodes : children.values()) {
            for (Node node : nodes.values()) {
                TreeParseResult nextParseResult = node.parse(context, depth + 1);
                if (!nextParseResult.isFailure()) {
                    return nextParseResult;
                }
                // Make sure the deepest failure gets shown to the user
                if (deepestNextParseResult == null || deepestNextParseResult.depth < nextParseResult.depth)
                    deepestNextParseResult = nextParseResult;
            }
        }
        return deepestNextParseResult;
    }

    class Node extends ArgumentParserTree {
        private ArgumentParser<?> parser;
        private ParsedMethodParam param;

        public Node(ParsedMethodParam param) {
            super();
            this.parser = param.getParser();
        }

        public ArgumentParser<?> getParser() {
            return parser;
        }

        public TreeParseResult parse(CommandExecuteContext context, int depth) {
            if(context.getArguments().length <= depth)
                return new TreeParseResult(ParseResult.failure("Not enough arguments"), null, depth);

            ParseResult<?> parseResult = parser.parse(context, context.getArgument(depth), "");
            if (parseResult.isFailure())
                return new TreeParseResult(parseResult, null, depth);
            TreeParseResult nextParseResult = getNextParseResult(context, depth);
            if(nextParseResult != null)
                return new TreeParseResult(parseResult, nextParseResult, depth);
            return new TreeParseResult(ParseResult.failure("Too many arguments"), null, depth);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Node node = (Node) o;

            if (!param.equals(node.param)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return param.hashCode();
        }
    }

    public enum Type {
        CONSTANT,
        FIXED_CHOICE,
        DYNAMIC_CHOICE,
        LIMITED_SYNTAX,
        FREE_SYNTAX,
    }

    static class TreeParseResult {
        private final ParseResult<?> parseResult;
        public TreeParseResult next;
        private boolean failure;
        public int depth;

        public TreeParseResult(ParseResult<?> parseResult, TreeParseResult nextParseResult, int depth) {
            next = nextParseResult;
            this.parseResult = parseResult;
            this.depth = depth;
        }

        public boolean isFailure() {
            return parseResult == null || parseResult.isFailure();
        }

        public boolean isSuccess() {
            return !isFailure();
        }

        public ParseResult<?> getParseResult() {
            return parseResult;
        }
    }
}
