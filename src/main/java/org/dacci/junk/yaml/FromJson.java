package org.dacci.junk.yaml;

import static java.nio.charset.StandardCharsets.*;
import static java.nio.file.StandardOpenOption.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "from-json")
@Slf4j
public class FromJson implements Runnable {
  private static final ObjectMapper JSON = new ObjectMapper();
  private static final Yaml YAML;

  static {
    var options = new DumperOptions();
    options.setWidth(Integer.MAX_VALUE);
    options.setDefaultFlowStyle(FlowStyle.BLOCK);

    YAML = new Yaml(options);
  }

  private static void processFile(Path file) {
    Object value;
    try (var reader = Files.newBufferedReader(file, UTF_8)) {
      value = JSON.readValue(reader, Object.class);
    } catch (Exception e) {
      log.error("Failed to load", e);
      return;
    }

    var yamlName = FilenameUtils.removeExtension(file.getFileName().toString()) + ".yaml";

    try (var writer =
        Files.newBufferedWriter(file.resolveSibling(yamlName), UTF_8, CREATE, TRUNCATE_EXISTING)) {
      YAML.dump(value, writer);
    } catch (Exception e) {
      log.error("Failed to save", e);
      return;
    }
  }

  @Parameters(arity = "1..")
  private List<Path> files;

  @Override
  public void run() {
    files.parallelStream().forEach(FromJson::processFile);
  }
}
