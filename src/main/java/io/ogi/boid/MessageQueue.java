package io.ogi.boid;

import io.ogi.boid.model.BoidPositions;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueue {
    private static final MessageQueue INSTANCE = new MessageQueue();

    private final Queue<BoidPositions> queue = new ConcurrentLinkedQueue<>();

    public static MessageQueue instance() {
        return INSTANCE;
    }

    private MessageQueue() {
    }

    public void push(BoidPositions s) {
        queue.add(s);
    }


    public BoidPositions pop() {
//        System.out.println(queue.peek());
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }


    public BoidPositions peek() {
        return queue.peek();
    }
}
