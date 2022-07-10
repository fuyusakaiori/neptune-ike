package org.nep.demo;

import org.nep.demo.snowflake.SnowFlake;

import java.util.UUID;

public class SnowFlakeTest {

    public static void main(String[] args) {
        SnowFlake snowFlake = new SnowFlake(1, 1);
        // 1. 雪花算法耗时
        long snowFlakeStart = System.currentTimeMillis();
        for (int index = 0; index < 10000; index++) {
            System.out.println(snowFlake.generate());
        }
        System.out.println(System.currentTimeMillis() - snowFlakeStart);
        // 2. UUID 耗时
        long uuidStart = System.currentTimeMillis();
        for (int index = 0; index < 10000; index++) {
            System.out.println(UUID.randomUUID());
        }
        System.out.println(System.currentTimeMillis() - uuidStart);
    }

}
