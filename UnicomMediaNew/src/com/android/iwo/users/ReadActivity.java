package com.android.iwo.users;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.iwo.media.activity.BaseActivity;
import com.android.iwo.media.apk.activity.*;

public class ReadActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read);
		init();
	}

	private void init() {
		setBack(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
		setTitle("用户协议及隐私政策");
		String html = "<p><b><font size=8> 重要声明：</b></p>"
				+ "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=red>爱握视频客户端</font>版权归属<font color=red>北京精准沟通国际传媒广告有限公司</font>（简称<font color=red>“精准沟通”</font>或“我们”)。本软件是一款为移动设备提供简单易用的视频拍摄及编辑功能，让您轻松几步完成视频拍摄。本用户协议（包括隐私政策，以下同）是您（终端用户）和<font color=red>精准沟通</font>之间关于本网站和“爱握视频”相关事项最终的、完整的且排他的协议。本用户协议（简称“本协议”）将对您使用本网站和“爱握视频”的行为产生法律约束力。</p>"
				+ "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;请您审慎阅读并选择接受或不接受本协议（未成年人应在法定监护人陪同下阅读)。除非您接受本协议所有条款，否则您无权注册、登录或使用本协议所涉相关服务。您的注册、登录、使用等行为将视为对本协议的接受，并同意接受本协议各项条款的约束。</p>"
				+ "<p><b><font size=8>双方权利义务:</b></p>"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1. 本协议容许变更。如果本协议有任何实质性变更，我们将通过本网站的公告来通知您。变更通知之后，继续使用本网站和“爱握视频”则视为您已知晓此类变更并同意接受条款的约束。"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2. <font color=red>精准沟通</font>保留单方面对本服务的全部或部分内容在任何时候不经任何通知的情况下变更、暂停、限制、终止或撤销爱握视频服务的权利，终端用户需承担此风险。"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3. 用户不得利用爱握视频服务制作、上载、复制、发送如下内容:"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(1) 破坏宪法所确定的基本原则的;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(2) 危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(3) 损害国家荣誉和利益的;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(4) 煽动民族歧视，破坏民族团结的;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(5) 破坏国家宗教政策，宣扬邪教和封建迷信的;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(6) 散布谣言，扰乱社会秩序，破坏社会稳定的;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(5) 破坏国家宗教政策，宣扬邪教和封建迷信的;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(7) 散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(8) 侮辱或者诽谤他人，侵害他人合法权益的;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(5) 破坏国家宗教政策，宣扬邪教和封建迷信的;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(9) 含有法律、行政法规禁止的其他内容的信息。"
				+ "<p><b><font size=8>法律责任与免责声明:</b></p>"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1. 用户违反本协议或相关法律、法规、规章的规定，导致或产生的任何第三方主张的任何索赔、要求或损失，包括合理的律师费，用户同意赔偿<font color=red>精准沟通</font>，并使之免受损害。"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2. 如发生下述情形，<font color=red>精准沟通</font>不承担任何法律责任:"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(1)依据法律规定或相关政府部门的要求提供您的个人信息;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(2) 由于您的使用不当而导致任何个人信息的泄露;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(3) 因技术故障等不可抗事件影响到服务的正常运行的，<font color=red>精准沟通</font>承诺在第一时间内与相关单位配合，及时处理进行修复，但用户因此而遭受的一切损失，精准沟通不承担责任。"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(4) 任何由于黑客攻击，电脑病毒的侵入，非法内容信息、骚扰信息的屏蔽，政府管制以及其他任何网络、技术、通信线路、信息安全管理措施等原因造成的服务中断、受阻等不能满足用户要求的情形;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(5) 用户因第三方如运营商的通讯线路故障、技术问题、网络、电脑故障、系统不稳定及其他因不可抗力造成的损失的情形;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(6) 使用“爱握视频”可能存在的来自他人匿名或冒名的含有威胁、诽谤、令人反感或非法内容的信息而招致的风险;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(7) 用户之间通过“ 爱握视频”与其他用户交往，因受误导或欺骗而导致或可能导致的任何心理、生理上的伤害以及经济上的损失;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(8) 在任何情况下，精准沟通都不对您或任何第三方因本协议产生的任何间接性、后果性、惩戒性的、偶然的、特殊或惩罚性的损害赔偿承担责任。访问、使用本网站和 “爱握视频” 所产生的损坏计算机系统或移动通讯设备的风险将由您个人承担;"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(9) 含有法律、行政法规禁止的其它内容的信息。"
				+ "<p><b><font size=8>协议的生效与终止:</b></p>"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1. 本协议自您接受之日起生效 "
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2. 无论是否通知，我们保留在任何时间以任何原因终止本协议的权利，包括出于善意的相信您违反了使用政策或本协议的其他规定。"
				+ "<p><b><font size=8>其它规定:</b></p>"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1. 本协议所定的部分条款的无效，不影响其它条款的效力"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2. 本协议的解释、效力及纠纷的解决，适用于中华人民共和国法律。若用户和<font color=red>精准沟通</font>之间发生任何纠纷或争议，首先应友好协商解决，协商不成的，用户在此完全同意将纠纷或争议提交<font color=red>精准沟通</font>住所地有管辖权的人民法院管辖。"
				+ "<br/>"
				+ "<br/>联系邮箱:services@iwomedia.com"
				+ "<br/>北京精准沟通国际传媒广告有限公司"
				+ "<p align=RIGHT>2012年1月1日</p>"
				+ "<p><b><font size=10>隐私政策</b></p>"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color=red>爱握视频客户端</font>版权归属<font color=red>北京精准沟通国际传媒广告有限公司</font>（简称<font color=red>“精准沟通”</font>或“我们”)。我们为您提供简单易用的视频拍摄及编辑功能，让您轻松几步完成视频拍摄。 依据法律的规定，我们将在特定情形下收集、使用、保存和披露您的个人信息。"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为实现“爱握视频”之服务目的，“爱握视频”会通过技术手段收集您的个人信息，通过技术手段所收集的个人信息仅限于您的电话号码、手机通讯录等提供“爱握视频”使用和优质客户服务所需的信息。"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;匿名汇总统计数据不是“爱握视频”所定义的个人信息，我们将为多种目的，包括但不限于分析和使用模式的报告等，来保存和使用此类信息。我们保留以任何目的或单方面许可第三方使用和披露匿名汇总统计数据的权利。"
				+ "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;本隐私政策容许调整，我们将通过您提供的电子邮件地址，或通过网站的公告通知您。您使用“爱握视频”的行为，将被视为您知晓并同意受隐私政策条款的约束.";
		TextView adout_us_text = (TextView) findViewById(R.id.adout_us_text);
		adout_us_text.setMovementMethod(ScrollingMovementMethod.getInstance());// 滚动
		adout_us_text.setText(Html.fromHtml(html));

		findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}

		});
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}
}
