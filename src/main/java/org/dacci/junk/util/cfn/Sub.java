package org.dacci.junk.util.cfn;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Sub {
  private String template;
  private Map<String, ?> variables;
}
