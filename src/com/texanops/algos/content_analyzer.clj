(ns com.texanops.algos.content-analyzer
  (:require [com.texanops.algos.utils :refer [get-content-file]]))

(defn analyze-content
  ([]
   (analyze-content (get-content-file)))
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
                            (compare (word-counts key2)
                              (word-counts key1))))
       word-counts))))
