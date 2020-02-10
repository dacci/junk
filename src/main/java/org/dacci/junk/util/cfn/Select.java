package org.dacci.junk.util.cfn;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Select {
  private int index;
  private Object selection;
}
