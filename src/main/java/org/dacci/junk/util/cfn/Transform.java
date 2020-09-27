package org.dacci.junk.util.cfn;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transform {
  private String name;
  private Map<String, ?> parameters;
}
