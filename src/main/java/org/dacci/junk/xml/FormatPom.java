package org.dacci.junk.xml;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "format-pom")
@Slf4j
public class FormatPom implements Runnable {
  private static final Path HYPHEN_PATH = Paths.get("-");

  @Parameters private List<Path> files = new ArrayList<>();

  private MavenXpp3Reader reader;
  private MavenXpp3Writer writer;

  private InputStream openForRead(Path file) throws IOException {
    if (file.equals(HYPHEN_PATH)) {
      return System.in;
    } else {
      return Files.newInputStream(file);
    }
  }

  private OutputStream openForWrite(Path file) throws IOException {
    if (file.equals(HYPHEN_PATH)) {
      return System.out;
    } else {
      return Files.newOutputStream(file, CREATE, TRUNCATE_EXISTING);
    }
  }

  private void processFile(Path file) {
    Model model;
    try (var in = openForRead(file)) {
      model = reader.read(in);
    } catch (IOException | XmlPullParserException e) {
      log.error("Failed to load", e);
      return;
    }

    try (var out = openForWrite(file)) {
      writer.write(out, model);
    } catch (IOException e) {
      log.error("Failed to save", e);
      return;
    }
  }

  @Override
  public void run() {
    if (reader == null) reader = new MavenXpp3Reader();
    if (writer == null) writer = new MavenXpp3Writer();

    if (files.isEmpty()) files.add(HYPHEN_PATH);
    files.forEach(this::processFile);
  }
}
