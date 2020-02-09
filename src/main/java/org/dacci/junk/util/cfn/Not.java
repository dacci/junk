package org.dacci.junk.util.cfn;

import java.util.Collection;

public class Not extends Conditional {
  public Not(Object... conditions) {
    super(conditions);
  }

  public Not(Collection<?> conditions) {
    super(conditions);
  }
}
