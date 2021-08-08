package com.example.springbatchtasklet;

import com.example.springbatchtasklet.batch.BatchConfig;
import com.example.springbatchtasklet.batch.CapitalizeNamesJobConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileSystemUtils;

import javax.batch.operations.NoSuchJobException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
		classes = {SpringBatchTaskletApplicationTests.BatchTestConfig.class})
public class SpringBatchTaskletApplicationTests {

	private static Path csvFilesPath, testInputsPath;

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@BeforeAll
	public static void copyFiles()
			throws URISyntaxException, IOException {
		csvFilesPath = Paths.get(new ClassPathResource("csv").getURI());
		testInputsPath = Paths.get("target/test-inputs");
		try {
			Files.createDirectory(testInputsPath);
		} catch (Exception e) {
			e.printStackTrace();
		}

		FileSystemUtils.copyRecursively(csvFilesPath, testInputsPath);
	}

	@Test
	public void testHelloWorldJob() throws Exception {

		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		assertThat(jobExecution.getExitStatus().getExitCode())
				.isEqualTo("COMPLETED");

		// check that all files are deleted
		File testInput = testInputsPath.toFile();
		assertThat(testInput.list().length).isEqualTo(0);
	}


	@Configuration
	@Import({BatchConfig.class, CapitalizeNamesJobConfig.class})
	static class BatchTestConfig {

		@Autowired
		private Job capitalizeNamesJob;

		@Bean
		JobLauncherTestUtils jobLauncherTestUtils()
				throws NoSuchJobException {
			JobLauncherTestUtils jobLauncherTestUtils =
					new JobLauncherTestUtils();
			jobLauncherTestUtils.setJob(capitalizeNamesJob);

			return jobLauncherTestUtils;
		}
	}
}
