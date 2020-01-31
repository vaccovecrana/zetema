package io.vacco.dns.schema.config;

import io.vacco.dns.plugin.aanswer.DAAnswerCfg;
import io.vacco.dns.plugin.exclude.DAExcludeCfg;
import io.vacco.dns.plugin.forward.DForwardCfg;
import io.vacco.dns.plugin.log.DLogCfg;

public class DPlugins {
  public DForwardCfg forward;
  public DAAnswerCfg aAnswer;
  public DAExcludeCfg answerExclude;
  public DLogCfg log;
}
