package org.dacci.junk.json;

import static java.nio.charset.StandardCharsets.*;
import static java.nio.file.StandardOpenOption.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.dacci.junk.util.JsonStringifyPrettyPrinter;
import org.yaml.snakeyaml.Yaml;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "from-yaml")
@Slf4j
public class FromYaml implements Runnable {
  private static final Yaml YAML = new Yaml();
  private static final ObjectMapper JSON =
      new ObjectMapper()
          .setDefaultPrettyPrinter(JsonStringifyPrettyPrinter.builder().build())
          .enable(SerializationFeature.INDENT_OUTPUT);

  private static void processFile(Path file) {
    Object value;
    try (var reader = Files.newBufferedReader(file, UTF_8)) {
      value = YAML.load(reader);
    } catch (Exception e) {
      log.error("Failed to load", e);
      return;
    }

    var jsonName = FilenameUtils.removeExtension(file.getFileName().toString()) + ".json";

    try (var writer =
        Files.newBufferedWriter(file.resolveSibling(jsonName), UTF_8, CREATE, TRUNCATE_EXISTING)) {
      JSON.writeValue(writer, value);
    } catch (Exception e) {
      log.error("Failed to save", e);
      return;
    }
  }

  @Parameters(arity = "1..")
  private List<Path> files;

  @Override
  public void run() {
    files.parallelStream().forEach(FromYaml::processFile);
  }
}
