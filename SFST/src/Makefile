
# try different definitions of SGILIB if hash_map is not found
#SGILIB=
SGILIB=-DSGI__gnu_cxx
#SGILIB=-DSGIext

# comment the following lines if readline is not found
READLINE = -DREADLINE
ifeq ($(findstring arwin, $(shell uname)),arwin)
LREADLINE = -lreadline -lncurses
else
LREADLINE = -lreadline -lhistory -lncurses
endif

# data type used for the encoding of the symbols (char, short, or int)
# short is the default
#CDT=-DCODE_DATA_TYPE=short

# Uncomment this line in order to compile a library and
# run "make clean" in order to force a recompilation of the object files
# FPIC = -fPIC

CXX = g++
WARNING = -Wall -Wcast-qual -Wconversion -std=c++98 -Wno-deprecated -ansi

ifeq ($(findstring CYGWIN,$(shell uname)),CYGWIN)
EXE = .exe
endif

CC = g++
CL = $(CC)
CFLAGS = -O3 $(WARNING) $(SGILIB) $(CDT) $(READLINE) $(FPIC)
LDFLAGS =

CXXFLAGS = $(CFLAGS)
LXXFLAGS = $(LDFLAGS)

INSTALL = install
INSTALL_MANPAGES = install -m 644
INSTALL_DIR = install -d -m 755

ifeq ($(findstring arwin, $(shell uname)),arwin)
INSTALL_LIBS = $(INSTALL)
else
INSTALL_LIBS = $(INSTALL) -s
endif

DESTDIR =
PREFIX = /usr/local/

PROGRAMS = fst-compiler$(EXE) fst-infl$(EXE) fst-generate$(EXE) fst-print$(EXE)\
	fst-compare$(EXE) fst-compact$(EXE) fst-infl2$(EXE) fst-lowmem$(EXE)\
	fst-infl3$(EXE) fst-lattice$(EXE) fst-match$(EXE) fst-parse$(EXE)\
	fst-parse2$(EXE) fst-train$(EXE) fst-compiler-utf8$(EXE) fst-mor$(EXE) \
	fst-text2bin$(EXE) fst-infl2-daemon$(EXE)

ALLPROGRAMS = $(PROGRAMS)


OBJ = basic.o utf8.o alphabet.o fst.o operators.o determinise.o hopcroft.o

.PHONY: all clean maintainer-clean install maninstall remake

all: $(ALLPROGRAMS)
	strip $(ALLPROGRAMS)

fst-api.zip: testprogram.C alphabet.C alphabet.h basic.C basic.h compact.C \
	compact.h lowmem.C lowmem.h robust.C
	zip $@ $^

fst-compact$(EXE): compact.o make-compact.o

fst-generate$(EXE): generate.o

fst-match$(EXE): compact.o

fst-train$(EXE): compact.o

fst-compiler$(EXE): $(OBJ) compact.o make-compact.o interface.o default-scanner.o

fst-compiler-utf8$(EXE): $(OBJ) compact.o make-compact.o interface.o \
	utf8-scanner.o fst-compiler.o
	$(CXX) -o $@ $^ $(LDFLAGS)

fst-mor$(EXE): $(OBJ) fst-mor.o
	$(CXX) -o $@ $^ $(LDFLAGS) $(LREADLINE)

fst-text2bin$(EXE): $(OBJ) fst-text2bin.o
	$(CXX) -o $@ $^ $(LDFLAGS)

%$(EXE): $(OBJ) %.o
	$(CXX) -o $@ $^ $(LDFLAGS)

fst-infl2$(EXE): alphabet.o basic.o compact.o robust.o utf8.o fst-infl2.o
	$(CXX) -o $@ $^ $(LDFLAGS)

fst-infl2-daemon$(EXE): alphabet.o basic.o compact.o robust.o utf8.o fst-infl2-daemon.o
	$(CXX) -o $@ $^ $(LDFLAGS)

fst-infl3$(EXE): alphabet.o basic.o utf8.o lowmem.o fst-infl3.o
	$(CXX) -o $@ $^ $(LDFLAGS)

testprogram$(EXE): testprogram.o
	$(CL) -lsfst -o $@ $^ $(LDFLAGS) || echo "Note that the SFST library libsfst.so must have been installed in order to compile testprogram."

fst-compiler.C: fst.h compact.h make-compact.h interface.h fst-compiler.yy
	bison -d -o $@ fst-compiler.yy
	if [ -f fst-compiler.C.h ]; then mv fst-compiler.C.h fst-compiler.h; fi
	if [ -f fst-compiler.H ]; then mv fst-compiler.H fst-compiler.h; fi

default-scanner.C: fst.h interface.h scanner.ll fst-compiler.C default-scanner.ll
	flex -o$@ default-scanner.ll

utf8-scanner.C: fst.h interface.h scanner.ll fst-compiler.C utf8-scanner.ll
	flex -o$@ utf8-scanner.ll

