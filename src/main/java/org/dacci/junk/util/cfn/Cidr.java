package org.dacci.junk.util.cfn;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Cidr {
  private Object ipBlock;
  private int count;
  private int cidrBits;
}
