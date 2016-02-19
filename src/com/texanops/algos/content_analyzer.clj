(ns com.texanops.algos.content-analyzer
  (:require [com.texanops.algos.utils :refer [get-content-file]]))

(defn remove-punctuation [word]
  (try
    (let [word-without-punctuation
          (second (re-find #"^[\p{Punct}]{0,}([\w]+)[\p{Punct}]{0,}[\w]{0,}$" word))]
      (.toLowerCase word-without-punctuation))
    (catch Exception e
      "FAILED")))

(defn record-word [recorded-words word]
  (let [word-without-punctuation (remove-punctuation word)
        [word-count word-variations] (get recorded-words word-without-punctuation [0 #{}])]
    (assoc recorded-words word-without-punctuation [(inc word-count) (conj word-variations word)])))

(defn sanitize-input [input]
  (clojure.string/replace
    (clojure.string/replace input #"[!?:.;,]" " ")
    #"[-]{2}" " "))

(defn analyze-content
  ([]
   (analyze-content (get-content-file)))
  ([file-name]
   (let [all-words (clojure.string/split (sanitize-input (slurp file-name)) #"\s+")
         longer-words (filter #(> (count %) 4) all-words)
         word-counts (reduce record-word {} longer-words)]
     (into (sorted-map-by (fn [key1 key2]
                            (compare (first (word-counts key2))
                              (first (word-counts key1)))))
       word-counts))))
