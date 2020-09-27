package org.dacci.junk.yaml;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

@Command(name = "from-json")
@Slf4j
public class FromJson implements Runnable {
  private static final Path HYPHEN_PATH = Paths.get("-");
  private static final ObjectMapper JSON = new ObjectMapper();

  @ParentCommand private YamlCommands parent;

  @Option(names = "--charset")
  private Charset charset = StandardCharsets.UTF_8;

  @Parameters private List<Path> files = new ArrayList<>();

  private Yaml yaml;

  private Reader openForRead(Path file) throws IOException {
    if (file.equals(HYPHEN_PATH)) {
      return new BufferedReader(new InputStreamReader(System.in, charset));
    } else {
      return Files.newBufferedReader(file, charset);
    }
  }

  private Writer openForWrite(Path file) throws IOException {
    if (file.equals(HYPHEN_PATH)) {
      return new BufferedWriter(new OutputStreamWriter(System.out, charset));
    } else {
      var yamlName = FilenameUtils.removeExtension(file.getFileName().toString()) + ".yaml";
      return Files.newBufferedWriter(
          file.resolveSibling(yamlName), charset, CREATE, TRUNCATE_EXISTING);
    }
  }

  private void processFile(Path file) {
    Object value;
    try (var reader = openForRead(file)) {
      value = JSON.readValue(reader, Object.class);
    } catch (Exception e) {
      log.error("Failed to load", e);
      return;
    }

    if (parent.isCloudFormation()) value = yaml.load(yaml.dump(value));

    try (var writer = openForWrite(file)) {
      yaml.dump(value, writer);
    } catch (Exception e) {
      log.error("Failed to save", e);
      return;
    }
  }

  @Override
  public void run() {
    yaml = parent.createYaml();

    if (files.isEmpty()) files.add(HYPHEN_PATH);

    files.forEach(this::processFile);
  }
}
