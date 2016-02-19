(ns com.texanops.algos.content-analyzer
  (:require [com.texanops.algos.utils :refer [get-content-file]]))

;; Looked for the FAILED key in the result. Indicates what failed the regex.
;; Also, this is equating words like to-morrow and to-night.
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

;; Removes punctuation and double hypens from the input.
(defn sanitize-input [input]
  (clojure.string/replace
    (clojure.string/replace input #"[!?:.;,]" " ")
    #"[-]{2}" " "))

(defn analyze-content
  ([]
   (analyze-content (get-content-file)))
  ([file-name]
   (let [all-words (clojure.string/split (sanitize-input (slurp file-name)) #"\s+")
         words-with-more-than-4chars (filter #(> (count %) 4) all-words)
         word-stats (reduce record-word {} words-with-more-than-4chars)
         words-sorted-by-frequency (sort #(let [[k1 [wc1 wvars1]] %1
                                                [k2 [wc2 wvars2]] %2]
                                            (> wc1 wc2)) word-stats)
         result (java.util.LinkedHashMap.)]
     (doseq [[key value] words-sorted-by-frequency]
       (.put result key value))
     result)))
