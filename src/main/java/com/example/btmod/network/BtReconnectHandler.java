package com.example.btmod.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.buffer.ByteBuf;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BtReconnectHandler extends ChannelInboundHandlerAdapter {
    private final Queue<ByteBuf> pendingPackets = new ConcurrentLinkedQueue<>();
    private boolean isReconnecting = false;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (!isReconnecting) {
            isReconnecting = true;
            System.out.println("[BT-Mod] Bağlantı koptu! 15 sn reconnect bekleniyor.");
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (isReconnecting && msg instanceof ByteBuf buf) {
            pendingPackets.add(buf.retain());
        } else {
            super.channelRead(ctx, msg);
        }
    }
}