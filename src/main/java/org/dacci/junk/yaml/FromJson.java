package org.dacci.junk.yaml;

import static java.nio.charset.StandardCharsets.*;
import static java.nio.file.StandardOpenOption.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.dacci.junk.util.CloudFormationYaml;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "from-json")
@Slf4j
public class FromJson implements Runnable {
  private static final ObjectMapper JSON = new ObjectMapper();
  private static final DumperOptions DUMPER_OPTIONS;

  static {
    DUMPER_OPTIONS = new DumperOptions();
    DUMPER_OPTIONS.setWidth(Integer.MAX_VALUE);
    DUMPER_OPTIONS.setDefaultFlowStyle(FlowStyle.BLOCK);
  }

  @Option(names = "--cfn")
  private boolean cfn;

  @Parameters(arity = "1..")
  private List<Path> files;

  private Yaml yaml;

  private void processFile(Path file) {
    Object value;
    try (var reader = Files.newBufferedReader(file, UTF_8)) {
      value = JSON.readValue(reader, Object.class);
    } catch (Exception e) {
      log.error("Failed to load", e);
      return;
    }

    if (cfn) {
      var dumped = yaml.dump(value);
      value = yaml.load(dumped);
    }

    var yamlName = FilenameUtils.removeExtension(file.getFileName().toString()) + ".yaml";

    try (var writer =
        Files.newBufferedWriter(file.resolveSibling(yamlName), UTF_8, CREATE, TRUNCATE_EXISTING)) {
      yaml.dump(value, writer);
    } catch (Exception e) {
      log.error("Failed to save", e);
      return;
    }
  }

  @Override
  public void run() {
    yaml = cfn ? new CloudFormationYaml(DUMPER_OPTIONS) : new Yaml(DUMPER_OPTIONS);

    files.parallelStream().forEach(this::processFile);
  }
}
