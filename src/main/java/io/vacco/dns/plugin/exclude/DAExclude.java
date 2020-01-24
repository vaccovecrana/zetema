package io.vacco.dns.plugin.exclude;

import io.vacco.dns.impl.*;
import io.vacco.dns.plugin.DPlugin;
import io.vacco.dns.schema.DZoneCfg;
import org.xbill.DNS.*;
import java.util.*;

public class DAExclude implements DPlugin {

  private final DZoneCfg zoneCfg;

  public DAExclude(DZoneCfg zoneCfg) {
    this.zoneCfg = Objects.requireNonNull(zoneCfg);
  }

  @Override
  public DQuery apply(DQuery query) {
    List<ARecord> aRecords = new ArrayList<>();
    DPacket response = query.getResponse();
    for (RRset sectionRRset : response.message.getSectionRRsets(Section.ANSWER)) {
      sectionRRset.rrs().forEachRemaining(r0 -> {
        if (r0 instanceof ARecord) {
          aRecords.add((ARecord) r0);
        }
      });
    }
    aRecords.stream()
        .filter(ar0 -> ar0.getName().toString(true).endsWith(zoneCfg.name))
        .filter(ar0 -> zoneCfg.plugins.answerExclude.match.contains(ar0.getAddress().getHostAddress()))
        .forEach(ar0 -> response.message.removeRecord(ar0, Section.ANSWER));
    return query;
  }
}
