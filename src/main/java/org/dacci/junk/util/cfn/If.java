package org.dacci.junk.util.cfn;

import java.util.Collection;

public class If extends Conditional {
  public If(Object... conditions) {
    super(conditions);
  }

  public If(Collection<?> conditions) {
    super(conditions);
  }
}
