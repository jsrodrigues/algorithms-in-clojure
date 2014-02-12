(ns org.gnu.algos.sorting
  (:import [java.util ArrayList]))

(defn bubblesort
  "Bubble sort algorithm"
  [inp]
  (let [work-list (ArrayList. inp)]
    (loop [last-elt (dec (count work-list))]
      (if (pos? last-elt)
        (do
          (dotimes [indx last-elt]
            (let [elt-a (.get work-list indx)
                  elt-b (.get work-list (inc indx))]
              (when (> elt-a elt-b)
                (.set work-list indx elt-b)
                (.set work-list (inc indx) elt-a))))
          (recur (dec last-elt)))
        work-list))))

(defn quicksort
  "Quicksort algorithm"
  ([inp]
     (let [inp-length (count inp)]
       (if (empty? inp)
         inp
         (let [[pivot-elt & other-elts] inp
               less-than-equal-pivot (filter #(>= pivot-elt %1) other-elts)
               greater-than-pivot (filter #(< pivot-elt %1) other-elts)]
           (lazy-cat (quicksort less-than-equal-pivot)
                     [pivot-elt]
                     (quicksort greater-than-pivot)))))))

(defn benchmark-sorts
  "Benchmark the various sorting functions"
  []
  (let [inp (take 1000 (repeatedly #(int (rand 1000))))
        srt-1 (time (do (println "Quicksort")
                        (quicksort inp)))
        srt-2 (time (do (println "Bubblesort")
                        (bubblesort inp)))]
    (= srt-1 srt-2)))
