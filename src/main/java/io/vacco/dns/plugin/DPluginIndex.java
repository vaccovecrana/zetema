package io.vacco.dns.plugin;

import io.vacco.dns.plugin.aanswer.*;
import io.vacco.dns.plugin.exclude.*;
import io.vacco.dns.plugin.forward.*;
import io.vacco.dns.plugin.log.*;
import io.vacco.dns.schema.config.*;

import java.util.*;
import java.util.stream.Collectors;

public class DPluginIndex extends HashMap<String, List<io.vacco.dns.plugin.DPlugin>> {

  public DPluginIndex(DProxy cfg) {
    cfg.zones.forEach(zCfg -> {

      DPlugins pc = zCfg.plugins;
      List<DPluginSlot> slots = new ArrayList<>();

      if (pc.forward != null) { slots.add(pc.forward); }
      if (pc.aAnswer != null) { slots.add(pc.aAnswer); }
      if (pc.answerExclude != null) { slots.add(pc.answerExclude); }
      if (pc.log != null) { slots.add(pc.log); }

      slots.sort(Comparator.comparingInt(s0 -> s0.k));

      put(zCfg.name, slots.stream().map(sl -> {
        if (sl instanceof DForwardCfg) return new DForward(zCfg);
        else if (sl instanceof DAExcludeCfg) return new DAExclude(zCfg);
        else if (sl instanceof DLogCfg) return new DLog(zCfg);
        else if (sl instanceof DAAnswerCfg) return new DAAnswer(zCfg);
        throw new IllegalStateException(
            String.format("Unknown plugin type: [%s]", sl.getClass().getCanonicalName())
        );
      }).collect(Collectors.toList()));
    });
  }
}
