package org.dacci.junk.util.cfn;

import java.util.Collection;

public class And extends Conditional {
  public And(Object... conditions) {
    super(conditions);
  }

  public And(Collection<?> conditions) {
    super(conditions);
  }
}
