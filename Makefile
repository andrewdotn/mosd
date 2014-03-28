.PHONY: all external java jni test

all: jni java

test: all testdata/mockbuntu
	(cd bin && find . -name '*Test.class') \
	    | sed -E -e 's,^\./|\.class$$,,g' -e 's,/,.,g' \
	    | xargs ./javatool java org.junit.runner.JUnitCore

testdata/mockbuntu: testdata/make-mockbuntu
	rm -rf $@
	testdata/make-mockbuntu
clean::
	rm -rf testdata/mockbuntu

java: bin external
	find src -type f -name '*.java' -print0 \
	    | xargs -0 ./javatool javac -g -deprecation -d bin

bin:
	mkdir -p bin
clean::
	rm -rf bin

external:
	$(MAKE) -C external
clean::
	$(MAKE) -C external clean

jni: java external
	$(MAKE) -C jni
clean::
	$(MAKE) -C jni clean
