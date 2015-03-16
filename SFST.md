SFST Finite State Tools

# Introduction #

SFST is a toolkit for the implementation of morphological analysers and other tools which are based on finite state transducer technology.

# Details #

### The SFST tools comprise ###
  * a programming language for finite state transducers (FST)
  * a compiler which translates the FST programs into minimised transducers
  * interactive and batch-mode programs for analysis and generation
  * tools for comparing and printing transducers
  * an efficient C++ transducer library for the implementation of new FST tools

### SFST is ###
  * freely available under the [GNU General Public License](http://www.gnu.org/copyleft/gpl.html)
  * easy to learn for users who are familiar with grep, sed, or Perl.
  * efficient implementation in C++
  * supports
    * a wide range of transducer operations
    * UTF-8 character coding
    * weighted transducers (basic functionality only)

### Downloads ###
Source code of the SFST tools
  * [SFST version 1.4.6i](http://www.cis.uni-muenchen.de/~schmid/tools/SFST/data/SFST-1.4.6i.tar.gz)

Precompiled morphological transducers
  * [SMOR](https://code.google.com/p/cistern/wiki/SMOR), a German finite-state morphology which is based on SFST.
  * [EMOR](http://www.cis.uni-muenchen.de/~schmid/tools/SFST/data/EMOR.tar.gz), an English finite-state morphology using SFST.

Documentation
  * short [manual](http://www.cis.uni-muenchen.de/~schmid/tools/SFST/data/SFST-Manual.pdf) (included in the source code package)
  * [tutorial](http://www.cis.uni-muenchen.de/~schmid/tools/SFST/data/SFST-Tutorial.pdf) on the implementation of computational morphologies (included in the source code package)

Packages (not necessarily up to date)
  * [Debian package](http://packages.debian.org/sid/sfst) for SFST (created by Francis Tyers)

### Publications ###

Please cite the following publication if you want to refer to the SFST tools:

Helmut Schmid, A Programming Language for Finite State Transducers, Proceedings of the 5th International Workshop on Finite State Methods in Natural Language Processing (FSMNLP 2005), Helsinki, Finland.


### Relationship to other FST Toolkits ###
There are two projects which extend the functionality of SFST in various ways:

  * Anssi Yli-Jyrä's [AFST](http://www.ling.helsinki.fi/~aylijyra/afst) toolkit is based on SFST

  * The [HFST](http://www.ling.helsinki.fi/kieliteknologia/tutkimus/hfst) tookit developed by Krister Lindén, Kimmo Koskenniemi, and colleagues was implemented on top of the three alternative FST libraries SFST, [OpenFST](http://www.openfst.org), and [foma](http://wiki.apertium.org/wiki/Foma).

### Contributions by other authors ###
  * Alex Linke provided an [interface](http://www.use-strict.de/software/fst2dot.html) to the Graphviz tool for the graphical output of transducers.
  * Sebastian Nagel wrote an [Emacs mode](http://www.cis.uni-muenchen.de/~schmid/tools/SFST/data/sfst.el) for editing transducer files and a [Perl program](http://www.cis.uni-muenchen.de/~schmid/tools/SFST/data/Fst2Dot.pl) which converts SFST transducers to the Graphviz format (similar to that of Alex Linke).
  * Stefan Evert also sent me a [Graphviz converter](http://www.cis.uni-muenchen.de/~schmid/tools/SFST/data/fst-draw).
  * Matthias Kistler provided a [highlighting mode](http://www.cis.uni-muenchen.de/~schmid/tools/SFST/data/vim-mode.tar.gz) for the VIM editor.
  * Toni Arnold developed a [Python interface](http://home.gna.org/pysfst/) for the SFST library and [Emores](http://home.gna.org/emores/), an Empirical MOrphological REaSoning engine for the automatic acquisition of lemmas from a word list.
  * Marius L. Jøhndal created a [Ruby interface](http://github.com/mlj/ruby-sfst/tree/master) for the SFST library.
  * [UIMA wrapper](http://code.google.com/p/dkpro-core-asl) for SFST (developed at the UKP Lab)


Please send comments, suggestions and bug reports to Helmut Schmid at FirstName.LastName@ims.uni-stuttgart.de. (Insert the name into the email address.)