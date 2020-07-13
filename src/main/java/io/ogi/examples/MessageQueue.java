package io.ogi.examples;

import io.ogi.examples.model.BoidPosition;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueue {
    private static final MessageQueue INSTANCE = new MessageQueue();

    private final Queue<BoidPosition> queue = new ConcurrentLinkedQueue<>();

    public static MessageQueue instance() {
        return INSTANCE;
    }

    private MessageQueue() {
    }

    public void push(BoidPosition s) {
        queue.add(s);
    }


    public BoidPosition pop() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }


    public BoidPosition peek() {
        return queue.peek();
    }
}
