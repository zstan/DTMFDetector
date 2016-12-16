package org.jcodec.containers.mp4.boxes;
import org.junit.Assert;
import org.junit.Test;

import java.lang.System;
import java.nio.ByteBuffer;

public class TrunBoxTest {

    @Test
    public void testReadWriteCreate() {
//@formatter:off
        Box box = TrunBox
                .create(2)
                .dataOffset(20)
                .sampleCompositionOffset(new int[] { 11, 12 })
                .sampleDuration(new int[] { 15, 16 })
                .sampleFlags(new int[] { 100, 200 })
                .sampleSize(new int[] { 30, 40 }).create();
//@formatter:on
        System.out.println(box.toString());
        ByteBuffer bb = ByteBuffer.allocate(1024);
        box.write(bb);
        bb.flip();

        TrunBox parsed = TrunBox.createTrunBox();
        bb.getLong(); // box header
        parsed.parse(bb);

        Assert.assertEquals(box.toString(), parsed.toString());

    }
}
