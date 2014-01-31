.PHONY: external jni

java: bin external jni
	find src -type f -print0 \
	    | xargs -0 ./javatool javac -g -deprecation -d bin

bin:
	mkdir -p bin
clean::
	rm -rf bin

external:
	$(MAKE) -C external
clean::
	$(MAKE) -C external clean

jni:
	$(MAKE) -C jni
clean::
	$(MAKE) -C jni clean
