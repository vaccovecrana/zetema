package io.vacco.dns;

import io.vacco.dns.impl.*;
import io.vacco.dns.plugin.*;
import io.vacco.dns.plugin.exclude.*;
import io.vacco.dns.plugin.forward.*;
import io.vacco.dns.plugin.log.*;
import io.vacco.dns.schema.*;
import io.vacco.ufn.UFn;
import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DServer {

  private static final Logger log = LoggerFactory.getLogger(DServer.class);

  private final DatagramSocket receiver;
  private final Map<String, List<DPlugin>> zoneIdx = new HashMap<>();
  private final DProxyCfg cfg;

  public DServer(DProxyCfg cfg) {
    this.cfg = Objects.requireNonNull(cfg);
    this.receiver = UFn.tryRt(() -> new DatagramSocket(cfg.listen.port, InetAddress.getByName(cfg.listen.address)));
    UFn.tryRun(() -> receiver.setReuseAddress(true));

    cfg.zones.forEach(zCfg -> {
      DPluginCfg pc = zCfg.plugins;
      List<DPluginSlot> slots = new ArrayList<>();
      if (pc.forward != null) { slots.add(pc.forward); }
      if (pc.answerExclude != null) { slots.add(pc.answerExclude); }
      if (pc.log != null) { slots.add(pc.log); }

      slots.sort(Comparator.comparingInt(s0 -> s0.k));

      zoneIdx.put(zCfg.name, slots.stream().map(sl -> {
        if (sl instanceof DForwardCfg) return new DForward(zCfg);
        else if (sl instanceof DAExcludeCfg) return new DAExclude(zCfg);
        else if (sl instanceof DLogCfg) return new DLog(zCfg);
        throw new IllegalStateException(
            String.format("Unknown plugin type: [%s]", sl.getClass().getCanonicalName())
        );
      }).collect(Collectors.toList()));
    });

    log.info("Zetema - Listening at [{}]", receiver.getLocalAddress());
  }

  public CompletableFuture<DQuery> processRequest(DPacket request) {
    return CompletableFuture.supplyAsync(() -> UFn.tryRt(() -> {
      String zoneName = request.message.getQuestion().getName().toString(true);
      Optional<String> chainMatch = zoneIdx.keySet().stream().filter(zoneName::endsWith).findFirst();
      chainMatch = chainMatch.isPresent() ? chainMatch : Optional.of(".");
      DQuery query = new DQuery().withRequest(request);
      for (DPlugin plugin : zoneIdx.get(chainMatch.get())) { query = plugin.apply(query); }
      return query;
    }));
  }

  public CompletableFuture<DQuery> processRequest() {
    DPacket dp = DPacket.from(receiver);
    if (cfg.logAddresses) {
      log.info("{} -> {}", dp.packet.getSocketAddress(), receiver.getLocalSocketAddress());
    }
    return processRequest(dp);
  }

  public void respond(DQuery q) {
    UFn.tryRun(() -> {
      DPacket req = q.getRequest();
      DPacket res = q.getResponse();
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
    if (configPath == null && args.length > 0) {
      configPath = args[0];
    } else {
      throw new IllegalArgumentException("Missing configuration file.");
    }
    new DServer(DProxyCfg.load(new FileReader(new File(configPath)))).run();
  }
}
