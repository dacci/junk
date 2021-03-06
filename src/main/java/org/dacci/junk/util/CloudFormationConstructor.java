package org.dacci.junk.util;

import static org.dacci.junk.util.CloudFormationYaml.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

import org.dacci.junk.util.cfn.And;
import org.dacci.junk.util.cfn.Base64;
import org.dacci.junk.util.cfn.Cidr;
import org.dacci.junk.util.cfn.Condition;
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

public class CloudFormationConstructor extends Constructor {
  @FunctionalInterface
  private interface Construct extends org.yaml.snakeyaml.constructor.Construct {
    @Override
    default void construct2ndStep(Node node, Object object) {
      if (node.isTwoStepsConstruction()) {
        throw new IllegalStateException("Not Implemented in " + getClass().getName());
      } else {
        throw new YAMLException("Unexpected recursive structure for Node: " + node);
      }
    }
  }

  public CloudFormationConstructor() {
    yamlConstructors.put(BASE64, (Construct) this::constructBase64);
    yamlConstructors.put(CIDR, (Construct) this::constructCidr);
    yamlConstructors.put(AND, (Construct) this::constructConditional);
    yamlConstructors.put(EQUALS, (Construct) this::constructConditional);
    yamlConstructors.put(IF, (Construct) this::constructConditional);
    yamlConstructors.put(NOT, (Construct) this::constructConditional);
    yamlConstructors.put(OR, (Construct) this::constructConditional);
    yamlConstructors.put(FIND_IN_MAP, (Construct) this::constructFindInMap);
    yamlConstructors.put(GET_ATT, (Construct) this::constructGetAtt);
    yamlConstructors.put(GET_A_ZS, (Construct) this::constructGetAZs);
    yamlConstructors.put(IMPORT_VALUE, (Construct) this::constructImportValue);
    yamlConstructors.put(JOIN, (Construct) this::constructJoin);
    yamlConstructors.put(SELECT, (Construct) this::constructSelect);
    yamlConstructors.put(SPLIT, (Construct) this::constructSplit);
    yamlConstructors.put(SUB, (Construct) this::constructSub);
    yamlConstructors.put(TRANSFORM, (Construct) this::constructTransform);
    yamlConstructors.put(REF, (Construct) this::constructRef);
    yamlConstructors.put(CONDITION, (Construct) this::constructCondition);
  }

  @Override
  protected Object constructObject(Node node) {
    if (node.getNodeId() == NodeId.mapping
        && !node.getTag().isSecondary()
        && ((MappingNode) node).getValue().size() == 1) {
      var tuple = ((MappingNode) node).getValue().get(0);
      var key = ((ScalarNode) tuple.getKeyNode()).getValue();

      switch (key) {
        case "Fn::Base64":
          return constructBase64(tuple.getValueNode());

        case "Fn::Cidr":
          return constructCidr(tuple.getValueNode());

        case "Fn::And":
        case "Fn::Equals":
        case "Fn::If":
        case "Fn::Not":
        case "Fn::Or":
          return constructConditional(key.substring(4), tuple.getValueNode());

        case "Fn::FindInMap":
          return constructFindInMap(tuple.getValueNode());

        case "Fn::GetAtt":
          return constructGetAtt(tuple.getValueNode());

        case "Fn::GetAZs":
          return constructGetAZs(tuple.getValueNode());

        case "Fn::ImportValue":
          return constructImportValue(tuple.getValueNode());

        case "Fn::Join":
          return constructJoin(tuple.getValueNode());

        case "Fn::Select":
          return constructSelect(tuple.getValueNode());

        case "Fn::Split":
          return constructSplit(tuple.getValueNode());

        case "Fn::Sub":
          return constructSub(tuple.getValueNode());

        case "Fn::Transform":
          return constructTransform(tuple.getValueNode());

        case "Ref":
          return constructRef(tuple.getValueNode());

        case "Condition":
          return constructCondition(tuple.getValueNode());
      }
    }

    return super.constructObject(node);
  }

