package com.example.springbatchtasklet.batch;

import com.example.springbatchtasklet.model.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

@Slf4j
public class CapitalizeNamesJobConfig {

    @Bean
    public Job capitallizedNamesJob(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory){
        return jobBuilderFactory.get("capitalizeNamesJob")
                .start(capitalizedNamesStep(stepBuilderFactory))
                .next(deleteFilesStep(stepBuilderFactory)).build();
    }

    @Bean
    public Step deleteFilesStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("deleteFilesStep")
                .tasklet(fileDeletingTasklet()).build();
    }

    @Bean
    public Tasklet fileDeletingTasklet() {
        return new FileDeletingTasklet(new FileSystemResource("target/test-inputs"));
    }

    @Bean
    public Step capitalizedNamesStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("capitalizedNamesStep")
                .<Person,Person>chunk(10).reader(multiItemReader())
                .processor(itemProcessor())
                .writer(itemWriter()).build();
    }

    @Bean
    public ItemWriter<? super Person> itemWriter() {
        return new FlatFileItemWriterBuilder<Person>()
                .name("personItemWriter")
                .resource(new FileSystemResource(
                        "target/test-outputs/persons.txt"))
                .delimited().delimiter(", ")
                .names(new String[] {"firstName", "lastName"}).build();
    }

    private ResourceAwareItemReaderItemStream<? extends Person> itemReader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader").delimited()
                .names(new String[] {"firstName", "lastName"})
                .targetType(Person.class).build();
    }

    private ItemProcessor<? super Person, ? extends Person> itemProcessor() {
        return new PersonItemProcessor();
    }

    private ItemReader<? extends Person> multiItemReader() {
        ResourcePatternResolver patternResolver =
                new PathMatchingResourcePatternResolver();
        Resource[] resources = null;
        try {
            resources = patternResolver
                    .getResources("file:target/test-inputs/*.csv");
        } catch (IOException e) {
            log.error("error reading files", e);
        }

        return new MultiResourceItemReaderBuilder<Person>()
                .name("multiPersonItemReader").delegate(itemReader())
                .resources(resources).setStrict(true).build();
    }


}
