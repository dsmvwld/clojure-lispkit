; vim: ts=2:sw=2:et:sts=2

(ns clojure-lispkit.core-test
  (:use clojure.test
        clojure-lispkit.core))

; TODO how to get shorter stack traces? (def *stack-trace-depth* 2)

(deftest dotted-test
  (testing "detection of dotted pairs"
    (testing "positive"
      (are [x] (dotted? x) 
	'(a . b)
	; TODO compact notation '(a.b) not supported yet
	'(a . ())
	'(a . nil)
	'(a b c . d)))
    (testing "negative"
      (are [x] (not (dotted? x))
	'(1 . 2 3)
	'(1 2 3)
	'(a .)
	'(.)
	'(. b)))))

(deftest car-test
  (testing "accessing the head of a dotted pair (or list ending in one)"
    (are [x y] (= (car x) y)
	 '(a . b) 'a
	 '(a b . c) 'a)))
    
(deftest cars-test
  (testing "accessing the sequence of heads of a dotted pair"
    (are [x y] (= (cars x) y)
	 '(a . b) '(a)
	 '(a b . c) '(a b))))
    
(deftest cdr-test
  (testing "accessing the tail of a dotted pair"
    (are [x y] (= (cdr x) y)
	 '(a . b) 'b
	 '(a b . c) 'c)))
    
(deftest numbers-test
  (testing "LispKit numbers"
    (testing "positive"
      (are [x] (lispkit-number? x)
	45
	+137
	-27
	0
	+0
	-0))
    (testing "negative"
      (are [x] (not (lispkit-number? x))
	3.14159
	1/2))))
(deftest numbers-test
  (testing "LispKit numbers"
    (testing "positive"
      (are [x] (lispkit-number? x)
	45
	+137
	-27
	0
	+0
	-0))
    (testing "negative"
      (are [x] (not (lispkit-number? x))
	3.14159
	1/2))))
(deftest cdr-test
  (testing "accessing the tail of a dotted pair"
    (are [x y] (= (cdr x) y)
	 '(a . b) 'b
	 '(a b . c) 'c)))
    
(deftest numbers-test
  (testing "LispKit numbers"
    (testing "positive"
      (are [x] (lispkit-number? x)
	45
	+137
	-27
	0
	+0
	-0))
    (testing "negative"
      (are [x] (not (lispkit-number? x))
	3.14159
	1/2))))
(deftest numbers-test
  (testing "LispKit numbers"
    (testing "positive"
      (are [x] (lispkit-number? x)
	45
	+137
	-27
	0
	+0
	-0))
    (testing "negative"
      (are [x] (not (lispkit-number? x))
	3.14159
	1/2))))

(deftest symbols-test
  (testing "LispKit symbols"
    (testing "positive"
      (are [x] (lispkit-symbol? x)
	'Hello
	'hello
	'Hello_world
	'X32
	'X+32
	(symbol ":**@?")))
    (testing "negative"
      (are [x] (not (lispkit-symbol? x))
	45
	+137
	-0
	'.
	(symbol "f()")
	'v.x))))

(deftest nil-test
  (testing "LispKit NIL/() atom-list"
    (is (lispkit-nil? '()))
    (is (lispkit-nil? NIL))))

(deftest abstract-I-test
  (testing "abstracting to I"
    (is (= (abstract 'x 'x) 'I))
    (is (= (abstract 'x '(x)) 'I))
    (is (= (abstract 'x '((x))) 'I))))

(deftest abstract-K-test
  (testing "abstracting to K"
    (is (= (abstract 'x '3) '(K 3)))
    (is (= (abstract 'x '(3)) '(K 3)))
    (is (= (abstract 'x '((3))) '(K 3)))))

(deftest abstract-S-test
  (testing "abstracting to S"
    (is (= (abstract 'x '(plus 1))
	   '(S (K plus) (K 1))))))

(deftest abstract-inc-test
  (testing "abstracting inc(x)"
    (is (= (abstract 'x '(plus 1 x))
	   '(S (S (K plus) (K 1)) I)))))

(deftest abstract-fac-test-no-opt
  (testing "translating fac(x)"
    (is (= (abstract 'x
		     '(cond (eq 0 x) 1 (times x (fac (minus x 1)))))
	   '(S (S (S (K cond) (S (S (K eq) (K 0)) I))
		  (K 1)) (S (S (K times) I) (S (K fac)
	       (S (S (K minus) I) (K 1)))))))))

(deftest abstract-fac-test-opt
  (testing "translating fac(x)"
    (is (= (optimize (abstract 'x
			       '(cond (eq 0 x) 1 (times x (fac (minus x 1))))))
	   '(S (C (B cond (eq 0)) 1)
	       (S times (B fac (C minus 1))))))))

(deftest translate-literals ; [Dill 88] pp. 67/68
  (testing "atoms"
    (is (= (translate-literal '7) 7))
    (is (= (translate-literal 'seven) 'seven))
    (is (= (translate-literal NIL) NIL))
    (is (= (translate-literal '()) NIL)))
  (testing "dotted pair"
    (is (= (translate-literal '(alpha . beta))
	   '(CONS alpha beta))))
  (testing "short list"
    (is (= (translate-literal '(alpha beta))
	   '(CONS alpha (CONS beta NIL)))))
  (testing "longer list"
    (is (= (translate-literal '(alpha (beta gamma) delta))
	   '(CONS alpha (CONS (CONS beta (CONS gamma NIL))(CONS delta NIL)))))))

