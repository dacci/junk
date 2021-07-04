package org.dacci.junk.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.yaml.snakeyaml.Yaml;

import org.dacci.junk.util.cfn.*;

public class CloudFormationConstructorTest {
  private static Yaml yaml;

  @BeforeAll
  public static void setUpClass() {
    yaml = new Yaml(new CloudFormationConstructor());
  }

  @Test
  public void testBase64() {
    {
      var actual = yaml.load("!Base64 valueToEncode");
      assertThat(actual, instanceOf(Base64.class));
      assertThat(actual, hasProperty("value", is("valueToEncode")));
    }
    {
      var actual = yaml.load("!Base64\n'Fn::Sub': string");
      assertThat(actual, instanceOf(Base64.class));
      assertThat(actual, hasProperty("value", hasEntry("Fn::Sub", "string")));
    }
    {
      var actual = yaml.load("Fn::Base64: valueToEncode");
      assertThat(actual, instanceOf(Base64.class));
      assertThat(actual, hasProperty("value", is("valueToEncode")));
    }
    {
      var actual = yaml.load("Fn::Base64:\n  'Fn::Sub': string");
      assertThat(actual, instanceOf(Base64.class));
      assertThat(actual, hasProperty("value", hasEntry("Fn::Sub", "string")));
    }
  }

