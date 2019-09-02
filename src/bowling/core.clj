(ns bowling.core)

;; may want to do more than just track the pins knocked down not sure yet
(def frame {:ball1 0
            :ball2 0})

;; Assuming a standard game of 10 frames, prob will just manage the spares and
;; strikes in the scoring section func
(def score-card {:frames []
                 :score  0})


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

   ))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Use Ctrl+c to exit.")

  (while true
    (let [cmd (slurp *in*)]
      (println (str "you entered: " cmd)))))