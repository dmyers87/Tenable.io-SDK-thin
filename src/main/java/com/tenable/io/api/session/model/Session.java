package com.tenable.io.api.session.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

  public Map<String, String> toMap() {
    Map<String, String> sessionMap = new HashMap<>();
    sessionMap.put("id", this.id);
    sessionMap.put("username", this.username);
    sessionMap.put("email", this.email);
    sessionMap.put("name", this.name);
    sessionMap.put("type", this.type);
    sessionMap.put("permissions", String.valueOf(this.permissions));
    sessionMap.put("containerId", String.valueOf(this.containerId));
    sessionMap.put("groups", Arrays.toString(this.groups));

    return sessionMap;
  }
}
