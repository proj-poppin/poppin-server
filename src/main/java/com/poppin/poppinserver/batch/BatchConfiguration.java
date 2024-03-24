//package com.poppin.poppinserver.batch;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.ItemWriter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//
//import java.util.Date;
//
//@Configuration
//@RequiredArgsConstructor
//@EnableBatchProcessing
//@EnableScheduling
//public class BatchConfiguration {
//
//    private final JobLauncher jobLauncher;
//    private final JobRepository jobRepository;
//
//    @Bean
//    public Job sampleJob(Step sampleStep) {
//        return new JobBuilder("sampleJob", jobRepository)
//                .incrementer(new RunIdIncrementer())
//                .start(sampleStep)
//                .build();
//    }
//
//    @Bean
//    public Step sampleStep(ItemReader<String> reader,
//                           ItemProcessor<String, String> processor,
//                           ItemWriter<String> writer) {
//        return new StepBuilder("sampleStep", jobRepository)
//                .<String, String>chunk(10)
//                .reader(reader)
//                .processor(processor)
//                .writer(writer)
//                .build();
//    }
//
//    // 스케줄링 설정 - 매일 자정에 실행
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void runJob() throws Exception {
//        JobParametersBuilder paramsBuilder = new JobParametersBuilder();
//        paramsBuilder.addDate("date", new Date());
//        jobLauncher.run(sampleJob(sampleStep()), paramsBuilder.toJobParameters());
//    }
//
//    // ItemReader, ItemProcessor, ItemWriter 구현체는 별도로 정의해야 함
//}
