package cn.itcast.bos.mq;

import cn.itcast.bos.utils.MailUtils;
import org.springframework.stereotype.Service;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

@Service("mailConsumer")
public class MailConsumer implements MessageListener{
    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage = (MapMessage) message;
        try {
            MailUtils.sendMail(mapMessage.getString("title"),mapMessage.getString("content")
                    ,mapMessage.getString("email"));
        } catch (Exception e) {
            throw new RuntimeException("邮件发送失败:"+e);
        }
    }
}
