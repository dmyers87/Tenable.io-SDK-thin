package com.tenable.io.api.session;

import com.tenable.io.api.ApiWrapperBase;
import com.tenable.io.api.session.model.Session;
import com.tenable.io.core.exceptions.TenableIoException;
import com.tenable.io.core.services.AsyncHttpService;
import com.tenable.io.core.services.HttpFuture;

public class SessionApi extends ApiWrapperBase {

  /**
   * Instantiates a new Session api.
   *
   * @param asyncHttpService the async http service
   * @param apiScheme        the api scheme
   * @param apiHost          the api host
   */
  public SessionApi( AsyncHttpService asyncHttpService, String apiScheme, String apiHost ){
    super(asyncHttpService, apiScheme, apiHost);
  }

  public Session getActiveSession() throws TenableIoException {
    HttpFuture httpFuture = asyncHttpService.doGet( createBaseUriBuilder( "/session" ).build() );
    return httpFuture.getAsType(Session.class);
  }
}
