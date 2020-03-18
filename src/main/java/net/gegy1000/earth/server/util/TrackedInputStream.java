package net.gegy1000.earth.server.util;

import net.minecraft.util.text.ITextComponent;

import java.io.IOException;
import java.io.InputStream;

public class TrackedInputStream extends InputStream {
    private final InputStream input;
    private ProgressTracker tracker;

    public TrackedInputStream(InputStream input) {
        this.input = input;
    }

    public TrackedInputStream submitTo(ITextComponent description, ProcessTracker processTracker) throws IOException {
        this.tracker = processTracker.push(description, this.input.available());
        return this;
    }

    @Override
    public int read() throws IOException {
        int read = this.input.read();
        if (read != -1) {
            this.trackBytes(1);
        }
        return read;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int count = this.input.read(b);
        this.trackBytes(count);
        return count;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = this.input.read(b, off, len);
        this.trackBytes(count);
        return count;
    }

    @Override
    public long skip(long n) throws IOException {
        long skipped = this.input.skip(n);
        this.trackBytes((int) skipped);
        return skipped;
    }

    @Override
    public int available() throws IOException {
        return this.input.available();
    }

    @Override
    public void close() throws IOException {
        this.input.close();
        if (this.tracker != null) {
            this.tracker.close();
        }
    }

    private void trackBytes(int count) {
        if (count <= 0 || this.tracker == null) return;
        this.tracker.step(count);
    }
}
