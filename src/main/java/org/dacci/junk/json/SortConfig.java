package org.dacci.junk.json;

import static java.nio.charset.StandardCharsets.*;
import static java.nio.file.StandardOpenOption.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.dacci.junk.util.JsonStringifyPrettyPrinter;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "sort-config")
@Slf4j
public class SortConfig implements Runnable {
  private static final ObjectMapper JSON =
      new ObjectMapper()
          .setDefaultPrettyPrinter(JsonStringifyPrettyPrinter.builder().indent("    ").build())
          .enable(SerializationFeature.INDENT_OUTPUT);
  private static TypeReference<Map<String, Object>> CONFIG_TYPE =
      new TypeReference<Map<String, Object>>() {};

  private static int keyComparator(String a, String b) {
    if (a == null && b == null) return 0;
    if (a == null) return -1;
    if (b == null) return 1;

    var aa = a.charAt(0) == '[';
    var bb = b.charAt(0) == '[';
    return aa == bb ? a.compareTo(b) : aa ? 1 : -1;
  }

  private static void process(Path file) {
    Map<String, Object> loaded;
    try (var reader = Files.newBufferedReader(file)) {
      loaded = JSON.readValue(reader, CONFIG_TYPE);
    } catch (IOException e) {
      log.error("Failed to load", e);
      return;
    }

    var sorted = new TreeMap<>(SortConfig::keyComparator);
    sorted.putAll(loaded);

    try (var writer = Files.newBufferedWriter(file, UTF_8, CREATE, TRUNCATE_EXISTING)) {
      JSON.writeValue(writer, sorted);
    } catch (IOException e) {
      log.error("Failed to save", e);
      return;
    }
  }

  @Parameters(arity = "1..")
  private List<Path> files;

  @Override
  public void run() {
    files.parallelStream().forEach(SortConfig::process);
  }
}
