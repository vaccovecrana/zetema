package io.vacco.dns.impl;

import java.util.Objects;

public class DQuery {

  private DnsPacket request;
  private DnsPacket response;

  public DnsPacket getRequest() { return request; }
  public DQuery withRequest(DnsPacket request) {
    this.request = Objects.requireNonNull(request);
    return this;
  }

  public DnsPacket getResponse() { return response; }
  public DQuery withReponse(DnsPacket response) {
    this.response = Objects.requireNonNull(response);
    return this;
  }
}
