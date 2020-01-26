package org.dacci.junk;

import java.util.ArrayDeque;
import org.dacci.junk.json.JsonCommands;
import org.dacci.junk.yaml.YamlCommands;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "junk",
    subcommands = {JsonCommands.class, YamlCommands.class})
public class Main implements Runnable {

  public interface CommandGroup extends Runnable {
    @Override
    default void run() {
      var queue = new ArrayDeque<>(COMMAND_LINE.getSubcommands().values());
      while (!queue.isEmpty()) {
        var subcommand = queue.removeFirst();
        if (subcommand.getCommand() == this) {
          subcommand.usage(System.out);
          return;
        }

        queue.addAll(subcommand.getSubcommands().values());
      }

      throw new UnsupportedOperationException();
    }
  }

  private static final CommandLine COMMAND_LINE = new CommandLine(new Main());

  public static void main(String... args) {
    System.exit(COMMAND_LINE.execute(args));
  }

  @Override
  public void run() {
    COMMAND_LINE.usage(System.out);
  }
}
