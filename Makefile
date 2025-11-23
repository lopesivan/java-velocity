MVN = mvn
Q   = -q

.PHONY: compile run test clean

compile:
	$(MVN) $(Q) compile

run:
	$(MVN) $(Q) exec:java

test:
	$(MVN) $(Q) test

clean:
	$(MVN) $(Q) clean

