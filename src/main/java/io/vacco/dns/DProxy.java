package io.vacco.dns;

import io.vacco.dns.plugin.DResponsePlugin;
import io.vacco.ufn.UFn;
import java.net.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class DProxy {

  private ThreadLocal<DatagramSocket> upstreamDs = ThreadLocal.withInitial(() -> UFn.tryRt(DatagramSocket::new));
  private final InetSocketAddress upstreamAddress;
  private final List<DResponsePlugin> plugins = new ArrayList<>();

  public DProxy(InetSocketAddress upstreamAddress) {
    this.upstreamAddress = Objects.requireNonNull(upstreamAddress);
  }

  public DProxy(String upstreamAddress, int upstreamPort) {
    this(new InetSocketAddress(upstreamAddress, upstreamPort));
  }

  public CompletableFuture<DatagramPacket> process(DPacket request) {
    return CompletableFuture.supplyAsync(() -> UFn.tryRt(() -> {
      DatagramSocket us = upstreamDs.get();
      us.send(request.markFor(upstreamAddress));
      DPacket response = DPacket.from(us);
      response.message.getHeader().setID(request.getId());

      for (DResponsePlugin plugin : plugins) {
        response = plugin.apply(request, response);
      }

      return response.markFor(request.packet.getSocketAddress());
    }));
  }

  public DProxy withPlugin(DResponsePlugin plugin) {
    plugins.add(plugin);
    return this;
  }
}
