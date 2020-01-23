package io.vacco.dns;

import org.xbill.DNS.Message;
import java.io.IOException;
import java.net.*;

public class DPacket {

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

  public static DPacket from(DatagramSocket socket) {
    byte[] data = new byte[MAX_SIZE];
    DPacket r = new DPacket();
    r.packet = new DatagramPacket(data, 0, data.length);
    try {
      socket.receive(r.packet);
      r.message = new Message(r.packet.getData());
      return r;
    } catch (IOException e) { throw new IllegalStateException(e); }
  }
}
