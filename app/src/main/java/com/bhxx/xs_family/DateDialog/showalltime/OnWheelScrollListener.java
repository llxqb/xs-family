package com.bhxx.xs_family.DateDialog.showalltime;
/**
 * @author fanxb 
 * @version 创建时间：2016-7-9 下午8:18:50 
 * 类说明 滚动监听器
 */
public interface OnWheelScrollListener {
	 /**
     * 在 WheelView 滚动开始的时候回调该接口
     * 
     * @param wheel
     *            开始滚动的 WheelView 对象
     */
    void onScrollingStarted(WheelView wheel);

    /**
     * 在 WheelView 滚动结束的时候回调该接口
     * 
     * @param wheel
     *            结束滚动的 WheelView 对象
     */
    void onScrollingFinished(WheelView wheel);
}
