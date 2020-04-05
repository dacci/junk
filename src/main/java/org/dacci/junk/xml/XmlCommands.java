package org.dacci.junk.xml;

import org.dacci.junk.Main.CommandGroup;
import picocli.CommandLine.Command;

@Command(
    name = "xml",
    subcommands = {FormatPom.class})
public class XmlCommands implements CommandGroup {}
