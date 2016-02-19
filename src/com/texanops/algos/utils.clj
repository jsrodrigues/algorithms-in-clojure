(ns com.texanops.algos.utils)

(defn get-resource-content-dir []
  (format "%s/resources/content" (System/getProperty "user.dir")))

(defn get-content-file
  ([]
   (get-content-file "macbeth.txt"))
  ([file-name]
   (format "%s/%s" (get-resource-content-dir) file-name)))
