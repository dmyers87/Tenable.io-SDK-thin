package com.tenable.io.api.session.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Session {
  private String id;
  private String username;
  private String email;
  private String name;
  private String type;
  private int permissions;
  private int lastLogin;
  private int containerId;
  private String[] groups;
}
