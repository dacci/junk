package org.dacci.junk.json;

import static java.nio.charset.StandardCharsets.*;
import static java.nio.file.StandardOpenOption.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.dacci.junk.util.CloudFormationModule;
import org.dacci.junk.util.CloudFormationYaml;
import org.dacci.junk.util.JsonStringifyPrettyPrinter;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "from-yaml")
@Slf4j
public class FromYaml implements Runnable {
  private static final ObjectMapper JSON =
      new ObjectMapper()
          .setDefaultPrettyPrinter(JsonStringifyPrettyPrinter.builder().build())
          .enable(SerializationFeature.INDENT_OUTPUT);

  @Option(names = "--cfn")
  private boolean cfn;

  @Parameters(arity = "1..")
  private List<Path> files;

  private Yaml yaml;

  private void processFile(Path file) {
    Object value;
    try (var reader = Files.newBufferedReader(file, UTF_8)) {
      value = yaml.load(reader);
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

  @Override
  public void run() {
    yaml = cfn ? new CloudFormationYaml() : new Yaml();
    if (cfn) JSON.registerModule(CloudFormationModule.getInstance());

    files.parallelStream().forEach(this::processFile);
  }
}
