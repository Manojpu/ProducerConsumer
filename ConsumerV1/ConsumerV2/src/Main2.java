import java.util.LinkedList;
import java.util.Queue;

class Data {
    private final Queue<String> q;
    private final int capacity;

    Data(int cap) {
        q = new LinkedList<>();
        capacity = cap;
    }

    public void publish(String msg) {
        synchronized (this) {
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
    }

    public void consume() {
        synchronized (this) {
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
}

class Producer implements Runnable {
    Data data;

    public Producer(Data data) {
        this.data = data;
    }

    final String[] messages = {"Hello!!", "How are u darling!!", "I am crazy!", "What's about you?!!", "That's very gorgoeus!!"};

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

public class Main2{
    public static void main(String[] args) {
        Data data = new Data(5);
        Thread producer = new Thread(new Producer(data), "producer");
        Thread consumer = new Thread(new Consumer(data), "consumer");
        producer.start();
        consumer.start();
    }
}