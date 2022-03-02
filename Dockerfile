FROM openjdk:latest
COPY ./src .
RUN javac nijakow/four/Main.java
ENTRYPOINT ["/usr/bin/java", "nijakow.four.Main", "-s", "-d", "nijakow/four/lib/"]
