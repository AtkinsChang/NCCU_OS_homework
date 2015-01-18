package edu.nccu.plsm.osproject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.Scanner;

public class Simple {

    // 不重要
    private static final Logger LOGGER = LoggerFactory.getLogger(Simple.class);
    // monitor
    private static final Object MONITOR = new Object();
    // 產生隨機數以現在時間為seed
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static final byte STRING_BEGIN = Byte.MAX_VALUE;
    private static final byte STRING_END = Byte.MIN_VALUE;

    private static final int WAIT_TIME = 1000;
    private static final int MAX_PROCESS_COUNT = 5;


    public static void main(String[] args) throws IOException, InterruptedException {
        //讀使用者輸入buffer大小
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please input buffer size: ");
        int bufferSize = scanner.nextInt();
        LOGGER.info("Allocation buffer in size {}...", bufferSize);
        ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);


        LOGGER.info("Program start...");
        //建立thread object
        Thread consumer = new Thread(new Consumer(buffer));
        //設定thread 名稱
        consumer.setName("consumer");
        //執行thread
        consumer.start();

        //同上
        Thread producer = new Thread(new Producer(buffer));
        producer.setName("producer");
        producer.start();

        //join
        consumer.join();
        producer.join();
    }

    private static class Producer implements Runnable {

        private final ByteBuffer buffer;

        Producer(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    //讀使用者輸入字串
                    System.out.print("Please input a string: ");
                    String string = scanner.next();
                    LOGGER.info("Get user input {}", string);

                    //string => char
                    char[] chars = string.toCharArray();
                    // begin token + 16 bit per char + end token
                    byte[] data = new byte[chars.length * Character.SIZE + 2];
                    data[0] = STRING_BEGIN;
                    data[data.length - 1] = STRING_END;
                    // char to bit
                    for (int i = 0; i < chars.length; i++) {
                        // 1~16bit
                        for (int j = 0; j < Character.SIZE; j++) {
                            data[1 + i * Character.SIZE + j] = (byte) ((chars[i] >> j) & 0x1);
                        }
                    }
                    String bits = "";
                    for (int i = 0; i < data.length; i++) {
                        switch (data[i]) {
                            case STRING_BEGIN:
                                bits += "{BEGIN}";
                                break;
                            case STRING_END:
                                bits += "{END}";
                                break;
                            default:
                                bits += data[i];
                        }
                    }
                    LOGGER.info("Bits: {}", bits);
                    LOGGER.info("Total {} bits to put to buffer", data.length);

                    for (int i = 0; i < data.length; ) {
                        //決定這次要寫完sleep多久
                        int sleep = RANDOM.nextInt(WAIT_TIME);
                        Thread.sleep(sleep);
                        //決定這次要寫多少bit
                        int target = RANDOM.nextInt(MAX_PROCESS_COUNT) + 1;
                        int count = 0;
                        //括起來的地方monitor
                        synchronized (MONITOR) {
                            //如果buffer是滿的就..
                            if (buffer.position() == buffer.limit()) {
                                MONITOR.wait();
                            }
                            //寫資料如果： 沒有滿，小於target，還有資料要寫入
                            while (buffer.position() < buffer.limit() && count <= target && i < data.length) {
                                buffer.put(data[i++]);
                                count++;
                            }
                            //如果寫入的次數等於buffer的大小，寫就是說寫入前是空的就...
                            if (buffer.position() == count) {
                                MONITOR.notifyAll();
                            }
                            LOGGER.info("Put {} bit{} into buffer [{}/{}] => [{}/{}], now sleep {} ms",
                                    count, (count > 1 ? "s" : ""),
                                    buffer.position() - count, buffer.limit(),
                                    buffer.position(), buffer.limit(),
                                    sleep);
                            // 當寫入end token後等consumer讀完
                            if (i == data.length) {
                                MONITOR.wait();
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private static class Consumer implements Runnable {

        private final ByteBuffer buffer;

        Consumer(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            //暫存，因為 1 char = 2 bytes
            byte[] data = new byte[Character.SIZE];
            int index = 0;
            StringBuilder result = null;
            boolean print = false;
            try {
                while (true) {
                    //決定這次要讀完sleep多久
                    int sleep = RANDOM.nextInt(WAIT_TIME);
                    //決定這次要讀多少bit
                    int target = RANDOM.nextInt(MAX_PROCESS_COUNT) + 1;
                    int count = 0;
                    //括起來的地方monitor
                    synchronized (MONITOR) {
                        //如果空的
                        if (buffer.position() == 0) {
                            MONITOR.wait();
                        }
                        //flip，簡單說就是把可以讀取的資料找出來
                        buffer.flip();
                        String bits = "";
                        //如果： 還有資料，小於target
                        while (buffer.position() < buffer.limit() && count <= target) {
                            byte b = buffer.get();
                            switch (b) {
                                case STRING_BEGIN:
                                    print = false;
                                    result = new StringBuilder();
                                    bits += "{BEGIN}";
                                    break;
                                case STRING_END:
                                    print = true;
                                    bits += "{END}";
                                    break;
                                default:
                                    bits += data[index];
                                    data[index++] = b;
                                    if (index == data.length) {
                                        int sum = 0;
                                        for (int i = 0; i < Character.SIZE; i++) {
                                            sum += (data[i] & 0x1) << i;
                                        }
                                        result.append((char) sum);
                                        index = 0;
                                    }
                                    break;
                            }
                            count++;
                        }
                        LOGGER.info("Get: \"{}\"", bits);
                        LOGGER.info("Total {} bit{} from buffer [{}/{}] => [{}/{}], now sleep {} ms",
                                count, (count > 1 ? "s" : ""),
                                buffer.limit(), buffer.capacity(),
                                buffer.limit() - buffer.position(), buffer.capacity(),
                                sleep);

                        if (print) {
                            LOGGER.info("------------------------Result------------------------");
                            LOGGER.info("Got a string \"{}\"", result.toString());
                            LOGGER.info("------------------------------------------------------");
                            MONITOR.notifyAll();
                        }
                        //如果可以讀的資料是容量上限 也就是滿 就...
                        if (buffer.limit() == buffer.capacity()) {
                            MONITOR.notifyAll();
                        }
                        //compact簡單說就是找出沒有讀的資料
                        buffer.compact();
                    }
                    Thread.sleep(sleep);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
