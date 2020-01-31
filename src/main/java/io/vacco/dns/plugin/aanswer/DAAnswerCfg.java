package io.vacco.dns.plugin.aanswer;

import io.vacco.dns.plugin.DPluginSlot;
import java.util.Set;

public class DAAnswerCfg extends DPluginSlot {
  public Set<String> with;
  public long ttl;
}
