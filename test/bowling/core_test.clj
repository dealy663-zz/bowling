(ns bowling.core-test
  (:require [clojure.test :refer :all]
            [bowling.core :refer :all]))

(def strike-frame (assoc frame :ball1 10))
(def spare-frame (assoc frame :ball1 4 :ball2 6))
(def final-frame (assoc-in score-card [:frames] (take 9 (repeat frame))))
(def final-frame-strike (assoc-in score-card [:frames] (conj (vec (take 8 (repeat frame))) strike-frame)))
(def final-frame-spare (assoc-in score-card [:frames] (conj (vec (take 8 (repeat frame))) spare-frame)))

(def complete-game (assoc-in score-card [:frames] (take 10 (repeat frame))))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))

(deftest validate-tester
  (testing "invalid inputs"
    (let [sc0 score-card]
      (is (false? (final-frame? sc0)))
      (is (final-frame? final-frame))
      (is (game-over? complete-game))

      (let [nsc (score-frame sc0 1 nil)
            exp (:last-exception nsc)]
        (is (not (nil? exp)))
        (is (= "ball2 must be an integer value >= 0" (.getMessage exp))))

      (let [nsc (score-frame sc0 nil 1)
            exp (:last-exception nsc)]
        (is (not (nil? exp)))
        (is (= "ball1 must be an integer value >= 0" (.getMessage exp))))

      (let [nsc (score-frame sc0 1 -1)
            exp (:last-exception nsc)]
        (is (not (nil? exp)))
        (is (= "A ball value can only be between 0 and 10" (.getMessage exp))))

      (let [nsc (score-frame sc0 -1 1)
            exp (:last-exception nsc)]
        (is (not (nil? exp)))
        (is (= "A ball value can only be between 0 and 10" (.getMessage exp))))


      (let [nsc (score-frame sc0 1 111)
            exp (:last-exception nsc)]
        (is (not (nil? exp)))
        (is (= "A ball value can only be between 0 and 10" (.getMessage exp))))


      (let [nsc (score-frame sc0 111 1)
            exp (:last-exception nsc)]
        (is (not (nil? exp)))
        (is (= "A ball value can only be between 0 and 10" (.getMessage exp))))

      (let [nsc (score-frame sc0 1 1 4)
            exp (:last-exception nsc)]
        (is (not (nil? exp)))
        (is (= "3 balls can only be thrown on the 10th frame" (.getMessage exp))))

      (let [nsc (score-frame final-frame 1 1 4)
            exp (:last-exception nsc)]
        (is (not (nil? exp)))
        (is (=  "ball3 can only be thrown in the final frame when ball1 is a strike" (.getMessage exp))))

      (let [nsc (score-frame final-frame 10 1 4)
            exp (:last-exception nsc)]
        (is (nil? exp)))

      )))