  @Test
  public void testCidr() {
    {
      var actual = yaml.load("!Cidr [ '192.168.0.0/24', 6, 5 ]");
      assertThat(actual, instanceOf(Cidr.class));
      assertThat(actual, hasProperty("ipBlock", is("192.168.0.0/24")));
      assertThat(actual, hasProperty("count", is(6)));
      assertThat(actual, hasProperty("cidrBits", is(5)));
    }
    {
      var actual = yaml.load("Fn::Cidr: [ '192.168.0.0/24', 6, 5 ]");
      assertThat(actual, instanceOf(Cidr.class));
      assertThat(actual, hasProperty("ipBlock", is("192.168.0.0/24")));
      assertThat(actual, hasProperty("count", is(6)));
      assertThat(actual, hasProperty("cidrBits", is(5)));
    }
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "!And",
        "!Equals",
        "!If",
        "!Not",
        "!Or",
        "Fn::And:",
        "Fn::Equals:",
        "Fn::If:",
        "Fn::Not:",
        "Fn::Or:"
      })
  public void testConditional(String tag) {
    var actual = yaml.load(tag + " [a, b]");
    assertThat(actual, instanceOf(Conditional.class));
    assertThat(actual, hasProperty("conditions", hasItems("a", "b")));
  }

  @Test
  public void testFindInMap() {
    {
      var actual = yaml.load("!FindInMap [ MapName, TopLevelKey, SecondLevelKey ]");
      assertThat(actual, instanceOf(FindInMap.class));
      assertThat(actual, hasProperty("mapName", is("MapName")));
      assertThat(actual, hasProperty("topLevelKey", is("TopLevelKey")));
      assertThat(actual, hasProperty("secondLevelKey", is("SecondLevelKey")));
    }
    {
      var actual = yaml.load("Fn::FindInMap: [ MapName, TopLevelKey, SecondLevelKey ]");
      assertThat(actual, instanceOf(FindInMap.class));
      assertThat(actual, hasProperty("mapName", is("MapName")));
      assertThat(actual, hasProperty("topLevelKey", is("TopLevelKey")));
      assertThat(actual, hasProperty("secondLevelKey", is("SecondLevelKey")));
    }
  }

  @Test
  public void testGetAtt() {
    {
      var actual = yaml.load("!GetAtt myELB.DNSName");
      assertThat(actual, instanceOf(GetAtt.class));
      assertThat(actual, hasProperty("resource", is("myELB")));
      assertThat(actual, hasProperty("attribute", is("DNSName")));
    }
    {
      var actual = yaml.load("Fn::GetAtt: [myELB, DNSName]");
      assertThat(actual, instanceOf(GetAtt.class));
      assertThat(actual, hasProperty("resource", is("myELB")));
      assertThat(actual, hasProperty("attribute", is("DNSName")));
    }
  }

  @Test
  public void testGetAZs() {
    {
      var actual = yaml.load("!GetAZs ap-northeast-1");
      assertThat(actual, instanceOf(GetAZs.class));
      assertThat(actual, hasProperty("region", is("ap-northeast-1")));
    }
    {
      var actual = yaml.load("Fn::GetAZs: ap-northeast-1");
      assertThat(actual, instanceOf(GetAZs.class));
      assertThat(actual, hasProperty("region", is("ap-northeast-1")));
    }
  }

  @Test
  public void testImportValue() {
    {
      var actual = yaml.load("!ImportValue sharedValueToImport");
      assertThat(actual, instanceOf(ImportValue.class));
      assertThat(actual, hasProperty("reference", is("sharedValueToImport")));
    }
    {
      var actual = yaml.load("Fn::ImportValue: sharedValueToImport");
      assertThat(actual, instanceOf(ImportValue.class));
      assertThat(actual, hasProperty("reference", is("sharedValueToImport")));
    }
  }

  @Test
  public void testJoin() {
    {
      var actual = yaml.load("!Join [ ':', [ a, b, c ] ]");
      assertThat(actual, instanceOf(Join.class));
      assertThat(actual, hasProperty("delimiter", is(":")));
      assertThat(actual, hasProperty("values", hasItems("a", "b", "c")));
    }
    {
      var actual = yaml.load("Fn::Join: [ ':', [ a, b, c ] ]");
      assertThat(actual, instanceOf(Join.class));
      assertThat(actual, hasProperty("delimiter", is(":")));
      assertThat(actual, hasProperty("values", hasItems("a", "b", "c")));
    }
    {
      var actual = yaml.load("!Join ['', !Ref SecurityGroupId]");
      assertThat(actual, instanceOf(Join.class));
    }
  }

  @Test
  public void testSelect() {
    {
      var actual = yaml.load("!Select [ '1', [ 'apples', 'grapes', 'oranges', 'mangoes' ] ]");
      assertThat(actual, instanceOf(Select.class));
      assertThat(actual, hasProperty("index", is(1)));
      assertThat(
          actual, hasProperty("selection", hasItems("apples", "grapes", "oranges", "mangoes")));
    }
    {
      var actual = yaml.load("Fn::Select: [ '1', [ 'apples', 'grapes', 'oranges', 'mangoes' ] ]");
      assertThat(actual, instanceOf(Select.class));
      assertThat(actual, hasProperty("index", is(1)));
      assertThat(
          actual, hasProperty("selection", hasItems("apples", "grapes", "oranges", "mangoes")));
    }
  }

  @Test
  public void testSplit() {
    {
      var actual = yaml.load("!Split ['|', 'a|b|c']");
      assertThat(actual, instanceOf(Split.class));
      assertThat(actual, hasProperty("delimiter", is("|")));
      assertThat(actual, hasProperty("source", is("a|b|c")));
    }
    {
      var actual = yaml.load("Fn::Split: ['|', 'a|b|c']");
      assertThat(actual, instanceOf(Split.class));
      assertThat(actual, hasProperty("delimiter", is("|")));
      assertThat(actual, hasProperty("source", is("a|b|c")));
    }
  }

  @Test
  public void testSub() {
    {
      var actual = yaml.load("!Sub String");
      assertThat(actual, instanceOf(Sub.class));
      assertThat(actual, hasProperty("template", is("String")));
    }
    {
      var actual = yaml.load("!Sub [String, {Var1Name: Var1Value}]");
      assertThat(actual, instanceOf(Sub.class));
      assertThat(actual, hasProperty("template", is("String")));
      assertThat(actual, hasProperty("variables", hasEntry("Var1Name", "Var1Value")));
    }
    {
      var actual = yaml.load("Fn::Sub: String");
      assertThat(actual, instanceOf(Sub.class));
      assertThat(actual, hasProperty("template", is("String")));
    }
    {
      var actual = yaml.load("Fn::Sub: [String, {Var1Name: Var1Value}]");
      assertThat(actual, instanceOf(Sub.class));
      assertThat(actual, hasProperty("template", is("String")));
      assertThat(actual, hasProperty("variables", hasEntry("Var1Name", "Var1Value")));
    }
  }

  @Test
  public void testTransform() {
    {
      var actual = yaml.load("!Transform { Name : macro_name, Parameters : {key : value } }");
      assertThat(actual, instanceOf(Transform.class));
      assertThat(actual, hasProperty("name", is("macro_name")));
      assertThat(actual, hasProperty("parameters", hasEntry("key", "value")));
    }
    {
      var actual = yaml.load("Fn::Transform: { Name : macro_name, Parameters : {key : value } }");
      assertThat(actual, instanceOf(Transform.class));
      assertThat(actual, hasProperty("name", is("macro_name")));
      assertThat(actual, hasProperty("parameters", hasEntry("key", "value")));
    }
  }

  @Test
  public void testRef() {
    {
      var actual = yaml.load("!Ref logicalName");
      assertThat(actual, instanceOf(Ref.class));
      assertThat(actual, hasProperty("reference", is("logicalName")));
    }
    {
      var actual = yaml.load("Ref: logicalName");
      assertThat(actual, instanceOf(Ref.class));
      assertThat(actual, hasProperty("reference", is("logicalName")));
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"!Condition someCondition", "Condition: someCondition"})
  public void testCondition(String expression) {
    var actual = yaml.load(expression);
    assertThat(actual, instanceOf(Condition.class));
    assertThat(actual, hasProperty("condition", is("someCondition")));
  }
}
