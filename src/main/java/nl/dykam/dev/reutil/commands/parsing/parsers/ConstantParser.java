package nl.dykam.dev.reutil.commands.parsing.parsers;

import nl.dykam.dev.reutil.commands.CommandExecuteContext;
import nl.dykam.dev.reutil.commands.CommandTabContext;
import nl.dykam.dev.reutil.commands.parsing.ArgumentParser;
import nl.dykam.dev.reutil.commands.parsing.ArgumentParserTree;
import nl.dykam.dev.reutil.commands.parsing.ParseResult;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Constaints the input to a set amount of strings. Leaves the actual parsing to a more specialized parser.
 * @param <T>
 */
public class ConstantParser<T> extends ArgumentParser<T> {
    private final Set<String> defaults;
    private final ArgumentParser<T> superParser;

    protected ConstantParser(Set<String> defaults, ArgumentParser<T> superParser) {
        super(determineDefaultName(defaults, superParser), superParser.requiresTarget(), defaults.size() == 1 ? ArgumentParserTree.Type.CONSTANT : ArgumentParserTree.Type.FIXED_CHOICE);
        this.defaults = defaults;
        this.superParser = superParser;
    }

    /**
     * Determine what to use as the default name/hint
     * @param defaults The defaults given
     * @param superParser The super parser
     * @return The join of the defaults if the final length is less than 32, otherwise the name of the superparser.
     */
    private static String determineDefaultName(Set<String> defaults, ArgumentParser<?> superParser) {
        String joined = StringUtils.join(defaults, "|");
        return joined.length() < 32 ? joined : superParser.getDefaultName();
    }

    @Override
    public ParseResult<T> parse(CommandExecuteContext context, String argument, String name) {
        if(!defaults.contains(argument.toLowerCase()))
            return ParseResult.failure(argument + " not recognized as valid value");
        return superParser.parse(context, argument, name);
    }

    @Override
    public List<String> complete(CommandTabContext context, String current) {
        List<String> completions = new ArrayList<>();
        for (String aDefault : defaults) {
            String lowerCase = current.toLowerCase();
            if(aDefault.startsWith(lowerCase))
                completions.add(aDefault);
        }
        return completions;
    }
}
