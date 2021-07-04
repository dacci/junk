package org.dacci.junk.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import org.dacci.junk.util.cfn.Base64;
import org.dacci.junk.util.cfn.Cidr;
import org.dacci.junk.util.cfn.Condition;
import org.dacci.junk.util.cfn.Conditional;
import org.dacci.junk.util.cfn.FindInMap;
import org.dacci.junk.util.cfn.GetAZs;
import org.dacci.junk.util.cfn.GetAtt;
import org.dacci.junk.util.cfn.ImportValue;
import org.dacci.junk.util.cfn.Join;
import org.dacci.junk.util.cfn.Ref;
import org.dacci.junk.util.cfn.Select;
import org.dacci.junk.util.cfn.Split;
import org.dacci.junk.util.cfn.Sub;
import org.dacci.junk.util.cfn.Transform;

@SuppressWarnings("serial")
public class CloudFormationModule extends SimpleModule {
  private static class Base64Serializer extends StdSerializer<Base64> {
    public Base64Serializer() {
      this(null);
    }

    public Base64Serializer(Class<Base64> t) {
      super(t);
    }

    @Override
    public void serialize(Base64 value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeStartObject();
      gen.writeObjectField("Fn::Base64", value.getValue());
      gen.writeEndObject();
    }
  }

  private static class CidrSerializer extends StdSerializer<Cidr> {
    public CidrSerializer() {
      this(null);
    }

    public CidrSerializer(Class<Cidr> t) {
      super(t);
    }

    @Override
    public void serialize(Cidr value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeStartObject();
      gen.writeArrayFieldStart("Fn::Cidr");
      gen.writeObject(value.getIpBlock());
      gen.writeNumber(value.getCount());
      gen.writeNumber(value.getCidrBits());
      gen.writeEndArray();
      gen.writeEndObject();
    }
  }

  private static class ConditionalSerializer extends StdSerializer<Conditional> {
    public ConditionalSerializer() {
      this(null);
    }

    public ConditionalSerializer(Class<Conditional> t) {
      super(t);
    }

    @Override
    public void serialize(Conditional value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeStartObject();
      gen.writeArrayFieldStart("Fn::" + value.getClass().getSimpleName());

      for (var condition : value.getConditions()) {
        gen.writeObject(condition);
      }

      gen.writeEndArray();
      gen.writeEndObject();
    }
  }

  private static class FindInMapSerializer extends StdSerializer<FindInMap> {
    public FindInMapSerializer() {
      this(null);
    }

    public FindInMapSerializer(Class<FindInMap> t) {
      super(t);
    }

    @Override
    public void serialize(FindInMap value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeStartObject();
      gen.writeArrayFieldStart("Fn::FindInMap");
      gen.writeObject(value.getMapName());
      gen.writeObject(value.getTopLevelKey());
      gen.writeObject(value.getSecondLevelKey());
      gen.writeEndArray();
      gen.writeEndObject();
    }
  }

  private static class GetAttSerializer extends StdSerializer<GetAtt> {
    public GetAttSerializer() {
      this(null);
    }

    public GetAttSerializer(Class<GetAtt> t) {
      super(t);
    }

    @Override
    public void serialize(GetAtt value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeStartObject();
      gen.writeArrayFieldStart("Fn::GetAtt");
      gen.writeObject(value.getResource());
      gen.writeObject(value.getAttribute());
      gen.writeEndArray();
      gen.writeEndObject();
    }
  }

  private static class GetAZsSerializer extends StdSerializer<GetAZs> {
    public GetAZsSerializer() {
      this(null);
    }

    public GetAZsSerializer(Class<GetAZs> t) {
      super(t);
    }

    @Override
    public void serialize(GetAZs value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeStartObject();
      gen.writeObjectField("Fn::GetAZs", value.getRegion());
      gen.writeEndObject();
    }
  }

  private static class ImportValueSerializer extends StdSerializer<ImportValue> {
    public ImportValueSerializer() {
      this(null);
    }

    public ImportValueSerializer(Class<ImportValue> t) {
      super(t);
    }

    @Override
    public void serialize(ImportValue value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeStartObject();
      gen.writeObjectField("Fn::ImportValue", value.getReference());
      gen.writeEndObject();
    }
  }

