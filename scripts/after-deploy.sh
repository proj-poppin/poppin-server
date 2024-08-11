#!/bin/bash

# Navigate to the deployment directory
cd /service/tomcat

# Find the JAR file that contains 'poppin-server' in the name
JAR_FILE=$(ls -1 *poppin-server*.jar | head -n 1)

# Run the JAR file with nohup to keep it running in the background
echo "Starting new application"
nohup java -jar $JAR_FILE > /dev/null 2>&1 &