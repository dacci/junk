package org.dacci.junk.util;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

public class CloudFormationYaml extends Yaml {
  public static final Tag BASE64 = new Tag("!Base64");
  public static final Tag CIDR = new Tag("!Cidr");
  public static final Tag AND = new Tag("!And");
  public static final Tag EQUALS = new Tag("!Equals");
  public static final Tag IF = new Tag("!If");
  public static final Tag NOT = new Tag("!Not");
  public static final Tag OR = new Tag("!Or");
  public static final Tag FIND_IN_MAP = new Tag("!FindInMap");
  public static final Tag GET_ATT = new Tag("!GetAtt");
  public static final Tag GET_A_ZS = new Tag("!GetAZs");
  public static final Tag IMPORT_VALUE = new Tag("!ImportValue");
  public static final Tag JOIN = new Tag("!Join");
  public static final Tag SELECT = new Tag("!Select");
  public static final Tag SPLIT = new Tag("!Split");
  public static final Tag SUB = new Tag("!Sub");
  public static final Tag TRANSFORM = new Tag("!Transform");
  public static final Tag REF = new Tag("!Ref");
  public static final Tag CONDITION = new Tag("!Condition");

  public CloudFormationYaml() {
    this(new DumperOptions(), new LoaderOptions());
  }

  public CloudFormationYaml(DumperOptions dumperOptions) {
    this(dumperOptions, new LoaderOptions());
  }

  public CloudFormationYaml(LoaderOptions loaderOptions) {
    this(new DumperOptions(), loaderOptions);
  }

  public CloudFormationYaml(DumperOptions dumperOptions, LoaderOptions loaderOptions) {
    super(
        new CloudFormationConstructor(),
        new CloudFormationRepresenter(dumperOptions),
        dumperOptions,
        loaderOptions);
  }
}
