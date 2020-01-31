package io.vacco.dns.impl;

import io.vacco.ufn.UFn;
import org.xbill.DNS.Message;
import java.net.*;

public class DnsPacket {

  public static final int MAX_SIZE = 1024;
  public DatagramPacket packet;
  public Message message;

  public int getId() { return message.getHeader().getID(); }

  public DatagramPacket markFor(SocketAddress target) {
    byte[] data = message.toWire();
    DatagramPacket packet = new DatagramPacket(data, data.length);
    packet.setSocketAddress(target);
    return packet;
  }

  public static DnsPacket from(DatagramSocket socket) {
    return UFn.tryRt(() -> {
      DnsPacket r = fromBuffer(new byte[MAX_SIZE]);
      socket.receive(r.packet);
      r.message = new Message(r.packet.getData());
      return r;
    });
  }

  public static DnsPacket fromMessage(Message m) {
    DnsPacket p = fromBuffer(m.toWire());
    p.message = m;
    return p;
  }

  public static DnsPacket fromBuffer(byte[] data) {
    DnsPacket r = new DnsPacket();
    r.packet = new DatagramPacket(data, 0, data.length);
    return r;
  }
}
