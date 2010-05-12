SHELL = /bin/bash -eu

CFLAGS = -W -Wall -Wextra -Wformat=2 -MD \
	 -I/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Headers \
	 -I/opt/local/include \
	 -g3 -O3
LDFLAGS = -L/opt/local/lib -lmagic -larchive

SRCS = \
       arinspect.c \
       net_subjoin_mosd_ArchiveInspector.c \
       net_subjoin_mosd_JNITest.c \


all: libmosd.jnilib mosd_jni.jar

libmosd.jnilib: ${SRCS:%.c=%.o}
	gcc ${CFLAGS} -shared $^ -o $@ ${LDFLAGS}

net_subjoin_mosd_ArchiveInspector.c: net_subjoin_mosd_ArchiveInspector.h
net_subjoin_mosd_JNITest.c: net_subjoin_mosd_JNITest.h

mosd_jni.jar:
	echo 'This is a dummy jar to give something to attach a native \
library location to in Eclipse.' > mosd_jni.txt
	jar cf $@ mosd_jni.txt
	rm -f mosd_jni.txt

%.h:
	javah -classpath ../bin $(shell echo $* | sed -e s,_,.,g)

clean:
	rm -f *.d *.jnilib *.o

-include ${SRCS:%.c=%.d}
