package io.vacco.dns.schema;

import io.vacco.dns.plugin.exclude.DAExcludeCfg;
import io.vacco.dns.plugin.forward.DForwardCfg;
import io.vacco.dns.plugin.log.DLogCfg;

public class DPluginCfg {
  public DForwardCfg forward;
  public DAExcludeCfg answerExclude;
  public DLogCfg log;
}
