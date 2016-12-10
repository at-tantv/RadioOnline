package com.tantv.vnradiotruyen.activities;

import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.tantv.vnradiotruyen.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Copyright @2015
 * Created by tantv on 06/11/2015.
 */
@EActivity(R.layout.activity_about)
public class AboutActitivy extends AppCompatActivity {
    @ViewById(R.id.webViewAbout)
    WebView mWebViewAbout;

    @Click(R.id.imgPlayMusicBackAbout)
    void onClickExit() {
        finish();
    }

    @AfterViews
    void afterView() {
        getSupportActionBar().hide();

        final String mimeType = "text/html";
        final String encoding = "UTF-8";

        String strHtml = "<p style=\"text-align: center;\"><span style=\"font-size: small;\"><strong>VN RADIO ONLINE</strong></span></p>\n" +
                "<p style=\"text-align: justify;\"><span style=\"font-size: small;\"><strong>VN RADIO ONLINE </strong>l&agrave; ứng dụng được nghe audio trực tuyến, được cung cấp <strong>MIỄN PH&Iacute;,&nbsp;</strong>chỉ với một thiết bị di động trong tay được kết nối internet bạn đ&atilde; c&oacute; thể c&oacute; cả kho audio trong tầm tay.</span></p>\n" +
                "<p style=\"text-align: justify;\"><span style=\"font-size: small;\">Với tốc độ nhanh ch&oacute;ng v&agrave; kho audio phong ph&uacute; đầy đủ thể loại hứa hẹn sẽ mang lại cho bạn những ph&uacute;t gi&acirc;y thư giản tuyệt vời.</span></p>\n" +
                "<p style=\"text-align: justify;\"><span style=\"font-size: small;\">Đặc biệt với chức năng <em>y&ecirc;u cầu audio</em> bạn c&oacute; thể gửi phản hồi cho ch&uacute;ng t&ocirc;i, ch&uacute;ng t&ocirc;i sẽ update data một c&aacute;ch sớm nhất.</span></p>\n" +
                "<p style=\"text-align: justify;\"><span style=\"font-size: small;\"><strong>Chức năng ch&iacute;nh:</strong></span></p>\n" +
                "<p style=\"text-align: justify;\"><span style=\"font-size: small;\">- Giao diện đơn giản, dễ sử dụng</span></p>\n" +
                "<p style=\"text-align: justify;\"><span style=\"font-size: small;\">- Tốc độ load nhanh</span></p>\n" +
                "<p style=\"text-align: justify;\"><span style=\"font-size: small;\">- C&oacute; chức năng y&ecirc;u cầu truyện hoặc audio bạn muốn nghe </span></p>\n" +
                "<p style=\"text-align: justify;\"><span style=\"font-size: small;\">- Kho dữ liệu miễn ph&iacute; , phong ph&uacute;</span></p>\n" +
                "<p style=\"text-align: justify;\"><span style=\"font-size: small;\">- Chức năng hẹn giờ tắt ứng dụng</span></p>\n" +
                "<p style=\"text-align: justify;\"><span style=\"font-size: small;\">- ....</span></p>\n" +
                "<p style=\"text-align: justify;\"><span style=\"font-size: small;\">Mọi &yacute; kiến đ&oacute;ng g&oacute;p xin gửi về qua email :<span style=\"text-decoration: underline; color: #000080;\"><em> htt.devvn@gmail.com</em></span></span></p>\n" +
                "<p style=\"text-align: justify;\"><span style=\"font-size: small;\">Ch&uacute;ng t&ocirc;i rất mong nhận được sự phản hồi của bạn để ứng dụng c&agrave;ng c&agrave;ng tốt hơn.</span></p>\n" +
                "<p style=\"text-align: justify;\"><span style=\"font-size: small;\">Th&acirc;n &aacute;i v&agrave; ch&uacute;c bạn c&oacute; những ph&uacute;t gi&acirc;y vui vẻ.</span></p>";

        mWebViewAbout.loadDataWithBaseURL("", strHtml, "text/html", "utf-8", "");
    }
}