  private static class JoinSerializer extends StdSerializer<Join> {
    public JoinSerializer() {
      this(null);
    }

    public JoinSerializer(Class<Join> t) {
      super(t);
    }

    @Override
    public void serialize(Join value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeStartObject();
      gen.writeArrayFieldStart("Fn::Join");
      gen.writeObject(value.getDelimiter());
      gen.writeObject(value.getValues());
      gen.writeEndArray();
      gen.writeEndObject();
    }
  }

  private static class SelectSerializer extends StdSerializer<Select> {
    public SelectSerializer() {
      this(null);
    }

    public SelectSerializer(Class<Select> t) {
      super(t);
    }

    @Override
    public void serialize(Select value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeStartObject();
      gen.writeArrayFieldStart("Fn::Select");
      gen.writeNumber(value.getIndex());
      gen.writeObject(value.getSelection());
      gen.writeEndArray();
      gen.writeEndObject();
    }
  }

  private static class SplitSerializer extends StdSerializer<Split> {
    public SplitSerializer() {
      this(null);
    }

    public SplitSerializer(Class<Split> t) {
      super(t);
    }

    @Override
    public void serialize(Split value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeStartObject();
      gen.writeArrayFieldStart("Fn::Split");
      gen.writeObject(value.getDelimiter());
      gen.writeObject(value.getSource());
      gen.writeEndArray();
      gen.writeEndObject();
    }
  }

  private static class SubSerializer extends StdSerializer<Sub> {
    public SubSerializer() {
      this(null);
    }

    public SubSerializer(Class<Sub> t) {
      super(t);
    }

    @Override
    public void serialize(Sub value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      var hasVariables = value.getVariables() != null && !value.getVariables().isEmpty();

      gen.writeStartObject();
      gen.writeFieldName("Fn::Sub");

      if (hasVariables) gen.writeStartArray();

      gen.writeObject(value.getTemplate());

      if (hasVariables) {
        gen.writeObject(value.getVariables());
        gen.writeEndArray();
      }

      gen.writeEndObject();
    }
  }

  private static class TransformSerializer extends StdSerializer<Transform> {
    public TransformSerializer() {
      this(null);
    }

    public TransformSerializer(Class<Transform> t) {
      super(t);
    }

    @Override
    public void serialize(Transform value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeStartObject();
      gen.writeObjectFieldStart("Fn::Transform");
      gen.writeObjectField("Name", value.getName());
      gen.writeObjectField("Parameters", value.getParameters());
      gen.writeEndObject();
      gen.writeEndObject();
    }
  }

  private static class RefSerializer extends StdSerializer<Ref> {
    public RefSerializer() {
      this(null);
    }

    public RefSerializer(Class<Ref> t) {
      super(t);
    }

    @Override
    public void serialize(Ref value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeStartObject();
      gen.writeObjectField("Ref", value.getReference());
      gen.writeEndObject();
    }
  }

  private static class ConditionSerializer extends StdSerializer<Condition> {
    public ConditionSerializer() {
      this(null);
    }

    public ConditionSerializer(Class<Condition> t) {
      super(t);
    }

    @Override
    public void serialize(Condition value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeStartObject();
      gen.writeObjectField("Condition", value.getCondition());
      gen.writeEndObject();
    }
  }

  private static class Holder {
    public static final CloudFormationModule INSTANCE = new CloudFormationModule();
  }

  public static CloudFormationModule getInstance() {
    return Holder.INSTANCE;
  }

  private CloudFormationModule() {
    addSerializer(Base64.class, new Base64Serializer());
    addSerializer(Cidr.class, new CidrSerializer());
    addSerializer(Conditional.class, new ConditionalSerializer());
    addSerializer(FindInMap.class, new FindInMapSerializer());
    addSerializer(GetAtt.class, new GetAttSerializer());
    addSerializer(GetAZs.class, new GetAZsSerializer());
    addSerializer(ImportValue.class, new ImportValueSerializer());
    addSerializer(Join.class, new JoinSerializer());
    addSerializer(Select.class, new SelectSerializer());
    addSerializer(Split.class, new SplitSerializer());
    addSerializer(Sub.class, new SubSerializer());
    addSerializer(Transform.class, new TransformSerializer());
    addSerializer(Ref.class, new RefSerializer());
    addSerializer(Condition.class, new ConditionSerializer());
  }
}
