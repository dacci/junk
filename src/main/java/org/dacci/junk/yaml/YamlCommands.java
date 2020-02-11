package org.dacci.junk.yaml;

import java.util.TimeZone;
import lombok.Data;
import org.dacci.junk.Main.CommandGroup;
import org.dacci.junk.util.CloudFormationYaml;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.LineBreak;
import org.yaml.snakeyaml.DumperOptions.NonPrintableStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;
import org.yaml.snakeyaml.DumperOptions.Version;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "yaml",
    subcommands = {FromJson.class})
@Data
public class YamlCommands implements CommandGroup {
  @Option(names = "--disallow-unicode", description = "force ASCII characters only")
  private boolean allowUnicode = true;

  @Option(names = "--scalar-style", description = "default style for scalars")
  private ScalarStyle scalarStyle = ScalarStyle.PLAIN;

  @Option(names = "--indent", description = "indent width")
  private int indent = 2;

  @Option(names = "--indicator-indent", description = "indicator indent width")
  private int indicatorIndent = 0;

  @Option(names = "--version", description = "specification version")
  private Version version = null;

  @Option(names = "--canonical", description = "produce canonical YAML document")
  private boolean canonical = false;

  @Option(names = "--pretty-flow", description = "produce pretty flow YAML document")
  private boolean prettyFlow = false;

  @Option(names = "--width", description = "preferred width to emit scalars")
  private int width = 80;

  @Option(names = "--no-split-lines", description = "do not split lines exceeding preferred width")
  private boolean splitLines = true;

  @Option(names = "--flow-style", description = "the default flow style")
  private FlowStyle flowStyle = FlowStyle.AUTO;

  @Option(names = "--line-break", description = "line break to separate the lines")
  private LineBreak lineBreak = LineBreak.UNIX;

  @Option(names = "--explicit-start")
  private boolean explicitStart = false;

  @Option(names = "--explicit-end")
  private boolean explicitEnd = false;

  @Option(names = "--time-zone")
  private TimeZone timeZone = null;

  @Option(
      names = "--max-simple-key-length",
      description = "max key length to use simple key (without '?')")
  private int maxSimpleKeyLength = 128;

  @Option(names = "--non-printable-style")
  private NonPrintableStyle nonPrintableStyle = NonPrintableStyle.BINARY;

  @Option(names = "--cfn", description = "handle CloudFormation intrinsics")
  private boolean cloudFormation = false;

  public Yaml createYaml() {
    var dumperOptions = new DumperOptions();
    dumperOptions.setAllowUnicode(allowUnicode);
    dumperOptions.setDefaultScalarStyle(scalarStyle);
    dumperOptions.setIndent(indent);
    dumperOptions.setIndicatorIndent(indicatorIndent);
    dumperOptions.setVersion(version);
    dumperOptions.setCanonical(canonical);
    dumperOptions.setPrettyFlow(prettyFlow);
    dumperOptions.setWidth(width < 0 ? Integer.MAX_VALUE : width);
    dumperOptions.setSplitLines(splitLines);
    dumperOptions.setDefaultFlowStyle(flowStyle);
    dumperOptions.setLineBreak(lineBreak);
    dumperOptions.setExplicitStart(explicitStart);
    dumperOptions.setExplicitEnd(explicitEnd);
    dumperOptions.setTimeZone(timeZone);
    dumperOptions.setMaxSimpleKeyLength(maxSimpleKeyLength);
    dumperOptions.setNonPrintableStyle(nonPrintableStyle);

    return cloudFormation ? new CloudFormationYaml(dumperOptions) : new Yaml(dumperOptions);
  }
}
