package org.dacci.junk.util.cfn;

import java.util.Collection;

public class Or extends Conditional {
  public Or(Object... conditions) {
    super(conditions);
  }

  public Or(Collection<?> conditions) {
    super(conditions);
  }
}
