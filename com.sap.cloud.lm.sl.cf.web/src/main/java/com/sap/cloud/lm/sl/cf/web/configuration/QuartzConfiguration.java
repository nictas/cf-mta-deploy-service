package com.sap.cloud.lm.sl.cf.web.configuration;

import java.util.Collections;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.sap.cloud.lm.sl.cf.core.util.ApplicationConfiguration;
import com.sap.cloud.lm.sl.cf.process.jobs.CleanUpJob;

@Configuration
public class QuartzConfiguration {

    private static final String QUARTZ_POSTGRESQL_PROPERTIES = "quartz.postgresql.properties";
    public static final String CLEAN_UP_TRIGGER_NAME = "cleanUpTrigger";
    public static final String TRIGGER_GROUP = "DEFAULT";

    @Bean
    public JobDetailFactoryBean cleanUpJobDetail() {
        JobDetailFactoryBean factory = new JobDetailFactoryBean();
        factory.setJobClass(CleanUpJob.class);
        factory.setDurability(true);
        return factory;
    }

    @Inject
    @Bean
    public CronTriggerFactoryBean cleanUpCronTriggerFactoryBean(ApplicationConfiguration configuration) {
        CronTriggerFactoryBean factory = new CronTriggerFactoryBean();
        factory.setJobDetail(cleanUpJobDetail().getObject());
        factory.setCronExpression(configuration.getCronExpressionForOldData());
        factory.setMisfireInstruction(org.quartz.CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
        factory.setGroup(TRIGGER_GROUP);
        factory.setName(CLEAN_UP_TRIGGER_NAME);
        return factory;
    }

    @Bean
    public AutowiringSpringBeanJobFactory quartzJobFactory() {
        AutowiringSpringBeanJobFactory quartzJobFactory = new AutowiringSpringBeanJobFactory();
        quartzJobFactory.setIgnoredUnknownProperties("applicationContext");
        return quartzJobFactory;
    }

    @Inject
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, DataSourceTransactionManager transactionManager,
                                                     AutowiringSpringBeanJobFactory quartzJobFactory, JobDetail jobDetail,
                                                     Trigger trigger) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setTransactionManager(transactionManager);
        schedulerFactoryBean.setJobFactory(quartzJobFactory);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setSchedulerName("cleanupScheduler");
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext");
        JobDetail[] jobDetails = Collections.singletonList(jobDetail)
                                            .toArray(new JobDetail[0]);
        schedulerFactoryBean.setJobDetails(jobDetails);
        Trigger[] triggers = Collections.singletonList(trigger)
                                        .toArray(new Trigger[0]);
        schedulerFactoryBean.setTriggers(triggers);
        schedulerFactoryBean.setConfigLocation(new ClassPathResource(QUARTZ_POSTGRESQL_PROPERTIES));
        return schedulerFactoryBean;
    }

}
