FROM adorsys/java:11

ENV APP_PORT=9091
COPY ./target/dial-validator-java-*.jar ./dial-validator-java.jar

CMD exec java $JAVA_OPTS -Duser.home=/tmp -jar ./dial-validator-java.jar --server.port=$APP_PORT