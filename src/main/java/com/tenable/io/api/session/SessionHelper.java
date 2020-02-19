package com.tenable.io.api.session;

import com.tenable.io.api.ApiHelperBase;
import com.tenable.io.api.TenableIoClient;
import com.tenable.io.api.session.model.Session;
import com.tenable.io.core.exceptions.TenableIoException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SessionHelper extends ApiHelperBase {
  private TenableIoClient client;

  /**
   * Instantiates a new Session helper.
   *
   * @param client the client
   */
  public SessionHelper( TenableIoClient client ) {
    this.client = client;
    path = client.getExportsApi().getUriString("/session");
  }

  public Session validate() throws TenableIoException {
    return this.client.getSessionApi().getActiveSession();
  }
}
