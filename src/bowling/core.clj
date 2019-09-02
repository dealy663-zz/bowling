(ns bowling.core)

(def frame {:ball1 0
            :ball2 0})

(def score-card {:frames (into [] (take 10 (repeat frame)))
                 :score  0})


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Use Ctrl+c to exit.")

  (while true
    (let [cmd (slurp *in*)]
      (println (str "you entered: " cmd)))))