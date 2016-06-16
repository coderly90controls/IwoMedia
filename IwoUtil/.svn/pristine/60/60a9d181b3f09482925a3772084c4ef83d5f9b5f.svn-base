package com.test.iwomag.android.pubblico.util;

import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

/**
 * TextView 中字体的样式的设置
 * @author DEV
 *
 */
public class TextUtil {

	/**
	 * 计算文字的大小
	 * 
	 * @return
	 */
	public static int getTextWidth(String text) {
		return (int) Layout.getDesiredWidth(text, 0, text.length(), new TextPaint());
	}

	/**
	 * 文字加下划线
	 */
	public static void setUnderLine(TextView text, String str) {
		SpannableStringBuilder spannable = new SpannableStringBuilder(str);
		CharacterStyle span = new UnderlineSpan();
		spannable.setSpan(span, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		text.setText(spannable);
	}
	
	/**
	 * 同一个textView下文字不同颜色
	 */
	public static void setTextColor(TextView textView, String[][] valueColor) {
		String html = "";
		for(int i=0; i<valueColor.length; i++){
			if(2==valueColor[i].length)
				html += "<font color='"+valueColor[i][1]+"'>"+valueColor[i][0]+"</font>";
			else if(3==valueColor[i].length)
				html += "<font size='" + valueColor[i][2] + "' color='"+valueColor[i][1]+"'>"+valueColor[i][0]+"</font>";
		}
		if(!StringUtil.isEmpty(html))
			textView.setText(Html.fromHtml(html));
	}
	/**
	 * 同一个textView下文字不同颜色
	 * 文字在前，颜色在后
	 */
	public static void setTextColor(TextView textView, String... valueColor) {
		Logger.i((valueColor == null) + "----" + valueColor.length);
		if(valueColor == null || valueColor.length%2!=0){
			return;
		}
		String html = "";
		for(int i=0; i<valueColor.length; i++){
				html += "<font color='"+valueColor[i+1]+"'>"+valueColor[i++]+"</font>";
		}
		if(!StringUtil.isEmpty(html))
			textView.setText(Html.fromHtml(html));
	}
	/*
	 * 四、同一个textView下文字不同颜色 SpannableStringBuilder
	 * style_title=newSpannableStringBuilder(titleStr); ForegroundColorSpan span
	 * =new
	 * ForegroundColorSpan(context.getResources().getColor(R.color.today_address
	 * )) style_title.setSpan(span,start,
	 * end,Spannable.SPAN_EXCLUSIVE_INCLUSIVE); 五、同一个textView下文字字体大小不同
	 * SpannableStringBuilder style_title=newSpannableStringBuilder(titleStr);
	 * AbsoluteSizeSpan span_2=newAbsoluteSizeSpan(12);//字体大小
	 * style_title.setSpan(span_2, start, end,
	 * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 六、添加删除线 SpannableString ss =new
	 * SpannableString(b.title); ss.setSpan(newStrikethroughSpan(), start, end,
	 * Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 七、添加图片主要用SpannableString和ImageSpan类：
	 * Drawable drawable = getResources().getDrawable(id);
	 * drawable.setBounds(0,0, drawable.getIntrinsicWidth(),
	 * drawable.getIntrinsicHeight()); //需要处理的文本，[smile]是需要被替代的文本
	 * SpannableString spannable =new
	 * SpannableString(getText().toString()+"[smile]");
	 * //要让图片替代指定的文字就要用ImageSpan ImageSpan span =new ImageSpan(drawable,
	 * ImageSpan.ALIGN_BASELINE); //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
	 * //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12 spannable.setSpan(span,
	 * getText().length(),getText().length()+"[smile]".length(),Spannable.
	 * SPAN_INCLUSIVE_EXCLUSIVE); setText(spannable);
	 */
}