default-scanner.ll: scanner.ll
	sed -e '/^utf8>/d' -e 's/^default> *//' scanner.ll > default-scanner.ll

utf8-scanner.ll: scanner.ll
	sed -e '/^default>/d' -e 's/^utf8> *//' scanner.ll > utf8-scanner.ll

libsfst.tar.gz: libsfst.so compact.h alphabet.h basic.h fst.h 
	tar -zcf $@ $^

libsfst.so: alphabet.o basic.o utf8.o compact.o fst.o operators.o determinise.o generate.o hopcroft.o
	$(CL) -shared -o $@ $^ $(LDFLAGS)

libsfst.dylib: alphabet.o basic.o utf8.o compact.o fst.o operators.o determinise.o generate.o hopcroft.o
	$(CL) -dynamiclib -current_version 0 -o $@ $^ $(LDFLAGS)

remake: clean $(ALLPROGRAMS)

clean:
	-rm *.o Makefile.bak y.tab.h man1/*~ 2>&- > /dev/null

maintainer-clean: clean
	-rm $(ALLPROGRAMS) testprogram 2>&- > /dev/null

install: $(PROGRAMS)
	$(INSTALL_DIR) $(DESTDIR)$(PREFIX)bin
	for p in $(PROGRAMS); do $(INSTALL) $$p $(DESTDIR)$(PREFIX)bin/$$p; done

maninstall:
	$(INSTALL_DIR) $(DESTDIR)$(PREFIX)man/man1
	for m in man1/*.1; do $(INSTALL_MANPAGES) $$m $(DESTDIR)$(PREFIX)man/$$m; done

ifeq ($(findstring arwin, $(shell uname)),arwin)
DYNLIBS = libsfst.dylib
else
DYNLIBS = libsfst.so
endif

libinstall: dynlib hfiles

dynlib:	$(DYNLIBS)
	$(INSTALL_LIBS) $^ $(PREFIX)lib
	(ldconfig || true)

hfiles: alphabet.h basic.h fst.h interface.h mem.h sgi.h utf8.h
	-@if [ ! -d $(PREFIX)include/sfst ]; then mkdir -p $(PREFIX)include/sfst; fi
	$(INSTALL) $^ $(PREFIX)include/sfst


# DO NOT DELETE

alphabet.o: utf8.h alphabet.h basic.h sgi.h
basic.o: basic.h
compact.o: compact.h alphabet.h basic.h sgi.h
default-scanner.o: interface.h utf8.h fst.h alphabet.h basic.h sgi.h mem.h
default-scanner.o: fst-compiler.h
determinise.o: fst.h alphabet.h basic.h sgi.h mem.h
fst-compact.o: make-compact.h fst.h alphabet.h basic.h sgi.h mem.h compact.h
fst-compare.o: fst.h alphabet.h basic.h sgi.h mem.h
fst-compiler.o: make-compact.h fst.h alphabet.h basic.h sgi.h mem.h compact.h
fst-compiler.o: interface.h utf8.h
fst-generate.o: fst.h alphabet.h basic.h sgi.h mem.h
fst-infl.o: fst.h alphabet.h basic.h sgi.h mem.h
fst-infl2-daemon.o: Socket.h compact.h alphabet.h basic.h sgi.h
fst-infl2.o: compact.h alphabet.h basic.h sgi.h
fst-infl3.o: lowmem.h alphabet.h basic.h sgi.h
fst-lattice.o: fst.h alphabet.h basic.h sgi.h mem.h
fst-lowmem.o: fst.h alphabet.h basic.h sgi.h mem.h
fst-match.o: compact.h alphabet.h basic.h sgi.h
fst-mor.o: fst.h alphabet.h basic.h sgi.h mem.h
fst-parse.o: fst.h alphabet.h basic.h sgi.h mem.h
fst-parse2.o: fst.h alphabet.h basic.h sgi.h mem.h
fst-print.o: fst.h alphabet.h basic.h sgi.h mem.h
fst-text2bin.o: fst.h alphabet.h basic.h sgi.h mem.h
fst-train.o: compact.h alphabet.h basic.h sgi.h
fst.o: fst.h alphabet.h basic.h sgi.h mem.h
generate.o: fst.h alphabet.h basic.h sgi.h mem.h
interface.o: interface.h utf8.h fst.h alphabet.h basic.h sgi.h mem.h
lowmem.o: lowmem.h alphabet.h basic.h sgi.h
make-compact.o: make-compact.h fst.h alphabet.h basic.h sgi.h mem.h compact.h
operators.o: fst.h alphabet.h basic.h sgi.h mem.h
robust.o: compact.h alphabet.h basic.h sgi.h
testprogram.o: compact.h alphabet.h basic.h sgi.h
utf8-scanner.o: interface.h utf8.h fst.h alphabet.h basic.h sgi.h mem.h
utf8-scanner.o: fst-compiler.h
utf8.o: utf8.h
hopcroft.o: fst.h alphabet.h basic.h sgi.h mem.h
