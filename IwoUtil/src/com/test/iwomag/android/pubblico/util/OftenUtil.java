package com.test.iwomag.android.pubblico.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Loader.ForceLoadContentObserver;

public class OftenUtil {
	/**
	 * listView是否有重复数据
	 * 
	 * @param list
	 * @return
	 */
	public static boolean removeDuplicate(List list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).equals(list.get(i))) {
					list.remove(j);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 比较listMap中的数据是否重复
	 * 
	 * @param list
	 * @return
	 */
	public static boolean compareListMapDuplicate(ArrayList<HashMap<String, String>> list, String... args) {
		for (int i = 0; i < list.size() - 1; i++) {
			int fold = 0;
			for (int j = list.size() - 1; j > i; j--) {
				Map mapA = list.get(j);
				Map mapB = list.get(i);
				for (String arg : args) {
					if (mapA.get(arg).equals(mapB.get(arg))) {
						list.remove(j);
						fold++;
					}
				}
				if (args.length == fold) {
					return true;
				}
			}
		}
		return false;
	}
}
