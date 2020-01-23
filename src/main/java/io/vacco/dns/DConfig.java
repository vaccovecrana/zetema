package io.vacco.dns;

import java.util.Objects;

public class DConfig {

  public String listenAddress, upstreamAddress;
  public int listenPort, upstreamPort;
  public boolean logQueries;

  public static DConfig from(String listenAddress, String lp, String upstreamAddress, String up, boolean logQueries) {
    DConfig cfg = new DConfig();
    cfg.listenAddress = listenAddress != null ? listenAddress : "127.0.0.1";
    cfg.upstreamAddress = Objects.requireNonNull(upstreamAddress);
    cfg.listenPort = lp != null ? Integer.parseInt(lp) : 53;
    cfg.upstreamPort = up != null ? Integer.parseInt(up) : 53;
    cfg.logQueries = logQueries;
    if (logQueries) {
      System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
    }
    return cfg;
  }

  public static DConfig loadEnv() {
    return from(
        System.getenv("ZETEMA_LISTEN_ADDRESS"),
        System.getenv("ZETEMA_LISTEN_PORT"),
        System.getenv("ZETEMA_UPSTREAM_ADDRESS"),
        System.getenv("ZETEMA_UPSTREAM_PORT"),
        Boolean.parseBoolean(System.getenv("ZETEMA_LOG_QUERIES"))
    );
  }

  @Override
  public String toString() {
    return "DConfig{" +
        "listenAddress='" + listenAddress + '\'' +
        ", listenPort=" + listenPort +
        ", upstreamAddress='" + upstreamAddress + '\'' +
        ", upstreamPort=" + upstreamPort +
        ", logQueries=" + logQueries +
        '}';
  }
}
