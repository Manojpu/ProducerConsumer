import java.util.LinkedList;
import java.util.Queue;

class Data {
    Queue<String> q;
    int capacity;

    Data(int cap) {
        q = new LinkedList<>();
        capacity = cap;
    }

    public synchronized void publish(String msg) {
        while (q.size() == capacity) {
            try {
                System.out.println(Thread.currentThread().getName() + " waiting for message to be consumed...");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        q.add(msg);
        System.out.println("Message published:: " + msg);
        System.out.println("Queue: " + q);
        System.out.println();
        notifyAll();
    }

    public synchronized void consume() {
        while (q.isEmpty()) {
            try {
                System.out.println(Thread.currentThread().getName() + " waiting for new message...");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        String msg = q.poll();
        System.out.println(Thread.currentThread().getName() + " has consumed msg:: " + msg);
        System.out.println("Queue: " + q);
        System.out.println();
        notifyAll();
    }
}

class Producer implements Runnable {
    Data data;

    public Producer(Data data) {
        this.data = data;
    }

    final String[] messages = {"Hi!!", "How are you!!", "I love you!", "What's going on?!!", "That's really funny!!"};

    @Override
    public void run() {
        int i = 0;
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000);
                data.publish(messages[i]);
                i = (i + 1) % messages.length;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer implements Runnable {
    Data data;

    public Consumer(Data data) {
        this.data = data;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(2000);
                data.consume();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Data data = new Data(5);
        Thread producer = new Thread(new Producer(data), "producer");
        Thread consumer = new Thread(new Consumer(data), "consumer");
        producer.start();
        consumer.start();
    }
}