
# IMPORTANT NOTE                                  

Elastic beanstalk requires a __zip__ file containing the `Dockerfile` and all other
related files. The files __MUST__ be in the root of the zip. __DO NOT__ zip up the
parent directory!

The `Dockerrun.aws.json` is used to pass logs from the docker container to the 
elastic beanstalk console.

The hidden file `.ebextensions` is used to configure __TLS__.