## How tu run this project
This project follows all the tutorials taught in the course unit.\
However there are a few things that need to be done additionally when running locally.\

## Running locally
Start by adding this line to application.properties (so spring can detect the emulator manually)\
Located in src/main/resources/application.properties\
  `spring.cloud.gcp.datastore.host=localhost:8081`

Run the emulator in a new terminal with\
  `gcloud beta emulators datastore start

Open a new terminal and initialize the environment variables (this is where the project will be run)
   `gcloud beta emulators datastore env-init`
Additionally, since spring needs an app id add\
  `export DATASTORE_USE_PROJECT_ID_AS_APP_ID=true`

Optionally instead of doing all this, spring has a built-in datastore emulator which can be enabled with the following command in application.properties\
  `spring.cloud.gcp.datastore.emulator.enabled=true\`
If this line is used, spring.cloud.gcp.datastore.host will be ignored\

Setting up ADC and using the datastore in the cloud can also be done with\
  `gcloud auth application-default login`

Important: remove the lines added in application.properties once you deploy (so the datastore in the cloud can be used)\

Run with:\
  `mvn appengine:run`

## Deploying
Login to your google account with\
  `gcloud auth login`

Set project with \
  `gcloud config set project <project-id>`

Finally run with\
  `mvn appengine:deploy -Dapp.deploy.projectId=<project-id> -Dapp.deploy.version=<version-id>`

