package cn.itcast.bos.web.action;

import cn.itcast.bos.utils.MailUtils;
import cn.itcast.crm.domain.Customer;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class CustomerAction extends BaseAction<Customer>{
    @Autowired
    @Qualifier("jmsQueueTemplate")
    private JmsTemplate jmsTemplate;

    @Action(value = "customer_sendSms")
    public String sendSms() throws IOException{
        // 生成短信验证码
        String randomCode = RandomStringUtils.randomNumeric(4);
        // 将短信验证码 保存到session
        // 手机号保存在Customer对象中
        ServletActionContext.getRequest().getSession().setAttribute(model.getTelephone(),randomCode);
        System.out.println("生成手机验证码为：" + randomCode);
        //编辑短信内容为
        final String msg = "尊敬的用户您好，本次获取的验证码为：" + randomCode
                + ",服务电话：4006184000";
        //调用SMS服务发送短信
        //String result = SmsUtils.sendSmsByHTTP(model.getTelephone(),msg);
       /* String result = "000w";
        if(result.startsWith("000")){
            //发生成功
            return NONE;
        }else {
            //发送失败
            throw new RuntimeException("短信发送失败,信息码为:" + result);
        }*/
       jmsTemplate.send("bos_sms", new MessageCreator() {
           @Override
           public Message createMessage(Session session) throws JMSException {
               MapMessage mapMessage = session.createMapMessage();
               mapMessage.setString("telephone",model.getTelephone());
               mapMessage.setString("msg",msg);
               return mapMessage;
           }
       });
       return NONE;
    }
    // 属性驱动
    private String checkcode;

    public void setCheckcode(String checkcode) {
        this.checkcode = checkcode;
    }
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Action(value = "customer_regist",results = {
            @Result(name = "success",type = "redirect",location = "signup-success.html"),
            @Result(name = "input", type = "redirect", location = "signup.html")})
    public String regist(){
        //校验短信验证码,如果不通过返回注册页面
        //从session中获取验证码
        String sessionCheckcode = (String) ServletActionContext.getRequest()
                .getSession().getAttribute(model.getTelephone());
        if(sessionCheckcode==null ||  !sessionCheckcode.equals(checkcode)){
            System.out.println("短信验证码错误!");
            return INPUT;
        }
        // 调用webService 连接CRM 保存客户信息
        WebClient.create("http://localhost:9002/crm_management/services/customerService/customer")
                .type(MediaType.APPLICATION_JSON).post(model);
        System.out.println("客户注册成功...");

        // 发送一封激活邮件
        //生成激活码
        String activecode = RandomStringUtils.randomNumeric(32);
        // 将激活码保存到redis，设置24小时失效
        redisTemplate.opsForValue().set(model.getTelephone(), activecode, 24, TimeUnit.HOURS);
        //调用MailUtils发送邮件
        final String content = "尊敬的客户您好，请于24小时内，进行邮箱账户的绑定，点击下面地址完成绑定:<br/><a href='"
                + MailUtils.activeUrl + "?telephone=" + model.getTelephone()
                + "&activecode=" + activecode + "'>速运快递邮箱绑定地址</a>";
        jmsTemplate.send("bos_mail", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("title","速运快递激活邮件");
                mapMessage.setString("email",model.getEmail());
                mapMessage.setString("content",content);
                return mapMessage;
            }
        });
        //MailUtils.sendMail("速运快递激活邮件", content, model.getEmail());
        return SUCCESS;
    }

    // 属性驱动
    private String activecode;

    public void setActivecode(String activecode) {
        this.activecode = activecode;
    }
    @Action("customer_activeMail")
    public String activeMail() throws IOException {
        //设置字符集
        ServletActionContext.getResponse().setContentType("text/html;charset=utf-8");
        // 判断激活码是否有效
        String activecodeRedis = redisTemplate.opsForValue().get(model.getTelephone());
        if(activecodeRedis == null || !activecodeRedis.equals(activecode)){
            // 激活码无效
            ServletActionContext.getResponse().getWriter().println("激活码无效，请登录系统，重新绑定邮箱");
        }else{
            Customer customer = WebClient.create("http://localhost:9002/crm_management/services" +
                    "/customerService/customer/telephone/" + model.getTelephone())
                    .accept(MediaType.APPLICATION_JSON).get(Customer.class);
            if(customer!=null||customer.getType()!=null||customer.getType() != 1){
                // 没有绑定,进行绑定
                WebClient.create("http://localhost:9002/crm_management/services" +
                        "/customerService/customer/updatetype/"
                        + model.getTelephone()).get();
                ServletActionContext.getResponse().getWriter()
                        .println("邮箱绑定成功！");
            }else{
                // 已经绑定过
                ServletActionContext.getResponse().getWriter().println("邮箱已经绑定过了,无需重复绑定!");
            }
            //删除redis的激活码
            redisTemplate.delete(model.getTelephone());
        }
        return NONE;
    }
}
