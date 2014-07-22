
SOURCES = smor.fst
OTHER = lexicon

smor.a: phon.a $(OTHER)

%.a: %.fst
	fst-compiler $< $@

%.ca: %.a
	fst-compact $< $@

Makefile: *.fst
	-makedepend -Y -o.a $(SOURCES) 2>/dev/null 


# DO NOT DELETE

smor.a: map.fst NUM.fst deko.fst flexion.fst defaults.fst FIX.fst PRO.fst
