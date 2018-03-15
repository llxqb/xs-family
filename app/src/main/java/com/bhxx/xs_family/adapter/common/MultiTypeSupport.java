package com.bhxx.xs_family.adapter.common;

/**
 * 适配器中加载多种布局使用
 * @类名称：MultiTypeSupport
 * @类描述：根据列表的内容不同定义
 * 封装：对数据只暴露能访问的接口，防止修改里面的数据
 */
public interface MultiTypeSupport<T>{
	
	int getViewTypeCount();
	
	int getItemViewType(int position, T data);
	
	int getLayoutId(int viewType);
}
