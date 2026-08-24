package com.example.btmod.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.oio.OioByteStreamChannel;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothNettyChannel extends OioByteStreamChannel {
    private final InputStream in;
    private final OutputStream out;

    public BluetoothNettyChannel(InputStream in, OutputStream out) {
        super(null);
        this.in = in;
        this.out = out;
    }

    @Override
    protected int doReadBytes(ByteBuf buf) throws Exception {
        int available = Math.min(this.in.available(), buf.writableBytes());
        if (available > 0) {
            return buf.writeBytes(this.in, available);
        }
        return 0;
    }

    @Override
    protected void doWriteBytes(ByteBuf buf) throws Exception {
        buf.readBytes(this.out, buf.readableBytes());
        this.out.flush();
    }

    @Override
    protected InputStream availableInputStream() { return this.in; }
    @Override
    protected OutputStream availableOutputStream() { return this.out; }
    @Override
    public boolean isActive() { return true; }
}