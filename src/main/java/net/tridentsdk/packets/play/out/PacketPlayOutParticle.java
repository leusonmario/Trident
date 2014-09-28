/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package net.tridentsdk.packets.play.out;

import io.netty.buffer.ByteBuf;
import net.tridentsdk.api.Location;
import net.tridentsdk.api.util.Vector;
import net.tridentsdk.server.netty.Codec;
import net.tridentsdk.server.netty.packet.OutPacket;

public class PacketPlayOutParticle extends OutPacket {

    protected int particleId;
    protected boolean distance;
    protected Location loc;
    protected Vector offset; // d - (d * Random#nextGaussian())
    protected float particleData;
    protected int[] data;

    @Override
    public int getId() {
        return 0x2A;
    }

    public int getParticleId() {
        return this.particleId;
    }

    public boolean isDistance() {
        return this.distance;
    }

    public Location getLoc() {
        return this.loc;
    }

    public Vector getOffset() {
        return this.offset;
    }

    public float getParticleData() {
        return this.particleData;
    }

    public int[] getData() {
        return this.data;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeInt(this.particleId);
        buf.writeBoolean(this.distance);

        buf.writeFloat((float) this.loc.getX());
        buf.writeFloat((float) this.loc.getY());
        buf.writeFloat((float) this.loc.getZ());

        buf.writeFloat((float) this.offset.getX());
        buf.writeFloat((float) this.offset.getY());
        buf.writeFloat((float) this.offset.getZ());

        buf.writeFloat(this.particleData);
        buf.writeInt(this.data.length);

        for (int i : this.data) {
            Codec.writeVarInt32(buf, i);
        }
    }
}
