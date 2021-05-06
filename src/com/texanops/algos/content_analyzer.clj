(ns com.texanops.algos.content-analyzer
  (:require [com.texanops.algos.utils :refer [get-content-file]]
            [clojure.string]))

(defn remove-punctuation [word]
  (try
    (let [word-without-punctuation (if-let [captured-word-list
                                            (re-find #"^[\p{Punct}]{0,}([\w]+[-]{0,1}[\w]+)[\p{Punct}]{0,}[\w]{0,}$"
                                                     word)]
                                     (second captured-word-list)
                                     word)]
      (.toLowerCase word-without-punctuation))
    (catch Exception e
      (format "FAILED: %s" (.getMessage e)))))

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
         words-sorted-by-frequency (sort #(let [[_ [wc1 _]] %1
                                                [_ [wc2 _]] %2]
                                            (> wc1 wc2)) word-stats)
         result (java.util.LinkedHashMap.)]
     (doseq [[key value] words-sorted-by-frequency]
       (.put result key value))
     result)))

;; Note that macbeth and analyze-content are two alternative solutions
;; For some reason (that I've not currently analyzed, the macbeth one
;; produces fewer words than analyze-content. Also analyze-content's
;; output, while verbose is not too user friendly
(defn macbeth
  ([]
   (macbeth (get-content-file)))
  ([file-name]
   (let [all-words (clojure.string/split (clojure.string/replace
                                          (clojure.string/replace
                                           (slurp file-name)
                                           #"[\p{Punct}]+" " ")
                                          #"\s+" "^") #"[\^]")
         longer-words (filter #(> (count %) 4) all-words)
         word-counts (reduce #(let [word (.toLowerCase %2)]
                                (assoc %1 word (inc (get %1 word 0)))) {} longer-words)]
     (into (sorted-map-by (fn [key1 key2]
                            (compare [(get word-counts key2) key2]
                                     [(get word-counts key1) key1])))
           word-counts))))
