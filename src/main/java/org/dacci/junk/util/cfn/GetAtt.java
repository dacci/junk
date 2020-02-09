package org.dacci.junk.util.cfn;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetAtt {
  private String resource;
  private String attribute;
}
