package com.java.ftpexp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public ItemReader<Object> ftpFileReader() {
        return new FtpFileReaderConfig().ftpFileReader();
    }

    @Bean
    public ItemProcessor<Object, Object> fileProcessor() {
        // Implement your own processor logic if needed
        return item -> item;
    }

    @Bean
    public ItemWriter<Object> fileWriter() {
        // Implement your own writer logic if needed
        return items -> {
            for (Object item : items) {
                System.out.println(item);
            }
        };
    }

    @Bean
    public Step myStep(ItemReader<Object> reader, ItemProcessor<Object, Object> processor, ItemWriter<Object> writer) {
        return stepBuilderFactory.get("myStep")
                .<Object, Object>chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    //  @Bean
    //  public Step myStep(ItemReader<FieldSet> reader, ItemProcessor<FieldSet, Object> processor, ItemWriter<Object> writer) {
    //      return stepBuilderFactory.get("myStep")
    //              .<FieldSet, Object>chunk(10)
    //              .reader(reader)
    //              .processor(processor)
    //              .writer(writer)
    //              .build();
    //  }

    @Bean
    public Job myJob(Step myStep) {
        return jobBuilderFactory.get("myJob")
                .start(myStep)
                .build();
    }

}