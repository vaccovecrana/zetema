package io.vacco.dns.impl;

import java.util.Objects;

public class DQuery {

  private DPacket request;
  private DPacket response;

  public DPacket getRequest() { return request; }
  public DQuery withRequest(DPacket request) {
    this.request = Objects.requireNonNull(request);
    return this;
  }

  public DPacket getResponse() { return response; }
  public DQuery withReponse(DPacket response) {
    this.response = Objects.requireNonNull(response);
    return this;
  }
}
