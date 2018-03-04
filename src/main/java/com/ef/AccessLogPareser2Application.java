package com.ef;

import com.ef.service.AccessLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class AccessLogPareser2Application implements ApplicationRunner {
	private static final Logger logger = LoggerFactory.getLogger(AccessLogPareser2Application.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd.hh:mm:ss");

    @Autowired
    private ApplicationContext context;

    public static void main(String[] args) {

		SpringApplication.run(AccessLogPareser2Application.class, args);
		logger.info("the end");
	}

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("Command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
        logger.info("Non Option Args: {}", args.getNonOptionArgs());
        logger.info("Option Names: {}", args.getOptionNames());
        if (args !=null && args.getSourceArgs().length >= 4) {

            String accesslog = args.getOptionValues("accesslog").get(0);
            String duration = args.getOptionValues("duration").get(0);
            int threshold = Integer.parseInt(args.getOptionValues("threshold").get(0));
            Date startDate = sdf.parse(args.getOptionValues("startDate").get(0));

            JobLauncher jobLauncher = context.getBean(JobLauncher.class);
            Job importUserJob = context.getBean(Job.class);
            JobExecution jobExecution = jobLauncher
                    .run(importUserJob,
                            new JobParametersBuilder()
                                    .addString("fullPathFileName", accesslog)
                                    .toJobParameters());

            AccessLogService accessLogService = context.getBean(AccessLogService.class);

            List<String> result = accessLogService.getIpsByParams(duration, threshold, startDate);
            accessLogService.storeIpsByParams(result, duration, threshold, startDate);

            logger.info("Result:");
            for (String name : result) {
                logger.info(name);
            }
        }
    }
}
