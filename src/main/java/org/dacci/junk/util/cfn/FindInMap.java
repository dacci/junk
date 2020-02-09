package org.dacci.junk.util.cfn;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FindInMap {
  private String mapName;
  private Object topLevelKey;
  private String secondLevelKey;
}
