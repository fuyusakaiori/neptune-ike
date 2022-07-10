package org.nep.demo.snowflake.exception;

/**
 * <h3>时钟回拨异常</h3>
 */
public class TimeBackWard extends RuntimeException {
    public TimeBackWard(String message) {
        super(message);
    }
}
