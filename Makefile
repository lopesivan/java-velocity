GRADLE = /opt/gradle/gradle-8.10.2/bin/gradle

.PHONY: init build run test clean

init:
	$(GRADLE) wrapper
build:
	./gradlew build
run:
	./gradlew run
test:
	./gradlew test
clean:
	./gradlew clean