  private int parseInt(Object value) {
    if (value instanceof Number) {
      return ((Number) value).intValue();
    } else if (value instanceof CharSequence) {
      return Integer.parseInt(((CharSequence) value).toString());
    }

    throw new IllegalArgumentException(value + " is neither Number nor CharSequence");
  }

  private Object constructBase64(Node node) {
    if (node instanceof ScalarNode) {
      return new Base64(constructScalar((ScalarNode) node));
    } else if (node instanceof MappingNode) {
      return new Base64(constructMapping((MappingNode) node));
    }

    throw new YAMLException("Unexpected node: " + node.getNodeId());
  }

  private Object constructCidr(Node node) {
    var sequence = constructSequence((SequenceNode) node);

    int count = parseInt(sequence.get(1));
    int cidrBits = parseInt(sequence.get(2));

    return new Cidr(sequence.get(0), count, cidrBits);
  }

  private Object constructConditional(String tag, Node node) {
    switch (tag) {
      case "And":
        return new And(constructSequence((SequenceNode) node));

      case "Equals":
        return new Equals(constructSequence((SequenceNode) node));

      case "If":
        return new If(constructSequence((SequenceNode) node));

      case "Not":
        return new Not(constructSequence((SequenceNode) node));

      case "Or":
        return new Or(constructSequence((SequenceNode) node));

      default:
        throw new YAMLException("Unexpected conditional: " + tag);
    }
  }

  private Object constructConditional(Node node) {
    return constructConditional(node.getTag().getValue().substring(1), node);
  }

  private Object constructFindInMap(Node node) {
    var sequence = constructSequence((SequenceNode) node);
    return new FindInMap((String) sequence.get(0), sequence.get(1), (String) sequence.get(2));
  }

  private Object constructGetAtt(Node node) {
    if (node instanceof ScalarNode) {
      var pair = constructScalar((ScalarNode) node).split("\\.", 2);
      return new GetAtt(pair[0], pair[1]);
    } else if (node instanceof SequenceNode) {
      var sequence = constructSequence((SequenceNode) node);
      return new GetAtt(sequence.get(0), (String) sequence.get(1));
    }

    throw new YAMLException("Unexpected node: " + node.getNodeId());
  }

  private Object constructGetAZs(Node node) {
    return new GetAZs(constructScalar((ScalarNode) node));
  }

  private Object constructImportValue(Node node) {
    return new ImportValue(constructScalar((ScalarNode) node));
  }

  private Object constructJoin(Node node) {
    var sequence = constructSequence((SequenceNode) node);
    return new Join(Objects.toString(sequence.get(0)), (List<?>) sequence.get(1));
  }

  private Object constructSelect(Node node) {
    var sequence = constructSequence((SequenceNode) node);
    return new Select(parseInt(sequence.get(0)), sequence.get(1));
  }

  private Object constructSplit(Node node) {
    var sequence = constructSequence((SequenceNode) node);
    return new Split((String) sequence.get(0), sequence.get(1));
  }

  @SuppressWarnings("unchecked")
  private Object constructSub(Node node) {
    String template;
    Map<String, Object> variables;

    if (node instanceof ScalarNode) {
      template = constructScalar((ScalarNode) node);
      variables = Collections.emptyMap();
    } else if (node instanceof SequenceNode) {
      var sequence = constructSequence((SequenceNode) node);
      template = (String) sequence.get(0);
      variables = (Map<String, Object>) sequence.get(1);
    } else {
      throw new YAMLException("Unexpected node: " + node.getNodeId());
    }

    return new Sub(template, variables);
  }

  private Object constructTransform(Node node) {
    var mapping = constructMapping((MappingNode) node);
    @SuppressWarnings("unchecked")
    var parameters = (Map<String, ?>) mapping.get("Parameters");
    return new Transform((String) mapping.get("Name"), parameters);
  }

  private Object constructRef(Node node) {
    return new Ref(constructScalar((ScalarNode) node));
  }

  private Object constructCondition(Node node) {
    return new Condition(constructScalar((ScalarNode) node));
  }
}
