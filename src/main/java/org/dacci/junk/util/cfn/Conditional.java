package org.dacci.junk.util.cfn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import lombok.Data;

@Data
public abstract class Conditional {
  private final List<?> conditions;

  protected Conditional(Object[] conditions) {
    this.conditions = new ArrayList<>(Arrays.asList(conditions));
  }

  protected Conditional(Collection<?> conditions) {
    this.conditions = new ArrayList<>(conditions);
  }
}
