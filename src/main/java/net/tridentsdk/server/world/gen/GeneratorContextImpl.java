/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2016 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tridentsdk.server.world.gen;

import net.tridentsdk.base.Substance;
import net.tridentsdk.server.world.ChunkSection;
import net.tridentsdk.world.gen.GenContainer;
import net.tridentsdk.world.gen.GeneratorContext;

import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;

/**
 * Implementation of a generator context.
 */
@ThreadSafe
public class GeneratorContextImpl implements GeneratorContext {
    /**
     * The container for running generator tasks in this
     * context
     */
    private final GenContainer container;
    /**
     * The count of threads active for termination used
     * for termination signalling
     */
    private final LongAdder count = new LongAdder();
    /**
     * Flag for determining the first round in which the
     * count will be decremented to ensure that the task
     * completes thoroughly.
     */
    private final AtomicBoolean firstGo = new AtomicBoolean(true);

    /**
     * The seed to be used for generation
     */
    private final long seed;
    /**
     * The last random value, used for the PRNG generator
     */
    private final AtomicLong random;
    /**
     * List of chunk sections
     */
    private final AtomicReferenceArray<ChunkSection> sections = new AtomicReferenceArray<>(16);

    /**
     * Creates a new generator context with the given seed
     * as the starting random.
     *
     * @param seed the seed
     */
    public GeneratorContextImpl(GenContainer container, long seed) {
        this.container = container;
        this.seed = seed;
        this.random = new AtomicLong(seed);
        this.count.increment();
    }

    /**
     * Obtains the collection of chunk sections that were
     * generated by the context as an array.
     *
     * @return the array of chunk sections
     */
    public ChunkSection[] asArray() {
        ChunkSection[] sections = new ChunkSection[16];
        for (int i = 0; i < this.sections.length(); i++) {
            sections[i] = this.sections.get(i);
        }

        return sections;
    }

    @Override
    public long nextLong() {
        while (true) {
            long l = this.random.get();

            long x = l;
            x ^= (x << 21);
            x ^= (x >>> 35);
            x ^= (x << 4);

            if (x != 0 && this.random.compareAndSet(l, x)) {
                return x;
            }
        }
    }

    @Override
    public long nextLong(long max) {
        return this.nextLong() % max;
    }

    @Override
    public int nextInt() {
        return this.nextInt(Integer.MAX_VALUE);
    }

    @Override
    public int nextInt(int max) {
        return (int) this.nextLong() % max;
    }

    @Override
    public long seed() {
        return this.seed;
    }

    @Override
    public void set(int x, int y, int z, Substance substance, byte meta) {
        this.set(x, y, z, build(substance.id(), meta));
    }

    @Override
    public void set(int x, int y, int z, Substance substance) {
        this.set(x, y, z, build(substance.id(), (byte) 0));
    }

    @Override
    public void set(int x, int y, int z, int id, byte meta) {
        this.set(x, y, z, build(id, meta));
    }

    @Override
    public void run(Runnable r) {
        this.count.increment();
        this.container.run(() -> {
            r.run();

            if (this.firstGo.compareAndSet(true, false)) {
                this.count.add(-2);
            } else {
                this.count.decrement();
            }
        });
    }

    /**
     * Checks to see whether all of the tasks submitted to
     * this context has finished.
     *
     * @return {@code true} if done
     */
    public boolean isDone() {
        boolean b = this.firstGo.get();
        int i = this.count.intValue();

        return b ? i == 1 : i == 0;
    }

    /**
     * Sets the block at the given coordinates to the given
     * block state value.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @param state the block to set
     */
    private void set(int x, int y, int z, short state) {
        int sectionIdx = section(y);
        ChunkSection section = this.sections.get(sectionIdx);
        if (section == null) {
            ChunkSection newSec = new ChunkSection();
            // if we end up with no chunk section
            // try to cas null -> newsec
            if (this.sections.compareAndSet(sectionIdx, null, newSec)) {
                // if we win the race, we use the same sec
                section = newSec;
            } else {
                // if we lose the race, retry
                section = this.sections.get(sectionIdx);
            }
        }

        int idx = idx(x, y & 15, z);
        section.set(idx, state);
    }

    /**
     * Builds the given block state given the ID number and
     * the metadata value.
     *
     * @param id the block ID
     * @param meta the block meta
     * @return the block state
     */
    // short is perfect for storing block data because
    // short = 2 bytes = 16 bits
    // 8 bit block id
    // 4 bit meta
    // 4 bit add (unused)
    // ------------------
    // 16 bits
    private static short build(int id, byte meta) {
        return (short) (id << 4 | meta);
    }

    /**
     * http://minecraft.gamepedia.com/Chunk_format
     * int BlockPos = y*16*16 + z*16 + x;
     *
     * return (y * (2^8)) + (z * (2^4)) + x;
     * use OR instead because bitwise ops are faster and
     * provides the same results as addition
     *
     * max size of this array is blocks in section, 4096
     * 16*16*16
     */
    private static int idx(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    /**
     * Obtains the section number for the given Y value.
     *
     * @param y the y value
     * @return the section number for that Y value
     */
    private static int section(int y) {
        return y / 16;
    }
}