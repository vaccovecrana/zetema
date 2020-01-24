package io.vacco.dns.plugin.log;

import io.vacco.dns.impl.DQuery;
import io.vacco.dns.plugin.DPlugin;
import org.slf4j.*;

public class DLog implements DPlugin {

  private static final Logger log = LoggerFactory.getLogger(DLog.class);

  @Override
  public DQuery apply(DQuery query) {
    log.info(query.getRequest().message.toString());
    if (query.getResponse() != null) {
      log.info(query.getResponse().message.toString());
    }
    return query;
  }
}
