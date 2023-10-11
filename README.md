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
aws events put-events --entries '[{"Source":"software.serverlessflix.video", "EventBusName":"videos", "DetailType":"new-video","Detail":"{\"id\": \"something new\",\"channel\" :\"This channel\",\"title\" :\"My Title\",\"author\": {\"username\" : \"Max\", \"email\" : \"mxschell@amazon.de\"}}"}]'
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