package com.ef.service;

import com.ef.model.LogRec;
import com.ef.model.LogRecDB;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


/**
 * Created by pasha on 02.03.18.
 */
@Configuration
public class LogBatchJobConfig<JobCompletionNotificationListener> {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @Scope(value = "step", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public FlatFileItemReader<LogRec> importReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFile) {
        FlatFileItemReader<LogRec> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(pathToFile));
        reader.setLineMapper(new DefaultLineMapper<LogRec>() {{
            setLineTokenizer(new DelimitedLineTokenizer("|") {{
                setNames(new String[] { "date", "ip", "request", "status", "useragent" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<LogRec>() {{
                setTargetType(LogRec.class);
            }});
        }});
        return reader;
    }

    @Bean
    public ItemProcessor<LogRec, LogRecDB> processor() {
        return new LogRecProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<LogRecDB> writer(DataSource dataSource) {
        JdbcBatchItemWriter<LogRecDB> writer = new JdbcBatchItemWriter<LogRecDB>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<LogRecDB>());
        writer.setSql("INSERT INTO logdata (accessdate, ip, request, status, useragent) VALUES (:date, :ip, :request, :status, :useragent)");
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public Job importUserJob(JobBuilderFactory jobs, Step s1) {
        return jobs.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(s1)
                .end()
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<LogRec> reader,
                      ItemWriter<LogRecDB> writer, ItemProcessor<LogRec, LogRecDB> processor) {
        return stepBuilderFactory.get("step1")
                .<LogRec, LogRecDB> chunk(10000)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

}
