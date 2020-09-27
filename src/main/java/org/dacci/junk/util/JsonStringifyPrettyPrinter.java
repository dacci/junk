package org.dacci.junk.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.Instantiatable;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JsonStringifyPrettyPrinter
    implements Instantiatable<JsonStringifyPrettyPrinter>, PrettyPrinter {

  public static class Builder {
    private String lineSeparator;
    private String indent;

    private Builder() {
      lineSeparator = System.lineSeparator();
      indent = "  ";
    }

    public Builder lineSeparator(String lineSeparator) {
      this.lineSeparator = lineSeparator;
      return this;
    }

    public Builder indent(String indent) {
      this.indent = indent;
      return this;
    }

    public JsonStringifyPrettyPrinter build() {
      return new JsonStringifyPrettyPrinter(lineSeparator, indent);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final String lineSeparator;
  private final String indent;

  private int level = 0;

  public JsonStringifyPrettyPrinter(JsonStringifyPrettyPrinter base) {
    lineSeparator = base.lineSeparator;
    indent = base.indent;
  }

  @Override
  public JsonStringifyPrettyPrinter createInstance() {
    return new JsonStringifyPrettyPrinter(this);
  }

  private static void writeIndent(JsonGenerator gen, int level, String content) throws IOException {
    for (int i = 0; i < level; ++i) gen.writeRaw(content);
  }

  @Override
  public void writeRootValueSeparator(JsonGenerator gen) throws IOException {}

  @Override
  public void writeStartObject(JsonGenerator gen) throws IOException {
    gen.writeRaw("{");
    gen.writeRaw(lineSeparator);
    writeIndent(gen, ++level, indent);
  }

  @Override
  public void writeEndObject(JsonGenerator gen, int nrOfEntries) throws IOException {
    gen.writeRaw(lineSeparator);
    writeIndent(gen, --level, indent);
    gen.writeRaw("}");
    if (level == 0) gen.writeRaw(lineSeparator);
  }

  @Override
  public void writeObjectEntrySeparator(JsonGenerator gen) throws IOException {
    gen.writeRaw(",");
    gen.writeRaw(lineSeparator);
    writeIndent(gen, level, indent);
  }

  @Override
  public void writeObjectFieldValueSeparator(JsonGenerator gen) throws IOException {
    gen.writeRaw(": ");
  }

  @Override
  public void writeStartArray(JsonGenerator gen) throws IOException {
    gen.writeRaw("[");
    gen.writeRaw(lineSeparator);
    writeIndent(gen, ++level, indent);
  }

  @Override
  public void writeEndArray(JsonGenerator gen, int nrOfValues) throws IOException {
    gen.writeRaw(lineSeparator);
    writeIndent(gen, --level, indent);
    gen.writeRaw("]");
  }

  @Override
  public void writeArrayValueSeparator(JsonGenerator gen) throws IOException {
    gen.writeRaw(",");
    gen.writeRaw(lineSeparator);
    writeIndent(gen, level, indent);
  }

  @Override
  public void beforeArrayValues(JsonGenerator gen) throws IOException {}

  @Override
  public void beforeObjectEntries(JsonGenerator gen) throws IOException {}
}
