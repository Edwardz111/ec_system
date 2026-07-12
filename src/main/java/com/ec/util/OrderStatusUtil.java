package com.ec.util;

/**
 * 订单状态：下单即为已完成，不参与业务流程判断。
 */
public final class OrderStatusUtil {

    public static final String COMPLETED = "COMPLETED";

    private OrderStatusUtil() {}

    public static String getStatusText() {
        return "已完成";
    }

    /** 下单即完成，均可评价 */
    public static boolean canReview() {
        return true;
    }
}
