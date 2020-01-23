package io.vacco.dns;

import io.vacco.dns.plugin.DResponsePlugin;
import io.vacco.ufn.UFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DProxy {

  private final Logger log;
  private ThreadLocal<DatagramSocket> upstreamDs = ThreadLocal.withInitial(() -> UFn.tryRt(DatagramSocket::new));
  private final InetSocketAddress upstreamAddress;
  private final List<DResponsePlugin> plugins = new ArrayList<>();

  public DProxy(InetSocketAddress upstreamAddress) {
    log = LoggerFactory.getLogger(DProxy.class);
    this.upstreamAddress = Objects.requireNonNull(upstreamAddress);
  }

  public DProxy(String upstreamAddress, int upstreamPort) {
    this(new InetSocketAddress(upstreamAddress, upstreamPort));
  }

  public CompletableFuture<DatagramPacket> process(DPacket request) {
    return CompletableFuture.supplyAsync(() -> UFn.tryRt(() -> {
      if (log.isDebugEnabled()) {
        log.debug("Request: [{}]", request.message);
      }

      DatagramSocket us = upstreamDs.get();
      us.send(request.markFor(upstreamAddress));
      DPacket response = DPacket.from(us);
      response.message.getHeader().setID(request.getId());

      for (DResponsePlugin plugin : plugins) {
        response = plugin.apply(request, response);
      }
      if (log.isDebugEnabled()) {
        log.debug("Response: [{}]", response.message);
      }
      return response.markFor(request.packet.getSocketAddress());
    }));
  }

  public DProxy withPlugin(DResponsePlugin plugin) {
    plugins.add(plugin);
    return this;
  }
}
