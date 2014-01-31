.PHONY: all
.DELETE_ON_ERROR:

all: \
    JSAP-2.1.jar \
    JSAP-2.1-src.zip \
    guava-11.0.2-sources.jar \
    guava-11.0.2.jar \
    junit-4.8.2.jar \
    junit-4.8.2-sources.jar \
    libarchive-2.8.3.tar.gz \
    libarchive-2.8.3-install/lib/libarchive.2.dylib

guava-11.0.2.jar:
	curl -LO http://search.maven.org/remotecontent?filepath=com/google/guava/guava/11.0.2/guava-11.0.2.jar

guava-11.0.2-sources.jar:
	curl -LO http://search.maven.org/remotecontent?filepath=com/google/guava/guava/11.0.2/guava-11.0.2-sources.jar

junit-4.8.2.jar:
	curl -LO http://search.maven.org/remotecontent?filepath=junit/junit/4.8.2/junit-4.8.2.jar

junit-4.8.2-sources.jar:
	curl -LO http://search.maven.org/remotecontent?filepath=junit/junit/4.8.2/junit-4.8.2-sources.jar

JSAP-2.1.jar:
	curl -LO http://search.maven.org/remotecontent?filepath=com/martiansoftware/jsap/2.1/jsap-2.1.jar

JSAP-2.1-src.zip:
	curl -LO http://sourceforge.net/projects/jsap/files/jsap/2.1/JSAP-2.1-src.zip

libarchive-2.8.3.tar.gz:
	curl -LO https://github.com/libarchive/libarchive/archive/v2.8.3.tar.gz && mv v2.8.3.tar.gz $@

libarchive-2.8.3-install/lib/libarchive.2.dylib: libarchive-2.8.3.tar.gz
	tar xf libarchive-2.8.3.tar.gz
	(cd libarchive-2.8.3 \
	    && autoreconf -i \
	    && ./configure --prefix="$$PWD/../libarchive-2.8.3-install" \
	    && make -j8 install)

clean::
	rm -rf libarchive-2.8.3-install
	$(MAKE) -C libarchive-2.8.3 clean

