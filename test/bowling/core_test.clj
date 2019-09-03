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
        (is (nil? exp))))))

(deftest update-tester
  (testing "update-last-frame spare"
    (let [sc1 (update-in score-card [:frames] conj (assoc frame :ball1 5 :ball2 5 :score 10))
          nxf (assoc frame :ball1 5 :ball2 3 :score 8)
          sc2 (update-last-frame sc1 nxf)]
      (is (= 15 (-> sc2 :frames (nth 0) :score)))))

  (testing "score-frame with strikes"
    (let [sc1 (update-in score-card [:frames] conj (assoc frame :ball1 10 :ball2 0 :score 10))
          nxf (assoc frame :ball1 7 :ball2 3 :score 10)
          sc2 (update-last-frame sc1 nxf)]
      (is (= 20 (-> sc2 :frames (nth 0) :score)))

      ;; simulating a strike in the 1st 2 frames final score for frame1 should be updated along
      ;; with final score of frame2
      (let [sc1 (score-frame score-card 10 0)]              ;; add first frame
        (is (= 1 (-> sc1 :frames count)))
        (is (= 10 (-> sc1 :frames (nth 0) :score)))
        (is (= 10 (get-total-score sc1)))

        (let [sc2 (score-frame sc1 10 0)]
          (is (= 2 (-> sc2 :frames count)))
          (is (= 20 (-> sc2 :frames (nth 0) :score)))
          (is (= 10 (-> sc2 :frames (nth 1) :score)))
          (is (= 30 (get-total-score sc2)))

          (let [sc3 (score-frame sc2 7 0)]
            (is (= 3 (-> sc3 :frames count)))
            (is (= 27 (-> sc3 :frames (nth 0) :score)))
            (is (= 17 (-> sc3 :frames (nth 1) :score)))
            (is (= 51 (get-total-score sc3)))

            (let [sc4 (score-frame sc3 0 4)]
              (is (= 4 (-> sc4 :frames count)))
              (is (= 17 (-> sc4 :frames (nth 1) :score)))
              (is (= 7 (-> sc4 :frames (nth 2) :score)))
              (is (= 4 (-> sc4 :frames (nth 3) :score)))
              (is (= 55 (get-total-score sc4)))

              (let [sc5 (score-frame sc4 10 0)]
                (is (= 4 (-> sc5 :frames (nth 3) :score)))
                (is (= 10 (-> sc5 :frames (nth 4) :score)))
                (is (= 65 (get-total-score sc5)))

                (let [sc6 (score-frame sc5 5 4)]
                  (is (= 19 (-> sc6 :frames (nth 4) :score)))
                  (is (= 9 (-> sc6 :frames (nth 5) :score)))
                  (is (= 83 (get-total-score sc6)))

                  (let [sc7 (score-frame sc6 7 3)]
                    (is (= 93 (get-total-score sc7)))

                    (let [sc8 (score-frame sc7 3 3)]
                      (is (= 13 (-> sc8 :frames (nth 6) :score)))
                      (is (= 102 (get-total-score sc8)))

                      (let [sc9 (score-frame sc8 10 0)]
                        (is (= 112 (get-total-score sc9)))

                        (let [sc10 (score-frame sc9 10 7 1)]
                          (is (= 27 (-> sc10 :frames (nth 8) :score)))
                          (is (= 18 (-> sc10 :frames (nth 9) :score)))
                          (is (= 147 (get-total-score sc10))))))))))))))))
