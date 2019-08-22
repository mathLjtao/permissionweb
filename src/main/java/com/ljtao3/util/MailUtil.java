package com.ljtao3.util;

import com.ljtao3.beans.Mail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

@Slf4j
public class MailUtil {

    public static boolean send(Mail mail) {

        // TODO
        String from = "781549105@qq.com";
        int port = 25;
        //服务器地址
        String host = "smtp.exmail.qq.com";
        String pass = "password";
        String nickname = "林";

        HtmlEmail email = new HtmlEmail();
        try {
            // 这里是SMTP发送服务器的名字：163的如下："smtp.163.com"
            email.setHostName(host);
            email.setCharset("UTF-8");
            for (String str : mail.getReceivers()) {
                // 收件人的邮箱
                email.addTo(str);
            }
            // 发送人的邮箱
            email.setFrom(from, nickname);
            email.setSmtpPort(port);
            // 设置认证：用户名-密码。分别为发件人在邮件服务器上的注册名称和密码
            email.setAuthentication(from, pass);
            // 要发送的邮件主题
            email.setSubject(mail.getSubject());
            email.setMsg(mail.getMessage());
            email.send();
            log.info("{} 发送邮件到 {}", from, StringUtils.join(mail.getReceivers(), ","));
            return true;
        } catch (EmailException e) {
            log.error(from + "发送邮件到" + StringUtils.join(mail.getReceivers(), ",") + "失败", e);
            return false;
        }
    }

}

