package org.dacci.junk.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.dacci.junk.util.cfn.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.yaml.snakeyaml.Yaml;

public class CloudFormationRepresenterTest {
  private static Yaml yaml;

  @BeforeAll
  public static void setUpClass() {
    yaml = new Yaml(new CloudFormationRepresenter());
  }

  @Test
  public void testBase64() {
    {
      var data = new Base64("valueToEncode");
      var actual = yaml.dump(data);
      assertThat(actual, is("!Base64 'valueToEncode'\n"));
    }
    {
      var data = new Base64(new Ref("logicalId"));
      var actual = yaml.dump(data);
      assertThat(actual, is("{'Fn::Base64': !Ref 'logicalId'}\n"));
    }
  }

  @Test
  public void testCidr() {
    {
      var data = new Cidr("192.168.0.0/24", 6, 5);
      var actual = yaml.dump(data);
      assertThat(actual, is("!Cidr [192.168.0.0/24, 6, 5]\n"));
    }
    {
      var data = new Cidr(new GetAtt("ExampleVpc", "CidrBlock"), 1, 8);
      var actual = yaml.dump(data);
      assertThat(actual, is("!Cidr [!GetAtt 'ExampleVpc.CidrBlock', 1, 8]\n"));
    }
  }

  @ParameterizedTest
  @ValueSource(classes = {And.class, Equals.class, If.class, Not.class, Or.class})
  public void testConditional(Class<? extends Conditional> clazz)
      throws ReflectiveOperationException {
    var constructor = clazz.getConstructor(Object[].class);
    var data = constructor.newInstance(new Object[] {new Object[] {new Ref("a"), "b"}});
    var actual = yaml.dump(data);
    assertThat(actual, is("!" + clazz.getSimpleName() + " [!Ref 'a', b]\n"));
  }

  @Test
  public void testFindInMap() {
    {
      var data = new FindInMap("MapName", "TopLevelKey", "SecondLevelKey");
      var actual = yaml.dump(data);
      assertThat(actual, is("!FindInMap [MapName, TopLevelKey, SecondLevelKey]\n"));
    }
    {
      var data = new FindInMap("RegionMap", new Ref("AWS::Region"), "HVM64");
      var actual = yaml.dump(data);
      assertThat(actual, is("!FindInMap [RegionMap, !Ref 'AWS::Region', HVM64]\n"));
    }
  }

  @Test
  public void testGetAtt() {
    var data = new GetAtt("myELB", "DNSName");
    var actual = yaml.dump(data);
    assertThat(actual, is("!GetAtt 'myELB.DNSName'\n"));
  }

  @Test
  public void testGetAZs() {
    {
      var data = new GetAZs("");
      var actual = yaml.dump(data);
      assertThat(actual, is("!GetAZs ''\n"));
    }
    {
      var data = new GetAZs(new Ref("AWS::Region"));
      var actual = yaml.dump(data);
      assertThat(actual, is("{'Fn::GetAZs': !Ref 'AWS::Region'}\n"));
    }
  }

  @Test
  public void testImportValue() {
    {
      var data = new ImportValue("sharedValueToImport");
      var actual = yaml.dump(data);
      assertThat(actual, is("!ImportValue 'sharedValueToImport'\n"));
    }
    {
      var sub = new Sub("${NetworkStack}-SubnetID", null);
      var data = new ImportValue(sub);
      var actual = yaml.dump(data);
      assertThat(actual, is("{'Fn::ImportValue': !Sub '${NetworkStack}-SubnetID'}\n"));
    }
  }

  @Test
  public void testJoin() {
    var data = new Join(":", Arrays.asList("a", "b", "c"));
    var actual = yaml.dump(data);
    assertThat(actual, is("!Join\n- ':'\n- [a, b, c]\n"));
  }

  @Test
  public void testSelect() {
    var data = new Select(1, Arrays.asList("apples", "grapes", "oranges", "mangoes"));
    var actual = yaml.dump(data);
    assertThat(actual, is("!Select\n- 1\n- [apples, grapes, oranges, mangoes]\n"));
  }

  @Test
  public void testSplit() {
    var data = new Split("|", "a|b|c");
    var actual = yaml.dump(data);
    assertThat(actual, is("!Split ['|', a|b|c]\n"));
  }

  @Test
  public void testSub() {
    {
      var data = new Sub("String", Collections.emptyMap());
      var actual = yaml.dump(data);
      assertThat(actual, is("!Sub 'String'\n"));
    }
    {
      var variables = new HashMap<String, Object>();
      variables.put("Var1Name", "Var1Value");
      var data = new Sub("String", variables);

      var actual = yaml.dump(data);
      assertThat(actual, is("!Sub\n- String\n- {Var1Name: Var1Value}\n"));
    }
  }

  @Test
  public void testTransform() {
    var parameters = new HashMap<String, Object>();
    parameters.put("key", "value");
    var data = new Transform("macro_name", parameters);

    var actual = yaml.dump(data);
    assertThat(actual, is("!Transform\nName: macro_name\nParameters: {key: value}\n"));
  }

  @Test
  public void testRef() {
    var actual = yaml.dump(new Ref("logicalName"));
    assertThat(actual, is("!Ref 'logicalName'\n"));
  }
}
