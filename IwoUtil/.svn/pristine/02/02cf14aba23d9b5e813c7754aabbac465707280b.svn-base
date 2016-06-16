package com.test.iwomag.android.pubblico.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class FileCache {

	public String CACHE_PATH = "";
	public static FileCache fileCache = null;

	public static FileCache getInstance(){
		if(fileCache == null)
			fileCache = new FileCache();
		return fileCache;
	}
	public FileCache() {
		String path = Environment.getExternalStorageDirectory() + "/IWOMAG/CACHE_MEDIA";
		CACHE_PATH = path;
		createFile();
	}

	public boolean isCanCache() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	private void createFile() {
		if (!isCanCache())
			return;
		File file = new File(CACHE_PATH);

		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public void onCreateBitmap(String name, Bitmap bitmap) {
		if (!isCanCache())
			return;
		if (bitmap == null)
			return;
		File file = new File(CACHE_PATH + "/" + onSettingName(name));

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (bitmap != null)
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			try {
				fOut.flush();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onCreateBitmap(String name, SoftReference<Bitmap> bitmap) {
		if (!isCanCache())
			return;
		if (bitmap.get() == null)
			return;
		File file1 = new File(CACHE_PATH );
		if(!file1.exists())
			file1.mkdirs();
		File file = new File(CACHE_PATH + "/" + onSettingName(name));

		if (!file.exists()) {
			try {
				
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			FileOutputStream fOut = null;
			try {
				fOut = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (bitmap.get() != null)
				bitmap.get().compress(Bitmap.CompressFormat.PNG, 100, fOut);
			try {
				fOut.flush();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void clearCache() {
		if (!isCanCache())
			return;
		String path = CACHE_PATH + "/";
		File file = new File(path);
		Logger.i("cache file path");
		if (file.exists()) {
			File[] files = file.listFiles();
			long now = System.currentTimeMillis();
			long fileTime = 0;
			for (File f : files) {
				fileTime = f.lastModified();
				if ((now - fileTime) / (1000 * 60 * 60/** 24*3 */
				) > 0) {
					f.delete();
					// Logger.i("delete file " + f.getName());
				}
			}
		}
	}

	public Bitmap onGetBitmap(String name) {
		if (!isCanCache())
			return null;
		String path = CACHE_PATH + "/" + onSettingName(name);
		File file = new File(path);

		if (file.exists()) {
			SoftReference<Bitmap> bitmap = null;
			try {
				bitmap = new SoftReference<Bitmap>(BitmapFactory.decodeFile(path));// 文件流
			} catch (OutOfMemoryError e) {
				bitmap = null;
			}
			if(bitmap != null)
				return bitmap.get();
		}
		return null;
	}

	public String onSettingName(String name) {
		if (StringUtil.isEmpty(name))
			return name;
		String[] path = name.split("/");
		if (path.length == 0)
			return name;
		else {
			return path[path.length - 1];
		}
	}

	/**
	 * 指定目录写文件
	 * 
	 * @param filename
	 * @param value
	 */
	public void writeStringToSD(String filename, String value) {
		Logger.i("添加缓存文件" + value);
		if (!isCanCache())
			return;
		File file = null;

		OutputStream output = null;
		try {
			// 在创建 的目录上创建文件；
			file = new File(CACHE_PATH + "/" + filename);
			if (StringUtil.isEmpty(value)) {
				return;
			}
			Logger.i("file " + file.exists());
			if (file.exists())
				file.delete();
			Logger.i("file 1" + file.exists());
			file.createNewFile();
			Logger.i("file 2" + file.exists());
			output = new FileOutputStream(file);
			output.write(value.getBytes());
			// 刷新缓存，
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Logger.i("缓存文件添加完成");
	}

	/**
	 * 指定目录读文件
	 * 
	 * @param name
	 * @return
	 */
	public String readStringFormSD(String name) {
		if (!isCanCache())
			return "";
		StringBuffer sb = new StringBuffer();
		File file = new File(CACHE_PATH + "/" + name);
		Logger.i("file is exit" + file.exists());
		if (!file.exists())
			return "";
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
		} catch (Exception e) {
			Logger.e("读取缓存失败");
		}

		return sb.toString();
	}

	public boolean fileisexits(String name) {
		File file = new File(CACHE_PATH + "/" + name);
		return file.exists();
	}

	/**
	 * 添加缓存文件
	 * 
	 * @param filename
	 * @param value
	 */
	public void writeStringToSDCard(String filename, String value) {
		Logger.i("添加缓存文件");
		if (!isCanCache())
			return;
		File file = null;

		OutputStream output = null;
		try {
			// 在创建 的目录上创建文件；
			file = new File(CACHE_PATH + "/" + filename + ".txt");
			if (StringUtil.isEmpty(value)) {
				if (file.exists())
					file.delete();
				return;
			}
			if (!file.exists())
				file.createNewFile();
			output = new FileOutputStream(file);
			output.write(value.getBytes());
			// 刷新缓存，
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Logger.i("缓存文件添加完成");
	}

	/**
	 * 读取缓存文件
	 * 
	 * @param name
	 * @return
	 */
	public String readFileToString(String name) {
		if (!isCanCache())
			return null;
		StringBuffer sb = new StringBuffer();
		File file = new File(CACHE_PATH + "/" + name + ".txt");
		if (!file.exists())
			return null;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
}
