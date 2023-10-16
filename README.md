# SVS402 Refactoring Java applications to Serverless

## High Level Design

![High level design](docs/svs402-hld.png)

## Requirements

CDK

## Deployment

```bash
./build-deploy.sh
```

## Testing

Create test videos:

```bash
aws events put-events --entries '[{"Source":"software.serverlessflix.video", "EventBusName":"videos", "DetailType":"new-video","Detail":"{\"id\": \"best-video\",\"channel\" :\"This channel\",\"title\" :\"This is not the best video in the world\",\"author\": {\"username\" : \"Max\", \"email\" : \"something123454@amazon.de\"}, \"playbackUrl\": \"/best-video-12343\", \"createdAt\": \"2023-10-16\"}"}]'

aws events put-events --entries '[{"Source":"software.serverlessflix.video", "EventBusName":"videos", "DetailType":"new-video","Detail":"{\"id\": \"svs402-recording\",\"channel\" :\"My channel\",\"title\" :\"SVS 402 Recording\",\"author\": {\"username\" : \"Mark\", \"email\" : \"something123454@amazon.de\"}, \"playbackUrl\": \"/svs-red-14543\", \"createdAt\": \"2023-10-16\"}"}]'

```

Call REST-Api (All Videos):

```bash
curl --location --request GET $(cat infra/target/output.json | jq -r '."SVS402-InfraStack".ApiEndpointSpring')'/videos' | jq
```

Call REST-Api (Single Videos):

```bash
curl --location --request GET $(cat infra/target/output.json | jq -r '."SVS402-InfraStack".ApiEndpointSpring')'/videos/svs402-recording' | jq
```

Generate some claps for the videos: 

```bash
artillery run -t $(cat infra/target/output.json | jq -r '."SVS402-InfraStack".ApiEndpointSpring') -v '{ "url": "/claps" , "videoId" : ["best-video", "svs402-recording"]}' infra/loadtest.yaml
```
Or call single API:

```bash
curl --location --request POST $(cat infra/target/output.json | jq -r '."SVS402-InfraStack".ApiEndpointSpring')'/claps' --data-raw '{
    "video": "best-video"
}' --header 'Content-Type: application/json' | jq
```

Retrieve videos and clap counts:

```bash
curl --location --request GET $(cat infra/target/output.json | jq -r '."SVS402-InfraStack".ApiEndpointSpring')'/videos' | jq
```


## Clean up

```bash
cd infra
cdk destroy
```


## Todo 

- [ ] Remove any sensitive data from tests

### After

- [ ]
- [ ]

### Before

- [ ]
- [ ]