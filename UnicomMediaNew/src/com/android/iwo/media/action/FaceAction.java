package com.android.iwo.media.action;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iwo.media.apk.activity.R;
import com.test.iwomag.android.pubblico.adapter.IwoAdapter;
import com.test.iwomag.android.pubblico.encoder.BASE64Decoder;
import com.test.iwomag.android.pubblico.encoder.BASE64Encoder;
import com.test.iwomag.android.pubblico.util.FileCache;
import com.test.iwomag.android.pubblico.util.Logger;
import com.test.iwomag.android.pubblico.util.StringUtil;

public class FaceAction {

	private static Activity activity;
	private GridView gridView;
	private IwoAdapter adapter;
	private ArrayList<HashMap<String, String>> mData = new ArrayList<HashMap<String, String>>();
	private static Resources resources;
	private AssetManager assets;
	private EditText text;
	protected static DisplayMetrics dm;
	protected static float scale = 0;

	public boolean isLongClick = false;

	public FaceAction(Activity a, int n, GridView grid, EditText t) {
		activity = a;
		text = t;
		gridView = grid;
		init(n);
	}

	public FaceAction(Activity a) {
		activity = a;
		resources = activity.getResources();
		assets = activity.getAssets();
		dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		scale = resources.getDisplayMetrics().density;
	}

