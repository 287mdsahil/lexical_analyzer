package utils;

import java.io.IOException;
import java.io.InputStreamReader;

public class Buffer {
    public static final int DEFAULT_SIZE = 512;

    private int startPtr, endPtr, currEndPtr, size;
    private int[] buffer;
    private InputStreamReader isr;

    public Buffer(InputStreamReader isr) {
        startPtr = endPtr = currEndPtr = size = 0;
        buffer = new int[DEFAULT_SIZE];
        this.isr = isr;
    }

    public int get() throws IOException {
        int ch;

        if (currEndPtr != endPtr) {
            ch = buffer[currEndPtr];
            currEndPtr = moduloAdd(currEndPtr, 1, buffer.length);
            size++;
            return ch;
        }
        
        if (size == buffer.length)
            throw new IllegalStateException("Buffer size exceeded");

        ch = isr.read();
        if (ch == -1)
            return -1;

        buffer[endPtr] = ch;
        endPtr = moduloAdd(endPtr, 1, buffer.length);
        currEndPtr = moduloAdd(currEndPtr, 1, buffer.length);
        size++;

        return ch;
    }

    public int consume() {
        if (size == 0)
            throw new IllegalStateException("Consuming from empty buffer");

        int ch = buffer[startPtr];
        startPtr = moduloAdd(startPtr, 1, buffer.length);
        size--;
        
        return ch;
    }

    public String consume(int num) {
        if (num > size)
            throw new IllegalArgumentException("Not enough items in buffer");
        
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < num; i++) {
            temp.append((char) consume());
        }

        return temp.toString();
    }

    public int size() {
        return size;
    }

    public void reset() {
        currEndPtr = startPtr;
        size = 0;
    }

    private static int moduloAdd(int a, int b, int mod) {
        return ((a % mod) + (b % mod)) % mod;
    }
}
