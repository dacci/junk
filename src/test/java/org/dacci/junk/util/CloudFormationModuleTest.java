package org.dacci.junk.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.dacci.junk.util.cfn.*;

public class CloudFormationModuleTest {
  private static ObjectMapper json;

  @BeforeAll
  public static void setUpClass() {
    json = new ObjectMapper();
    json.registerModule(CloudFormationModule.getInstance());
  }

  @Test
  public void testBase64() throws JsonProcessingException {
    var data = new Base64("valueToEncode");
    var actual = json.writeValueAsString(data);
    assertThat(actual, is("{\"Fn::Base64\":\"valueToEncode\"}"));
  }

  @Test
  public void testCidr() throws JsonProcessingException {
    var data = new Cidr("192.168.0.0/24", 6, 5);
    var actual = json.writeValueAsString(data);
    assertThat(actual, is("{\"Fn::Cidr\":[\"192.168.0.0/24\",6,5]}"));
  }

  @ParameterizedTest
  @ValueSource(classes = {And.class, Equals.class, If.class, Not.class, Or.class})
  public void testConditional(Class<? extends Conditional> clazz)
      throws ReflectiveOperationException, JsonProcessingException {
    var constructor = clazz.getConstructor(Object[].class);
    var data = constructor.newInstance(new Object[] {new Object[] {"a", "b"}});
    var actual = json.writeValueAsString(data);
    assertThat(actual, is("{\"Fn::" + clazz.getSimpleName() + "\":[\"a\",\"b\"]}"));
  }

  @Test
  public void testFindInMap() throws JsonProcessingException {
    var data = new FindInMap("MapName", "TopLevelKey", "SecondLevelKey");
    var actual = json.writeValueAsString(data);
    assertThat(actual, is("{\"Fn::FindInMap\":[\"MapName\",\"TopLevelKey\",\"SecondLevelKey\"]}"));
  }

  @Test
  public void testGetAtt() throws JsonProcessingException {
    var data = new GetAtt("myELB", "DNSName");
    var actual = json.writeValueAsString(data);
    assertThat(actual, is("{\"Fn::GetAtt\":[\"myELB\",\"DNSName\"]}"));
  }

  @Test
  public void testGetAZs() throws JsonProcessingException {
    var data = new GetAZs("");
    var actual = json.writeValueAsString(data);
    assertThat(actual, is("{\"Fn::GetAZs\":\"\"}"));
  }

  @Test
  public void testImportValue() throws JsonProcessingException {
    var data = new ImportValue("sharedValueToImport");
    var actual = json.writeValueAsString(data);
    assertThat(actual, is("{\"Fn::ImportValue\":\"sharedValueToImport\"}"));
  }

  @Test
  public void testJoin() throws JsonProcessingException {
    var data = new Join(":", Arrays.asList("a", "b", "c"));
    var actual = json.writeValueAsString(data);
    assertThat(actual, is("{\"Fn::Join\":[\":\",[\"a\",\"b\",\"c\"]]}"));
  }

  @Test
  public void testSelect() throws JsonProcessingException {
    var data = new Select(1, Arrays.asList("apples", "grapes", "oranges", "mangoes"));
    var actual = json.writeValueAsString(data);
    assertThat(actual, is("{\"Fn::Select\":[1,[\"apples\",\"grapes\",\"oranges\",\"mangoes\"]]}"));
  }

  @Test
  public void testSplit() throws JsonProcessingException {
    var data = new Split("|", "a|b|c");
    var actual = json.writeValueAsString(data);
    assertThat(actual, is("{\"Fn::Split\":[\"|\",\"a|b|c\"]}"));
  }

  @Test
  public void testSub() throws JsonProcessingException {
    {
      var data = new Sub("String", Collections.emptyMap());
      var actual = json.writeValueAsString(data);
      assertThat(actual, is("{\"Fn::Sub\":\"String\"}"));
    }
    {
      var variables = new HashMap<String, Object>();
      variables.put("Var1Name", "Var1Value");
      var data = new Sub("String", variables);

      var actual = json.writeValueAsString(data);
      assertThat(actual, is("{\"Fn::Sub\":[\"String\",{\"Var1Name\":\"Var1Value\"}]}"));
    }
  }

  @Test
  public void testTransform() throws JsonProcessingException {
    var parameters = new HashMap<String, Object>();
    parameters.put("key", "value");
    var data = new Transform("macro_name", parameters);

    var actual = json.writeValueAsString(data);
    assertThat(
        actual,
        is("{\"Fn::Transform\":{\"Name\":\"macro_name\",\"Parameters\":{\"key\":\"value\"}}}"));
  }

  @Test
  public void testRef() throws JsonProcessingException {
    var data = new Ref("logicalName");
    var actual = json.writeValueAsString(data);
    assertThat(actual, is("{\"Ref\":\"logicalName\"}"));
  }

  @Test
  public void testCondition() throws JsonProcessingException {
    var condition = new Condition("someCondition");
    var actual = json.writeValueAsString(condition);
    assertThat(actual, is("{\"Condition\":\"someCondition\"}"));
  }
}