	private void init(int n) {
		assets = activity.getAssets();
		resources = activity.getResources();
		dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		scale = resources.getDisplayMetrics().density;
		String[] data = resources.getStringArray(R.array.face);
		for (int i = n; i < (n + 20) && (i < data.length); i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("key", "face/face_" + i + ".png");
			map.put("name", data[i]);
			mData.add(map);
		}
		HashMap<String, String> map1 = new HashMap<String, String>();
		map1.put("key", "face/del.png");
		map1.put("name", "[del]");
		mData.add(map1);
		adapter = new IwoAdapter(activity, mData) {
			@Override
			public View getView(int position, View v, ViewGroup parent) {
				if (v == null)
					v = mInflater.inflate(R.layout.face_item, parent, false);
				InputStream is = null;
				try {
					is = assets.open(mData.get(position).get("key"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				BitmapFactory.Options options = new BitmapFactory.Options();
				Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);

				ImageView image = (ImageView) v.findViewById(R.id.img);
				setImgSize(image, 7 * 20, 1, 7);
				image.setImageBitmap(bitmap);
				return v;
			}
		};

		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int n, long m) {
				String t = text.getText().toString();
				if ("[del]".equals(mData.get(n).get("name"))) {
					String del = "";
					if (t.length() >= 4 && t.endsWith("]")) {
						del = t.substring(t.length() - 4, t.length());
						Logger.i("del = " + del);
						t = t.substring(0, t.length() - 4);
					}
					text.setText(FaceAction.setEditView(activity, t));
				} else {
					text.setText(FaceAction.setEditView(activity, t
							+ mData.get(n).get("name")));
				}
				text.requestFocus();
				CharSequence te = text.getText();
				Logger.i("te instanceof Spannable" + (te instanceof Spannable));
				if (te instanceof Spannable) {
					Spannable spanText = (Spannable) te;
					Selection.setSelection(spanText, te.length());
				}
			}
		});
	}

	protected int setImgSize(ImageView item, int del, float size, int n) {
		int width = (dm.widthPixels - (int) (del * scale + 0.5f)) / n;
		android.view.ViewGroup.LayoutParams params = item.getLayoutParams();
		params.height = (int) (width * size);
		params.width = width;
		return params.height;
	}

	public static SpannableString setEditView(Activity a, String content) {
		dm = new DisplayMetrics();
		a.getWindowManager().getDefaultDisplay().getMetrics(dm);
		scale = resources.getDisplayMetrics().density;
		if (StringUtil.isEmpty(content))
			return new SpannableString("");
		SpannableString ss = new SpannableString(content);
		if (StringUtil.isEmpty(ss))
			return ss;
		if (!(content.contains("[") && content.contains("]")))
			return ss;
		int lenth = content.length();
		for (int i = 0; i < lenth; i++) {
			if ('[' == content.charAt(i) && (i + 3) < lenth
					&& (']' == content.charAt(i + 3))) {
				Drawable drawable = getAssets(a, content.substring(i, i + 4));
				if (drawable != null) {
					drawable.setBounds(0, 0, (int) (20 * scale),
							(int) (20 * scale));
					ImageSpan span = new ImageSpan(drawable,
							ImageSpan.ALIGN_BOTTOM);
					ss.setSpan(span, i, i + 4,
							Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				}
			}
		}
		Logger.i("SpannableString=" + ss);
		return ss;
	}

	public SpannableString setTextView(final Activity a, final View view,
			String content) {
		dm = new DisplayMetrics();
		a.getWindowManager().getDefaultDisplay().getMetrics(dm);
		scale = resources.getDisplayMetrics().density;
		if (StringUtil.isEmpty(content))
			return new SpannableString("");
		SpannableString ss = new SpannableString(content);
		if (StringUtil.isEmpty(ss))
			return ss;
		int lenth = content.length();

		String s = "-0123456789";
		String str = "";
		int n = 0;
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < lenth; i++) {
			if (s.contains("" + content.charAt(i))) {
				n++;
				str += content.charAt(i);
				if (i == lenth - 1)
					if (n >= 7)
						list.add(str);
			} else {
				if (n >= 7)
					list.add(str);
				n = 0;
				str = "";
			}
		}

		for (int i = 0; i < list.size(); i++) {
			final String t = list.get(i);
			View.OnClickListener l = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!isLongClick)
						setTel(a, view, t);
					isLongClick = false;
				}
			};

			int start = content.indexOf(t);
			int end = start + t.length();
			ss.setSpan(new Clickabled(l), start, end,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}

		for (int i = 0; i < lenth; i++) {
			if ('[' == content.charAt(i) && (i + 3) < lenth
					&& (']' == content.charAt(i + 3))) {

				Drawable drawable = getAssets(a, content.substring(i, i + 4));
				if (drawable != null) {
					drawable.setBounds(0, 0, (int) (20 * scale),
							(int) (20 * scale));
					ImageSpan span = new ImageSpan(drawable,
							ImageSpan.ALIGN_BOTTOM);
					ss.setSpan(span, i, i + 4,
							Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return ss;
	}

	/**
	 * 对手机号的操作
	 * 
	 * @param view
	 */
	private void setTel(final Context mContext, final View view,
			final String tel) {

		OnClickListener listener = new OnClickListener() {

			public void onClick(View v) {
				Intent intent = null;
				switch (v.getId()) {
				case R.id.chat_tel_call:
					intent = new Intent();
					intent.setAction("android.intent.action.DIAL");
					intent.setData(Uri.parse("tel:" + tel));
					mContext.startActivity(intent);
					break;
				case R.id.chat_tel_copy:
					chatCopy(mContext, tel);
					break;
				case R.id.chat_tel_add:
					intent = new Intent(Intent.ACTION_INSERT);
					intent.setType("vnd.android.cursor.dir/person");
					intent.setType("vnd.android.cursor.dir/contact");
					intent.setType("vnd.android.cursor.dir/raw_contact");

					intent.putExtra(ContactsContract.Intents.Insert.PHONE, tel);
					mContext.startActivity(intent);
					break;
				case R.id.chat_tel_cancle:
					break;
				default:
					break;
				}
				view.setVisibility(View.GONE);
			}
		};
		((TextView) view.findViewById(R.id.chat_tel)).setText(tel
				+ "可能是个手机号码，你可以");
		view.setVisibility(View.VISIBLE);
		view.findViewById(R.id.chat_tel_call).setOnClickListener(listener);
		view.findViewById(R.id.chat_tel_copy).setOnClickListener(listener);
		view.findViewById(R.id.chat_tel_cancle).setOnClickListener(listener);
		view.findViewById(R.id.chat_tel_add).setOnClickListener(listener);
	}

	/**
	 * 复制
	 * 
	 * @param tex
	 */
	public void chatCopy(final Context mContext, String content) {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mContext
					.getSystemService("clipboard");
			ClipData clip = ClipData.newPlainText("label", content);
			clipboard.setPrimaryClip(clip);

		} else {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mContext
					.getSystemService("clipboard");
			clipboard.setText(content);
		}
		Toast.makeText(mContext, "已复制", Toast.LENGTH_SHORT).show();
	}

	public class Clickabled extends ClickableSpan implements OnClickListener {
		private final View.OnClickListener mListener;

		public Clickabled(View.OnClickListener l) {
			mListener = l;
		}

		@Override
		public void onClick(View v) {
			mListener.onClick(v);
		}
	}

	/**
	 * 从assets文件夹下读取图片
	 * 
	 * @param a
	 * @param source
	 * @return
	 */
	private static Drawable getAssets(Activity a, String source) {
		String[] data = a.getResources().getStringArray(R.array.face);
		int n = data.length;
		String face = "";
		for (int i = 0; i < n; i++) {
			if (data[i].equals(source)) {
				face = "face/face_" + i + ".png";
				break;
			}
		}
		InputStream is = null;
		try {
			is = a.getAssets().open(face);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BitmapDrawable(is);
	}

	/**
	 * //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
	 * 
	 * @param path
	 * @return
	 */
	public static String GetImageStr(Context context, String path) {
		Logger.i("path" + path);
		if (StringUtil.isEmpty(path))
			return "";
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		String en = "";
		try {
			in = new FileInputStream(path);
			data = new byte[in.available()];
			in.read(data);
			in.close();
			// 对字节数组Base64编码
			BASE64Encoder encoder = new BASE64Encoder();
			en = encoder.encode(data);
			Logger.i("---解析=" + "---完");
		} catch (Exception e) {
			Logger.e("解析错误=" + e.toString());
			// IwoToast.makeText(context, "读取SD卡失败");
		} catch (Error e) {
			Logger.e("解析错误=" + e.toString());
		}
		return en;// 返回Base64编码过的字节数组字符串
	}

	/**
	 * 
	 * @param imgStr
	 * @param type
	 *            其余.图片。4.语音
	 * @return
	 */
	public static String GenerateImage(String imgStr, String type) {// 对字节数组字符串进行Base64解码并生成图片
		// System.out.print("type = " + type + "img = " + imgStr);
		if (imgStr == null) // 图像数据为空
			return "";
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			// Base64解码
			byte[] b = decoder.decodeBuffer(imgStr);
			for (int i = 0; i < b.length; ++i) {
				if (b[i] < 0) {// 调整异常数据
					b[i] += 256;
				}
			}
			// 生成jpeg图片
			String path = FileCache.getInstance().CACHE_PATH + "/";
			if ("3".equals(type)) {
				path += UUID.randomUUID().toString() + ".mp3";
			} else {
				path += UUID.randomUUID().toString() + ".jpg";
			}
			OutputStream out = new FileOutputStream(path);
			out.write(b);
			out.flush();
			out.close();
			Logger.i("路径---" + path);
			return path;
		} catch (Exception e) {
			Logger.e("base 64 解码错误" + e.toString());
		}
		return "";
	}

	/**
	 * 压缩图片
	 * 
	 * @param bitmap
	 *            源图片
	 * @param width
	 *            想要的宽度
	 * @param height
	 *            想要的高度
	 * @param isAdjust
	 *            是否自动调整尺寸, true图片就不会拉伸，false严格按照你的尺寸压缩
	 * @return Bitmap
	 */
	public static Bitmap reduce(Bitmap bitmap, int width, int height) {
		// 如果想要的宽度和高度都比源图片小，就不压缩了，直接返回原图
		if (bitmap.getWidth() < width && bitmap.getHeight() < height) {
			return bitmap;
		}
		// 根据想要的尺寸精确计算压缩比例, 方法详解：public BigDecimal divide(BigDecimal divisor,
		// int scale, int roundingMode);
		// scale表示要保留的小数位, roundingMode表示如何处理多余的小数位，BigDecimal.ROUND_DOWN表示自动舍弃
		float sx = new BigDecimal(width).divide(
				new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN)
				.floatValue();
		float sy = new BigDecimal(height).divide(
				new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN)
				.floatValue();
		// 如果想自动调整比例，不至于图片会拉伸
		sx = (sx < sy ? sx : sy);
		sy = sx;// 哪个比例小一点，就用哪个比例

		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy);// 调用api中的方法进行压缩，就大功告成了
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	/**
	 * 正则表达式截取字符串
	 * 
	 * public static String getMessagePar(String s, String from, String to){
	 * String regex = ".+?" + from + "(.+?)" + to+ ".+?";
	 * System.out.println(regex); Pattern pattern = Pattern.compile(regex);
	 * Matcher matcher = pattern.matcher(s); if (matcher.matches()) { String
	 * group = matcher.group(1); System.out.println(group); return group; }else
	 * { System.out.println("no matches!!"); return ""; } }
	 */
}
