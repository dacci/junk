package org.dacci.junk.util;

import static org.dacci.junk.util.CloudFormationYaml.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import org.dacci.junk.util.cfn.And;
import org.dacci.junk.util.cfn.Base64;
import org.dacci.junk.util.cfn.Cidr;
import org.dacci.junk.util.cfn.Conditional;
import org.dacci.junk.util.cfn.Equals;
import org.dacci.junk.util.cfn.FindInMap;
import org.dacci.junk.util.cfn.GetAZs;
import org.dacci.junk.util.cfn.GetAtt;
import org.dacci.junk.util.cfn.If;
import org.dacci.junk.util.cfn.ImportValue;
import org.dacci.junk.util.cfn.Join;
import org.dacci.junk.util.cfn.Not;
import org.dacci.junk.util.cfn.Or;
import org.dacci.junk.util.cfn.Ref;
import org.dacci.junk.util.cfn.Select;
import org.dacci.junk.util.cfn.Split;
import org.dacci.junk.util.cfn.Sub;
import org.dacci.junk.util.cfn.Transform;

public class CloudFormationRepresenter extends Representer {
  private final DumperOptions dumperOptions;

  @FunctionalInterface
  public interface Represent extends org.yaml.snakeyaml.representer.Represent {}

  public CloudFormationRepresenter() {
    this(new DumperOptions());
  }

  public CloudFormationRepresenter(DumperOptions dumperOptions) {
    super(dumperOptions);

    this.dumperOptions = dumperOptions;

    representers.put(Base64.class, (Represent) this::representBase64);
    representers.put(Cidr.class, (Represent) this::representCidr);
    representers.put(And.class, (Represent) this::representAnd);
    representers.put(Equals.class, (Represent) this::representEquals);
    representers.put(If.class, (Represent) this::representIf);
    representers.put(Not.class, (Represent) this::representNot);
    representers.put(Or.class, (Represent) this::representOr);
    representers.put(FindInMap.class, (Represent) this::representFindInMap);
    representers.put(GetAtt.class, (Represent) this::representGetAtt);
    representers.put(GetAZs.class, (Represent) this::representGetAZs);
    representers.put(ImportValue.class, (Represent) this::representImportValue);
    representers.put(Join.class, (Represent) this::representJoin);
    representers.put(Select.class, (Represent) this::representSelect);
    representers.put(Split.class, (Represent) this::representSplit);
    representers.put(Sub.class, (Represent) this::representSub);
    representers.put(Transform.class, (Represent) this::representTransform);
    representers.put(Ref.class, (Represent) this::representRef);
  }

  private boolean isScalar(Object value) {
    return value instanceof Number || value instanceof CharSequence;
  }

  private Node representBase64(Object data) {
    var value = ((Base64) data).getValue();
    if (isScalar(value)) {
      return representScalar(BASE64, value.toString(), dumperOptions.getDefaultScalarStyle());
    } else {
      var mapping = new HashMap<>();
      mapping.put("Fn::Base64", value);
      return represent(mapping);
    }
  }

  private Node representCidr(Object data) {
    var cidr = (Cidr) data;

    var sequence = new ArrayList<>();
    sequence.add(cidr.getIpBlock());
    sequence.add(cidr.getCount());
    sequence.add(cidr.getCidrBits());

    return representSequence(CIDR, sequence, dumperOptions.getDefaultFlowStyle());
  }

  private Node representConditional(Tag tag, Conditional conditional) {
    return representSequence(tag, conditional.getConditions(), dumperOptions.getDefaultFlowStyle());
  }

  private Node representAnd(Object data) {
    return representConditional(AND, (Conditional) data);
  }

  private Node representEquals(Object data) {
    return representConditional(EQUALS, (Conditional) data);
  }

  private Node representIf(Object data) {
    return representConditional(IF, (Conditional) data);
  }

  private Node representNot(Object data) {
    return representConditional(NOT, (Conditional) data);
  }

  private Node representOr(Object data) {
    return representConditional(OR, (Conditional) data);
  }

  private Node representFindInMap(Object data) {
    var findInMap = (FindInMap) data;

    var sequence = new ArrayList<>();
    sequence.add(findInMap.getMapName());
    sequence.add(findInMap.getTopLevelKey());
    sequence.add(findInMap.getSecondLevelKey());

    return representSequence(FIND_IN_MAP, sequence, dumperOptions.getDefaultFlowStyle());
  }

  private Node representGetAtt(Object data) {
    var getAtt = (GetAtt) data;
    return representScalar(GET_ATT, getAtt.getResource() + "." + getAtt.getAttribute());
  }

  private Node representGetAZs(Object data) {
    var region = ((GetAZs) data).getRegion();

    if (isScalar(region)) {
      return representScalar(GET_A_ZS, region.toString());
    } else {
      var mapping = new HashMap<>();
      mapping.put("Fn::GetAZs", region);
      return represent(mapping);
    }
  }

  private Node representImportValue(Object data) {
    var reference = ((ImportValue) data).getReference();

    if (isScalar(reference)) {
      return representScalar(
          IMPORT_VALUE, reference.toString(), dumperOptions.getDefaultScalarStyle());
    } else {
      var mapping = new HashMap<>();
      mapping.put("Fn::ImportValue", reference);
      return represent(mapping);
    }
  }

  private Node representJoin(Object data) {
    var join = (Join) data;

    var sequence = new ArrayList<>();
    sequence.add(join.getDelimiter());
    sequence.add(join.getValues());

    return representSequence(JOIN, sequence, dumperOptions.getDefaultFlowStyle());
  }

  private Node representSelect(Object data) {
    var select_ = (Select) data;

    var sequence = new ArrayList<>();
    sequence.add(select_.getIndex());
    sequence.add(select_.getSelection());

    return representSequence(SELECT, sequence, dumperOptions.getDefaultFlowStyle());
  }

  private Node representSplit(Object data) {
    var split = (Split) data;

    var sequence = new ArrayList<>();
    sequence.add(split.getDelimiter());
    sequence.add(split.getSource());

    return representSequence(SPLIT, sequence, dumperOptions.getDefaultFlowStyle());
  }

  private Node representSub(Object data) {
    var sub = (Sub) data;
    var variables = sub.getVariables();
    if (variables == null || variables.isEmpty()) {
      return representScalar(SUB, sub.getTemplate());
    } else {
      var sequence = new ArrayList<>();
      sequence.add(sub.getTemplate());
      sequence.add(sub.getVariables());
      return representSequence(SUB, sequence, dumperOptions.getDefaultFlowStyle());
    }
  }

  private Node representTransform(Object data) {
    var transform = (Transform) data;

    var mapping = new LinkedHashMap<>();
    mapping.put("Name", transform.getName());
    mapping.put("Parameters", transform.getParameters());

    return representMapping(TRANSFORM, mapping, dumperOptions.getDefaultFlowStyle());
  }

  private Node representRef(Object data) {
    return representScalar(REF, ((Ref) data).getReference(), dumperOptions.getDefaultScalarStyle());
  }
}
