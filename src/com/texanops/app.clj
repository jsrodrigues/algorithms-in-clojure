(ns com.texanops.app
  (:gen-class))

(defn -main [& args]
  (println "Algorithms in Clojure and misc experimental stuff."))

(defn setup-aliases []
  (require
    '[com.texanops.algos.content-analyzer :as ca]
    '[com.texanops.algos.sorting :as srt]))
