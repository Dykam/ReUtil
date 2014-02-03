package nl.dykam.dev.reutil.commands.parsing;

import java.util.EnumMap;
import java.util.List;

public class ParserTree {

    class Node {
        EnumMap<Rank, List<Node>> children;
    }

    private enum Rank {
    }
}
