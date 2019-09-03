(ns bowling.core)

;; may want to do more than just track the pins knocked down not sure yet
(def frame {:ball1 0
            :ball2 0
            :ball3 nil})


;; Assuming a standard game of 10 frames, prob will just manage the spares and
;; strikes in the scoring section func
(def score-card {:frames         []
                 :score          0
                 :last-exception nil})

(defn final-frame?
  "Returns true if this is the final frame of the game

   score-card    The score card representing the game being played"
  [score-card]

  (= 9 (-> score-card :frames count)))

(defn game-over?
  "Returns true if this game has finished

   score-card   The score card representing the game being played"
  [score-card]

  (= 10 (-> score-card :frames count)))


(defn validate
  "Validates that the input to the score-frame function make sense

   score-card  The score card representing the game being played
   ball1       The number of pins knocked down on the first throw
   ball2       The number of pins knocked down on the second throw
   ball3       The number of pins knocked down on the third throw

   throws an Exception if there is an error"
  [score-card ball1 ball2 ball3]

  (cond
    (not (or (final-frame? score-card)
             (nil? ball3)))
    (throw (Exception. "3 balls can only be thrown on the 10th frame"))

    (not (integer? ball1))
    (throw (Exception. "ball1 must be an integer value >= 0"))

    (not (integer? ball2))
    (throw (Exception. "ball2 must be an integer value >= 0"))

    (and (final-frame? score-card)
         (not (integer? ball3)))
    (throw (Exception. "ball3 must be an integer value >= 0"))

    (and (final-frame? score-card)
         (integer? ball3)
         (< ball1 10))
    (throw (Exception. "ball3 can only be thrown in the final frame when ball1 is a strike"))

    (or (> ball1 10) (< ball1 0)
        (> ball2 10) (< ball2 0))
    (throw (Exception. "A ball value can only be between 0 and 10"))

    (and (not (nil? ball3))
         (or (> ball3 10) (< ball3 0)))
    (throw (Exception. "A ball value can only be between 0 and 10"))

    (>= (-> score-card :frames count) 10)
    (throw (Exception. "The game is over we hope you enjoyed your game at Rokt Bowling."))))

(defn update-penultimate-frame
  "update the values of the second to last frame

   score-card       The score card representing the game being played
   current-frame    The scores for the frame that was just bowled"
  [score-card current-frame]

  (let [cc2 (-> score-card :frames count (- 2))]
    (if (>= cc2 0)
      (let [pen-frame (-> score-card :frames (nth cc2))]
        (if (= 10 (:ball1 pen-frame))
          (update-in score-card [:frames cc2 :score] + (:ball1 current-frame))
          score-card))
      score-card)))

(defn update-last-frame
  "update the values of the previous frame

   score-card         The score card representing the game being played
   current-frame      The scores for the frame that was just bowled

   returns the score-card with the last frame with the updated score if there was a strike or spare"
  [score-card current-frame]

  (let [cc (-> score-card :frames count dec)]               ;; nothing to update if 1st frame
    (if (>= cc 0)
      (let [last-frame (-> score-card :frames last)]
        (if (= 10 (:ball1 last-frame))                      ;; there was a strike
          (let [new-sc (update-in score-card [:frames cc :score] + (:ball1 current-frame) (:ball2 current-frame))]
            (update-penultimate-frame new-sc current-frame))
          (if (= 10 (:score last-frame))                    ;; the spare was picked up if score is 10 but ball1 wasn't
            (update-in score-card [:frames cc :score] + (:ball1 current-frame))
            score-card)))
      score-card)))

(defn score-frame
  "Scores a bowled frame.

   score-card  The score card representing the game being played
   ball1       The number of pins knocked down on the first throw
   ball2       The number of pins knocked down on the second throw
   ball3       (optional) The number of pins knocked down on the third throw of the
               10th frame only

   returns a score-card including the latest frame bowled"

  ([score-card ball1 ball2]

   (score-frame score-card ball1 ball2 nil))

  ([score-card ball1 ball2 ball3]
   (try
     (validate score-card ball1 ball2 ball3)                     ;; check inputs

     (let [ball3      (if (nil? ball3) 0 ball3)
           new-frame  (assoc frame :ball1 ball1 :ball2 ball2 :ball3 ball3
                                   :score (+ ball1 ball2 ball3)) ;; setup new frame
           new-sc     (update-last-frame score-card new-frame)]  ;; update previous frames
       (update-in new-sc [:frames] conj new-frame))              ;; add new frame to score-card
     (catch Exception exp
       (assoc score-card :last-exception exp)))))

(defn get-total-score
  "Returns the current score of the game, total of all frames

   score-card   The score card representing the game being played"
  [score-card]

  (reduce + 0 (map #(:score %) (:frames score-card))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Use Ctrl+c to exit.")

  (while true
    (let [cmd (slurp *in*)]
      (println (str "you entered: " cmd)))))