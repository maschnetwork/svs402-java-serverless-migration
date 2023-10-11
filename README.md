# SVS402 Refactoring Java applications to Serverless

## High Level Design

![High level design](docs/svs402-hld.png)

## Requirements

CDK

## Deployment

```bash
cd infra
cdk deploy
```


## Testing

Create test videos:

```bash
aws events put-events --entries '[{"Source":"software.serverlessflix.video", "EventBusName":"videos", "DetailType":"new-video","Detail":"{\"id\": \"something new\",\"channel\" :\"This channel\",\"title\" :\"My Title\",\"author\": {\"username\" : \"Max\"}}"}]'
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