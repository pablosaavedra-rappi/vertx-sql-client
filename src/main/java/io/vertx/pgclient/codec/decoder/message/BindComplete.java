package io.vertx.pgclient.codec.decoder.message;

import io.vertx.pgclient.codec.Message;

/**
 * @author <a href="mailto:emad.albloushi@gmail.com">Emad Alblueshi</a>
 */

public class BindComplete implements Message {

  public static final BindComplete INSTANCE = new BindComplete();

  private BindComplete(){}

  @Override
  public String toString() {
    return "BindComplete{}";
  }
}
