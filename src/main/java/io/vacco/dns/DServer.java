package io.vacco.dns;

import io.vacco.dns.impl.*;
import io.vacco.dns.plugin.*;
import io.vacco.dns.schema.config.DProxy;
import io.vacco.ufn.UFn;
import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class DServer {

  private static final Logger log = LoggerFactory.getLogger(DServer.class);

  private final DatagramSocket receiver;
  private final DPluginIndex pluginIndex;
  private final DProxy proxyCfg;

  public DServer(DProxy cfg) {
    this.proxyCfg = Objects.requireNonNull(cfg);
    this.pluginIndex = new DPluginIndex(cfg);
    this.receiver = UFn.tryRt(() -> new DatagramSocket(cfg.listen.port, InetAddress.getByName(cfg.listen.address)));
    UFn.tryRun(() -> receiver.setReuseAddress(true));
    log.info("Zetema - Listening at [{}]", receiver.getLocalAddress());
  }

  public CompletableFuture<DQuery> processRequest(DnsPacket request) {
    return CompletableFuture.supplyAsync(() -> UFn.tryRt(() -> {
      String zoneName = request.message.getQuestion().getName().toString(true);
      Optional<String> chainMatch = pluginIndex.keySet().stream().filter(zoneName::endsWith).findFirst();
      chainMatch = chainMatch.isPresent() ? chainMatch : Optional.of(".");
      DQuery query = new DQuery().withRequest(request);
      for (DPlugin plugin : pluginIndex.get(chainMatch.get())) { query = plugin.apply(query); }
      return query;
    }));
  }

  public CompletableFuture<DQuery> processRequest() {
    DnsPacket dp = DnsPacket.from(receiver);
    if (proxyCfg.logAddresses) {
      log.info("{} -> {}", dp.packet.getSocketAddress(), receiver.getLocalSocketAddress());
    }
    return processRequest(dp);
  }

  public void respond(DQuery q) {
    UFn.tryRun(() -> {
      DnsPacket req = q.getRequest();
      DnsPacket res = q.getResponse();
      DatagramPacket out = res.markFor(req.packet.getSocketAddress());
      receiver.send(out);
    });
  }

  public void run() {
    while (true) {
      processRequest().thenAccept(this::respond);
    }
  }

  public static void main(String[] args) throws Exception {
    String configPath = System.getenv("ZETEMA_CONFIG");
    if (configPath == null) {
      if (args.length > 0) {
        configPath = args[0];
      } else {
        throw new IllegalArgumentException("Missing configuration file.");
      }
    }
    new DServer(DProxy.load(new FileReader(new File(configPath)))).run();
  }
}
