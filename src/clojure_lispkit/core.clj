; vim: ts=2:sw=2:et:sts=2
;;;; ======================================================================
;;;; clojure-lispkit/core.clj -- LispKit-to-combinators compiler in Clojure
;;;; hh 04nov12 started
;;;; hh 18nov12 integrated matchure, more unit tests
;;;; ======================================================================

(ns clojure-lispkit.core
  (:use matchure))

;;; ======================================================
;;; LispKit sexprs will have dotted pairs. Deal with them.
;;; ======================================================

(defn dotted?
  "Answer whether the given sexpr is a dotted pair, or a list ending in a dotted pair"
  [sexpr]
  (let [n (count sexpr)]
       (and (>= n 3)
	    (= (nth sexpr (- n 2)) '.))))

(defn ensure-dotted
  "Throw an IllegalArgumentException unless the given sexpr is a dotted pair"
  [sexpr]
  (when-not (dotted? sexpr)
	    (throw (IllegalArgumentException. "Not a dotted pair"))))

(defn car
  "Answer the head of a dotted pair"
  [sexpr]
  (ensure-dotted sexpr)
  (first sexpr))

(defn cars
  "Answer the sequence of heads of a dotted pair"
  [sexpr]
  (ensure-dotted sexpr)
  (drop-last 2 sexpr))

(defn cdr
  "Answer the tail of a dotted pair"
  [sexpr]
  (ensure-dotted sexpr)
  (last sexpr))

;;; ========================================================
;;; LispKit abstract syntax, per LispKit ftp package summary
;;; ========================================================

(defn lispkit-number?
  "LispKit only handles integer numbers"
  [x]
  (integer? x))

(defn lispkit-symbol?
  "LispKit symbols may not start like a number"
  [x]
  (and (symbol? x)
       (re-seq #"^[^-+0-9\s\.()][^\s\.()]*$" (name x))))

(def NIL '())

(defn lispkit-nil?
  [x]
  (= x NIL))

(defn lispkit-atom?
  "Answer whether the argument is an atom in LispKit
   (either an integer, a symbol, or NIL)"
  [x]
  (or (lispkit-number? x)
      (lispkit-symbol? x)
      (lispkit-nil? x)))

;;; ==========================================================================
;;; bracket abstraction: Turner's algorithm A, with optimizations from B and C
;;; ==========================================================================

(defn abstract-atom
  "Abstract a variable from a LispKit atom"
  [var atom]
  (if (= var atom)
      'I
      (list 'K atom)))

(defn abstract
  "Abstract a variable from a LispKit form or atom"
  [var form]
  (if (lispkit-atom? form)
      (abstract-atom var form)
      (reduce #(list 'S %1 %2)
	      (map #(abstract var %1) form))))

(defn abstract*
  "Abstract a sequence of variables from a given form or atom"
  [vars form]
  (if (empty? vars)
      form
      (abstract* (rest vars)
		 (abstract (first vars) form))))

(defn optimize-term
  "If possible, simplify a combinator term
   using rules from Turner's algorithm B and C"
  [term]
  (cond-match term
	      ; optimization rules from algorithm B:
	      ['S ['K ?a] ['K ?b]]    (list 'K (list a b))
	      ['S ['K ?a] 'I]	      a
	      ['S ['K ?a] ?b]	      (list 'B a b)
	      ['S ?a ['K ?b]]	      (list 'C a b)
	      ; optimization rules from algorithm C:
	      ['S ['B ?a ?b] ?c]      (list 'S' a b c)
	      ['B [?a ?b] ?c]	      (list 'B' a b c)
	      ['C ['B ?a ?b] ?c]      (list 'C' a b c)
	      ; if nothing matched, return the unoptimized term:
	      ? term))

(defn optimize
  "If possible, simplify a combinator term recursively (depth-first)"
  [term]
  (if (lispkit-atom? term)
      term
      (optimize-term (map optimize term))))

(defn translate-literal
  "Translate a LispKit literal into a combinator term"
  [expr]
  (if (lispkit-atom? expr)
      expr
      (list 'CONS
	    (translate-literal (car expr))
	    (translate-literal (cdr expr)))))

(defn translate
  "Translate a LispKit form into a combinator term"
  [form]
  (optimize
    (cond-match form
      ['quote ?expr]
	(translate-literal expr)
      ['lambda ?args ?expr]
	(abstract* (reverse args)
		   (translate expr))
      ['let ?expr & ?decls]
	(let [vars (map car decls)
	      values (map cdr decls)]
	     (concat (abstract* (reverse vars)
				(translate expr))
		     (map translate values)))
      [?func & ?args]
	(concat (translate func)
		(map translate args))
      ? form)))
