package org.dacci.junk.util.cfn;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Select {
  private int index;
  private List<?> selection;
}
