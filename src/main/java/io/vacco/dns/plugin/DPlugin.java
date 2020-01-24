package io.vacco.dns.plugin;

import io.vacco.dns.impl.DQuery;

public interface DPlugin {
  DQuery apply(DQuery query) throws Exception;
}
