# clojure-lispkit

Translate LispKit LISP to combinator logic terms.
Run the resulting graph by spine reduction.

Over the years, I've written variants of this in XLISP (on an Atari ST),
in Ruby, in OMeta, and in Common Lisp. This is a push for final closure,
in Clojure.

Peter Henderson originally came up with LispKit LISP for his seminal
text book on "Functional Programming" [Hend 80], where he introduced
it as a LISP dialect to compile to SECD virtual machine code.

In the late 70s, David Turner already had published several papers
towards compiling functional languages (like Turner's SASL) to 
combinator logic terms [Turn 79a][Turn 79b].

In 1988, Antoni Diller combined (sic!) the two flows into his work
"Compiling Functional Languages" [Dill 88], which details how to
compile LispKit LISP down to combinatory logic terms.

## References

[Dill 88] @book{DBLP:books/daglib/0067389,
  author    = {Antoni Diller},
  title     = {Compiling functional languages},
  publisher = {Wiley},
  year      = {1989},
  isbn      = {978-0-471-92027-4},
  pages     = {I-XXI, 1-289},
  bibsource = {DBLP, http://dblp.uni-trier.de}
}

[Hend 80] @book{DBLP:books/daglib/0068837,
  author    = {Peter Henderson},
  title     = {Functional programming - application and implementation},
  publisher = {Prentice Hall},
  series    = {Prentice Hall International Series in Computer Science},
  year      = {1980},
  isbn      = {978-0-13-331579-0},
  pages     = {I-XI, 1-355},
  bibsource = {DBLP, http://dblp.uni-trier.de}
}

[Turn 79a] Turner, D.A. (1979). "A New Implementation Technique for Applicative Languages". Software - Practice and Experience 9: 31.

[Turn 79b]

## Usage

TODO

## License

Copyright © 2012 Helge Horch

Distributed under the GNU Public License, GPL v3.
