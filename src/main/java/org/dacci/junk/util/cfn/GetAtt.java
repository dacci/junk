package org.dacci.junk.util.cfn;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetAtt {
  private Object resource;
  private String attribute;
}
