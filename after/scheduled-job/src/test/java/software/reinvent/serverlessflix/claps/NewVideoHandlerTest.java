package software.reinvent.serverlessflix.claps;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class NewVideoHandlerTest {

    public static final TestContext TEST_CONTEXT = new TestContext();
    private NewVideoHandler newVideoHandler = new NewVideoHandler();

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
        newVideoHandler.handleRequest(new ByteArrayInputStream(event.getBytes()), null, TEST_CONTEXT);
    }

    @Test
    public void validPreValidateEventSerializesWithoutError() throws IOException {
        String preValidateEvent = """
                {
                  "version": "0",
                  "id": "c2996c57-e91e-8e7b-54e4-147d2cc497de",
                  "detail-type": "preMetadata.transcribeplugin",
                  "source": "ServerlessFlix.pluginManager",
                  "account": "832196373597",
                  "time": "2023-08-31T10:03:03Z",
                  "region": "us-west-2",
                  "resources": [
                    "arn:aws:states:us-west-2:832196373597:stateMachine:832196373597-us-west-2-PluginLifecycleWorkflow",
                    "arn:aws:states:us-west-2:832196373597:execution:832196373597-us-west-2-PluginLifecycleWorkflow:9c56bea3-f552-4771-bc0a-509b1af68618"
                  ],
                  "detail": {
                    "video": {
                      "createdAt": "2023-08-30T14:57:51.169Z",
                      "durationmillis": 24000,
                      "thumbnail": "https://dmpdx8pmqxo3f.cloudfront.net/media/ivs/v1/832196373597/ERaAhUZnrHJG/2023/8/30/14/57/ovoTjGdG38Mk/media/hls/720p30/thumbnails/thumb0.jpg",
                      "playbackUrl": "https://dmpdx8pmqxo3f.cloudfront.net/media/ivs/v1/832196373597/ERaAhUZnrHJG/2023/8/30/14/57/ovoTjGdG38Mk/media/hls/720p30/output.mp4",
                      "channel": "08d143f0-00f1-7052-f97a-cbcda39ff077",
                      "id": "st-1E4rsUkhLinpvScDxzDQ7ql",
                      "author": {
                        "username": "random1"
                      }
                    },
                    "pluginData": {
                      "preValidate": [
                        {
                          "OutputKey": "plugin-duration_plugin",
                          "OutputValue": {
                            "valid": true
                          }
                        }
                      ],
                      "postValidate": [
                        {
                          "Entries": [
                            {
                              "EventId": "b784478f-a460-6f1a-af50-5cc42e895b80"
                            }
                          ],
                          "FailedEntryCount": 0
                        }
                      ]
                    },
                    "taskToken": "AQCgAAAAKgAAAAMAAAAAAAAAAWt75fCKm1gDK7Iv4BMxeI3WJTMFRNiJ1gpcLcY7KeHwI3hB/Dy9JnfQyL5Bfm3wzCCfcTZD8czzxyeY7VYb97xtycdxKsBsiwwOQ0cJ41FfDjdNPmI5Yn9OK1rdlyf64Bd1ZnhJNQ==1qSa17DZLyI2IbOd+NBjooFp1k64s84dF+Xw+3mUJD/yuZ4fPXm55AQjS8vSKZi+9Pf+HY/nBQXuImvSM4cxBTYBjiHygSAKqGHYxEKNRwhZvjEyFh9Je79dDlpPM6ItQy83rFGqTaJVGZWMPHV/ijE/yg56Sfw4L56NAFzJfRHDYhNhbb31jumnGxdxhP6VPfcYoCsh8Nikz7dvRvWGfmfCp57tr3yx9QEdoBPjTNwkjhYlocml6y5jPI+xwgV8h0uGibp4ulUx+itide3W2DiqJkNanmf0sOv9Im7TQv3YIB7RCE3xJKGWsCavMEk59N5P9Pl7moYseCShsgowjCVRaCQrKFaf4GyxYa5jvfmAXqmcCbJqE95+fS9FUGn1E/sr/oy1I5RVb3ZqkbQPoUJBXkSgygvwMnmRTcN9miBQl3pV7CGI3OO2uECpNbLXgcXhB62UE54EPsOVA+YZrKarn2Wn3x5pDN1T9+jxwDProFDcN12sIhxIMba0b5CzeX6GOj2doo1sb9+oFuyy"
                  }
                }
                """;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(preValidateEvent.getBytes());
        newVideoHandler.handleRequest(inputStream, null, TEST_CONTEXT);
    }
}