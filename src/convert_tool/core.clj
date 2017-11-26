(ns convert-tool.core
  (:require
     [clojure.pprint :refer [pprint]]
     [puget.printer :refer [cprint]]
     [clojure.inspector :as inspect :refer [inspect-tree]]
     [clojure.data.csv :as csv]
     [clojure.java.io :as io]
     [convert-tool.util :refer :all]
     [convert-tool.transform :refer :all]
     [clojure.set :refer :all]))

(defn process [input-file-name output-file-name]

  "oh well, transducers were recommended for streaming, but for marginal reasons to our use case here..."

  (println "processing" input-file-name)

  (with-open
    [input-file (io/reader input-file-name)
     output-file (io/writer output-file-name)]

    (doseq
      [[index line] (map-indexed vector (line-seq input-file))]
      (csv/write-csv output-file (transform ud-spreadsheet-transform index line)))

    (println "output file" output-file-name "ready")))


(defn -main
  [input-file-name output-file-name]
  (process input-file-name output-file-name))
