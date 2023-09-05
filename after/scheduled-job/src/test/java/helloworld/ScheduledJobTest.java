package helloworld;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ScheduledJobTest {

    private final String event = """
            {
                    "version": "0",
                    "id": "fe8d3c65-xmpl-c5c3-2c87-81584709a377",
                    "detail-type": "RDS DB Instance Event",
                    "source": "aws.rds",
                    "account": "123456789012",
                    "time": "2020-04-28T07:20:20Z",
                    "region": "us-east-2",
                    "resources": [
                            "arn:aws:rds:us-east-2:123456789012:db:rdz6xmpliljlb1"
                    ],
                    "detail": {}
            }
            """;

    @Test
    public void testScheduledEvent() throws IOException {
        ScheduledJob scheduledJob = new ScheduledJob();
        scheduledJob.handleRequest(new ByteArrayInputStream(event.getBytes()), null, new TestContext());
    }
}