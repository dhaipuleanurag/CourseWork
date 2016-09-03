;; (fromTo k n) returns the list of integers from k to n. The size
;; of the problem can be seen as the difference
;; between k and n.
;; Base Case: if k = n (i.e. if the size of the problem is 0), then
;; the result is the list containing only n.
;; Hypothesis: Assume (fromTo (+ k 1) n) returns the list of integers
;; from k+1 to n.
;; Recursive step: (fromTo k n) = (cons k (FromTo (+ k 1) n)
(define (fromTo k n)
    (cond ((= k n) (list n))
          (else (cons k (fromTo (+ k 1) n))))
          )


;; (removeMults m L) returns the list of integers from L that are not multiples of m.
;; Base Case: if L = null (i.e. if the list is empty), then
;; return a empty list.
;; Hypothesis: Assume (removeMults m (cdr L)) returns a list containing integers from (cdr L)
;; that are not multiples of m.
;; Recursive step: (removeMults m L) = (cons (car L)(removeMults m (cdr L))
(define (removeMults m L)
    (cond((null? L) '())
         (else
          (cond ((= 0 (modulo (car L) m)) (removeMults m (cdr L)))
              (else (cons (car L)(removeMults m (cdr L))))
              ))))

;; (removeAllMults L) returns a list containing those elements of L that are
;; not multiples of each other.
;; Base Case: if L = null (i.e. the list is empty), then
;; return empty list.
;; Hypothesis: Assume (removeAllMults (removeMults (car L) L)) returns the list of integers
;; containing those elements of L, after having removed multiples of previous, that are not multiples
;; of each other
;; Recursive step: (removeAllMults L) = (cons (car L) (removeAllMults (removeMults (car L) L)))
(define (removeAllMults L)
    (cond ((null? L) '())
          (else (cons (car L) (removeAllMults (removeMults (car L) L))))
          ))

;; (removeAllMults L) is a generalized function that would perform the job of prime number generation had
;; been started from 2.
;; The idea is to keep first element in list and remove all its multiples and pass the new list after having removed multiples
;; and the first element.
(define (primes n)
    (removeAllMults (fromTo 2 n))
    )


;; (maxDepth L) returns the maximum nesting depth of any element within L, such that
;; top most elements are at depth 0.
;; Base Case: if L is not list (i.e. say just a number), then
;;            we return -1 because a element in a list is considered as of depth 0.
;;            if L is list without any element then max deoth is 0.
;; Hypothesis: Assume maxDepth(car L) and maxDepth(cdr L)returns maximum depth of (car L) and (cdr L)
;; respectively.
;; Recursive step: maxDepth L = (getMax(+ (maxDepth (car L)) 1) (maxDepth (cdr L)))
;; we compare if the recursive depth of car is more or cdr. We are adding after calculating depth of (car L) because with car we have
;; gone one level lower deeper into nesting.
(define (getMax x y)
(if(> x y)x y))

(define (maxDepth L)
    (cond ((not (list? L)) -1)
          ((null? L) 0)
          (else (getMax(+ (maxDepth (car L)) 1) (maxDepth (cdr L))))))

;; (prefix exp) returns the prefix arithematic expression of the infix expression passed as list.
;; Base Case: if L is not list (i.e. say just a number), then
;;            we return that number.
;;            if L is list whose length is 1, then return prefix of that element from the list
;; Hypothesis: Assume prefix(car exp) and prefix(cdr (cdr exp))returns prefix expression for (car exp) and (cdr (cdr exp))
;; respectively.
;; Recursive step: prefix exp = (list (cadr exp) (prefix (car exp)) (prefix (cdr (cdr exp))))
;; we basically are changing the position of operator and calling prefix function on operands, which could be infix arithematic operations in
;; themselves.
(define (prefix exp)
    (cond ((not(list? exp)) exp)
          ((= (length exp) 1) (prefix (car exp)))
          (else (list (cadr exp) (prefix (car exp)) (prefix (cdr (cdr exp))) )
      )))

;; (composition L) returns a function that is composition of all the functions in the list L.
;; Base Case: if L is of length 1 then we return a lambda function with that one function. 
;; Hypothesis: Assume composition(cdr L) returns a composition of all functions in (cdr L)
;; Recursive step: (composition L) = ((car L) ((composition (cdr L)) x))
(define (composition L)
  (lambda (x)
  (cond ((= (length L) 1)((car L) x))
         (else ((car L) ((composition (cdr L)) x)))
         )
          ))

(define f (composition (list (lambda (x) (+ x 1)) (lambda (x) (* x 2)))))