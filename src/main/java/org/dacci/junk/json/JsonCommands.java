package org.dacci.junk.json;

import org.dacci.junk.Main.CommandGroup;
import picocli.CommandLine.Command;

@Command(
    name = "json",
    subcommands = {SortConfig.class, FromYaml.class})
public class JsonCommands implements CommandGroup {}
