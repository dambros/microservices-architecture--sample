#!/bin/sh

echo "********************************************************"
echo "Waiting for the eureka server to start on port $EUREKASERVER_PORT"
echo "********************************************************"
while ! `nc -z eurekaserver $EUREKASERVER_PORT`; do sleep 3; done
echo "******* Eureka Server has started"

echo "********************************************************"
echo "Starting the Zipkin Server"
echo "********************************************************"
java -Djava.security.egd=file:/dev/./urandom -Dserver.port=$SERVER_PORT   \
     -Dspring.profiles.active=$PROFILE                          \
     -Dspring.zipkin.baseUrl=$ZIPKIN_URI                       \
     -Deureka.client.serviceUrl.defaultZone=$EUREKASERVER_URI  \
     -jar /usr/local/zipkinserver/@project.build.finalName@.jar