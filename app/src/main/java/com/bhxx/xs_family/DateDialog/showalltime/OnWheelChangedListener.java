package com.bhxx.xs_family.DateDialog.showalltime;
/** 
 * @author fanxb 
 * @version 创建时间：2016-7-9 下午8:17:29 
 * 类说明 条目改变监听器
 */
public interface OnWheelChangedListener {

	/**
     * 当前条目改变时回调该方法
     * 
     * @param wheel
     *            条目改变的 WheelView 对象
     * @param oldValue
     *            WheelView 旧的条目值
     * @param newValue
     *            WheelView 新的条目值
     */
    void onChanged(WheelView wheel, int oldValue, int newValue);
}
