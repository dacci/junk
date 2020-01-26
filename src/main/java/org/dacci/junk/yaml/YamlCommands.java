package org.dacci.junk.yaml;

import org.dacci.junk.Main.CommandGroup;
import picocli.CommandLine.Command;

@Command(
    name = "yaml",
    subcommands = {FromJson.class})
public class YamlCommands implements CommandGroup {}
