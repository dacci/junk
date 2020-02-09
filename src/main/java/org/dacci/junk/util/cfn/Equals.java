package org.dacci.junk.util.cfn;

import java.util.Collection;

public class Equals extends Conditional {
  public Equals(Object... conditions) {
    super(conditions);
  }

  public Equals(Collection<?> conditions) {
    super(conditions);
  }
}
