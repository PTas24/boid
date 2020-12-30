package io.ogi.examples.model;

import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class BoidMessage<T> implements Message<T> {

  boolean acked = false;
  T payload;

  public BoidMessage(T payload) {
    this.payload = payload;
  }

  public static <T> T of(T bp) {
    return bp;
  }

  boolean acked() {
    return acked;
  }

  @Override
  public T getPayload() {
    return payload;
  }

  @Override
  public CompletionStage<Void> ack() {
    acked = true;
    return CompletableFuture.completedStage(null);
  }
}
