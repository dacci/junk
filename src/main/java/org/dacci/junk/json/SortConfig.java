package org.dacci.junk.json;

import static java.nio.charset.StandardCharsets.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.dacci.junk.util.JsonStringifyPrettyPrinter;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "sort-config")
@Slf4j
public class SortConfig implements Runnable {
  public static class Config extends TreeMap<String, Object> {
    public Config() {
      super(SortConfig::keyComparator);
    }
  }

  private static final ObjectMapper JSON =
      new ObjectMapper()
          .setDefaultPrettyPrinter(
              JsonStringifyPrettyPrinter.builder().lineSeparator("\n").indent("    ").build())
          .enable(SerializationFeature.INDENT_OUTPUT)
          .registerModule(new SimpleModule().addAbstractTypeMapping(Map.class, TreeMap.class));

  private static int keyComparator(String a, String b) {
    if (a == b) return 0;
    if (a == null) return -1;
    if (b == null) return 1;

    var aa = a.charAt(0) == '[';
    var bb = b.charAt(0) == '[';
    return aa == bb ? a.compareTo(b) : aa ? 1 : -1;
  }

  private static void process(Path file) {
    Config config;
    try (var reader = Files.newBufferedReader(file, UTF_8)) {
      config = JSON.readValue(reader, Config.class);
    } catch (IOException e) {
      log.error("Failed to load", e);
      return;
    }

    try (var writer = Files.newBufferedWriter(file, UTF_8)) {
      JSON.writeValue(writer, config);
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
