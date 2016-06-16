package com.test.iwomag.android.pubblico.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * 模拟Http请求
 * 
 * @author zhangjian
 * 
 */
@SuppressWarnings("deprecation")
public class HtmlFetch {

	private static int timeout = 60000;
	private String baseUrl = null;
	public String Accept = "appliaction/json";
	public String UserAgent = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Trident/4.0; GTB6; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.5.30729; .NET CLR 3.0.30729; msn OptimizedIE8;ENUS)";

	public String getBaseUrl() {
		return baseUrl;
	}

	private HttpClient client = null;
	private String DefaultEncode = null;

	public String getDefaultEncode() {
		return DefaultEncode;
	}

	public void setDefaultEncode(String name) {
		DefaultEncode = name;
	}

	private String url = null;// 当前url

	public String getUrl() {
		return url;
	}

	private String content = null;// 获取的html代码
	private Boolean usePostMethod = false;// 使用post方式提交

	public void setUsePostMethod(Boolean usePostMethod) {
		this.usePostMethod = usePostMethod;
	}

	public String referer = "";

	private List<FilePart> files = new ArrayList<FilePart>();

	// f
	public String getContent() {
		return content;
	}

	private static int MAX301 = 3;
	private int curr301 = 0;// 当前301的次数

	public String getHtml(String sUrl) throws HttpException, IOException {
		Logger.i("HtmlFetch url = " + sUrl);
		content = getData(getByteArray(getOutputStream(getStream(sUrl))));
		return content;
	}

	/**
	 * 添加需要上传的文件
	 * 
	 * @param fieldName
	 * @param fileName
	 */
	public void addFile(String fieldName, String fileName) {
		if(com.test.iwomag.android.pubblico.util.StringUtil.isEmpty(fileName))return;
		try {
			File file = new File(fileName);
			if(file.exists()){
				Logger.i("文件存在");
			}else {
				Logger.i("文件不存在");
			}
			FilePart fp = new FilePart(fieldName, new File(fileName));
			Logger.i("fp=" + fp.toString());
			files.add(fp);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * public String[] getLinkUrls(){ if(content==null){ return null; } String[]
	 * urls =
	 * RegexUtil.getMatchValues(content,"href\\s*?=\\s*?['\"]?([^'\" ]+)",1);
	 * return StringUtil.url2absolute(urls, baseUrl); }
	 */
	/**
	 * 根据http的输出流,获取html页面源码
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	private String getData(byte[] bb) throws HttpException, IOException {

		String text = null;
		// ByteArrayOutputStream out = getOutputStream(is);

		if (bb != null) {
			String charset = DefaultEncode;
			if (DefaultEncode == null) {
				charset = "utf-8";
				DefaultEncode = charset;
			}
			text = new String(bb, charset);

			String v = getMatchValue(text.replaceAll("<[!]--(.*?)-->", ""), "<\\s*?meta.*?(?=charset).*?[>]", 0);
			if (v != null && !v.trim().equals("")) {
				charset = getMatchValue(v, "charset\\s*?=\\s*?([^ \"';>]+)\\s*?", 1);
				if (charset != null && !DefaultEncode.equals(charset)) {
					DefaultEncode = charset;
					text = new String(bb, charset);
				}
			}
			String bu = getMatchValue(text.replaceAll("<[!]--(.*?)-->", ""), "<base \\s*?href\\s*?=\\s*?['\"]?(http[^'\" ]+)", 1);
			if (bu == null || bu.trim().equals("")) {
				baseUrl = url;
			} else {
				baseUrl = bu;
			}
			String url301 = getMatchValue(text.replaceAll("<[!]--(.*?)-->", ""), "<meta.*(?=http-equiv[=]\"Refresh\")(.*?)>", 0);
			if (url301 != null && !url301.trim().equals("")) {
				url301 = getMatchValue(url301, "content\\s*?=\\s*?['\"]0\\s*?;\\s*?url=([^'\">]+)", 1);
				if (url301 != null && !url301.trim().equals("")) {

					url301 = StringUtil.url2absolute(url301, baseUrl != null ? baseUrl : url);
					return getData(getByteArray(getOutputStream(getStream(url301))));
				}

			}

		}

		return text;
	}

	/**
	 * 使用get方式访问一下某个地址。 获取请求的状态即可
	 * 
	 * @param sUrl
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public int visitedurl(String sUrl) throws HttpException, IOException {

		int status = 0;
		try {
			getHtml(sUrl);
		} catch (java.lang.Exception ex) {
			return 0;
		} finally {

		}
		return status;
	}

	/**
	 * 获取指定url的返回的流
	 * 
	 * @param sUrl
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public InputStream getStream(String sUrl) throws HttpException, IOException {
		this.url = sUrl;
		curr301++;
		if (curr301 >= MAX301) {
			content = "";
			return null;
		}
		try {
			@SuppressWarnings("unused")
			URL u = new URL(sUrl);
		} catch (MalformedURLException e1) {
			return null;
		}

		int status = 0;
		if (client == null) {
			HttpClientParams p = new HttpClientParams();
			p.setSoTimeout(timeout);
			p.setParameter(HttpMethodParams.USER_AGENT, UserAgent);
			p.setParameter("Accept", Accept);
			client = new HttpClient(p);
		}

		HttpMethodBase method = null;

		if (files.size() > 0) {
			usePostMethod = true;
		}
		if (usePostMethod) {
			int j = sUrl.indexOf("?");
			if (j != -1) {
				// Logger.i(sUrl.substring(0, j));

				String[] arr = sUrl.substring(j + 1).replaceAll("&amp;", "&").split("&");
				NameValuePair[] nvps = null;
				if (arr != null && arr.length > 0) {
					nvps = new NameValuePair[arr.length];
					String[] ar = null;
					String s = null;
					String k = "";
					String v = "";
					for (int i = 0; i < arr.length; i++) {
						s = arr[i];
						if (s != null) {
							ar = s.split("=");
							if (ar != null) {
								if (ar.length == 0) {
									continue;
								}
								k = ar.length > 0 ? ar[0] : "";
								v = ar.length > 1 ? ar[1] : "";
								Logger.i(k + "=" + v);
								if (k != null && !k.trim().equals(""))
									nvps[i] = new NameValuePair(k, v);
							}
						}
					}
				}

				if (files.size() > 0) {
					try {
						method = new MultipartPostMethod(sUrl.substring(0, j));
					} catch (java.lang.IllegalArgumentException ex) {
						return null;
					}

					if (nvps != null) {
						for (NameValuePair nvp : nvps) {
							if(nvp != null)
							((MultipartPostMethod) method).addParameter(nvp.getName(), nvp.getValue());
						}
					}
					for (FilePart fp : files) {
						((MultipartPostMethod) method).addPart(fp);
					}
				} else {
					try {
						method = new PostMethod(sUrl.substring(0, j));
					} catch (java.lang.IllegalArgumentException ex) {
						return null;
					}

					if (nvps != null)
						((PostMethod) method).addParameters(nvps);
					// ((PostMethod) method).setRequestBody(nvps);
				}

			}
		} else {
			try {
				method = new GetMethod(sUrl);
			} catch (java.lang.IllegalArgumentException ex) {
				return null;
			}
		}

		InputStream is = null;

		Logger.i("htmlFetch=" + sUrl);
		method.setRequestHeader(new Header("Referer", referer));
		status = client.executeMethod(method);
		if (status == HttpStatus.SC_OK) {
			is = method.getResponseBodyAsStream();
		} else if (status == HttpStatus.SC_MOVED_PERMANENTLY || status == HttpStatus.SC_MOVED_TEMPORARILY) {
			// 301 302
			Header h = method.getResponseHeader("location");
			String location = null;
			if (h != null) {
				location = h.getValue();
			}
			if (location == null || location.trim().equals("")) {
			}
			if (location != null && !location.trim().equals("")) {
				location = StringUtil.url2absolute(location, baseUrl != null ? baseUrl : sUrl);
				return getStream(location);
			}

		}

		curr301 = 0;
		files.clear();
		usePostMethod = false;
		return is;
	}

	public ByteArrayOutputStream getOutputStream(InputStream is) throws IOException {
		ByteArrayOutputStream out = null;

		if (is != null) {
			out = new ByteArrayOutputStream(1024);
			byte[] b = new byte[1024];
			int nRead;
			while ((nRead = is.read(b, 0, 1024)) > 0) {
				out.write(b, 0, nRead);
			}
		}

		return out;
	}

	/**
	 * 从ByteArrayOutputStream读取字节数组
	 * 
	 * @param out
	 * @return
	 */
	public byte[] getByteArray(ByteArrayOutputStream out) {
		if (out != null) {
			return out.toByteArray();
		}
		return null;
	}

	private static String getMatchValue(String source, String regex, int groupIndex) {

		Matcher m = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(source);
		if (m.find()) {
			return m.group(groupIndex);
		}
		return null;
	}
}
