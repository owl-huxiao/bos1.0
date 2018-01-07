package cn.itcast.bos.quartz;

import cn.itcast.bos.service.take_delivery.PromotionService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * 定时改变宣传任务的状态
 */

public class PromotionJob implements Job{
    @Autowired
    private PromotionService promotionService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("活动过期处理任务执行了....");
        // 每分钟执行一次，当前时间大于 promotion数据表 endDate， 活动已经过期，设置status='2'
        promotionService.updateStatus(new Date());
    }
}
