package software.amazon.svs402;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@SpringBootApplication
public class Svs402Application {

    public static void main(String[] args) {
        SpringApplication.run(Svs402Application.class, args);
    }

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob().ofType(SoutJob.class)
                .storeDurably()
                .withIdentity("MS1")
                .withDescription("Sout the job instance id")
                .build();
    }

    @Bean
    public Trigger trigger(JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity("Qrtz_Trigger")
                .withDescription("Sample trigger")
                .withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(10))
                .build();
    }
}
