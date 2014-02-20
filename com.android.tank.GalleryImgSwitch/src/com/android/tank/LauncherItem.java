
///******************************************************************************//
///**************************请尊重tank的成果毕竟这也是花了笔者很多时间和心思*****//
/*************************  为了让大家容易懂tank特地详细的写了很多的解释*********************************************////
///**************************欢迎访问我的博客http://www.cnblogs.com/tankaixiong/********************************************//
///***************************里面文章将持续更新！***************************************************//

package com.android.tank;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

public class LauncherItem {
	Drawable icon;
	String name;
	ComponentName component;

	LauncherItem(Drawable d, String s, ComponentName cn) {
		icon = d;
		name = s;
		component = cn;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ComponentName getComponent() {
		return component;
	}

	public void setComponent(ComponentName component) {
		this.component = component;
	}
	
};