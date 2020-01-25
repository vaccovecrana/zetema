package io.vacco.dns.plugin.log;

import io.vacco.dns.impl.DPacket;
import io.vacco.dns.impl.DQuery;
import io.vacco.dns.plugin.DPlugin;
import io.vacco.dns.schema.DZoneCfg;
import org.slf4j.*;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DLog implements DPlugin {

  private static final Logger log = LoggerFactory.getLogger(DLog.class);
  private final DLogCfg logCfg;

  public DLog(DZoneCfg cfg) {
    this.logCfg = cfg.plugins.log;
  }

  @Override
  public DQuery apply(DQuery query) {

    DPacket req = query.getRequest();
    DPacket res = query.getResponse();

    log.info(req.packet.getAddress().toString());
    log.info(req.packet.getSocketAddress().toString());
    if (logCfg.verbose) {
      log.info(req.message.toString());
    } else {
      log.info(req.message.getQuestion().toString());
    }
    if (res != null) {
      log.info(res.packet.getAddress().toString());
      log.info(res.packet.getSocketAddress().toString());
      if (logCfg.verbose) {
        log.info(res.message.toString());
      } else {
        log.info("\n{}", Arrays.stream(res.message.getSectionArray(Section.ANSWER))
            .map(Record::toString).collect(Collectors.joining("\n")));
      }
    }
    return query;
  }
}
