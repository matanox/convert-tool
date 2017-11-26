(ns convert-tool.transform
  (:require
     [clojure.pprint :refer [pprint]]
     [puget.printer :refer [cprint]]
     [clojure.inspector :as inspect :refer [inspect-tree]]
     [clojure.data.csv :as csv]
     [clojure.java.io :as io]
     [convert-tool.util :refer :all]
     [clojure.set :refer :all]
     [clojure.string :refer [split]]))

(defn ud-spreadsheet-transform [token-num token line-num]
  {:pre
    [(number? token-num)
     (string? token)
     (number? line-num)]}

  "a specific transorfmer into matan's ud spreadsheet format"

  [(inc line-num) (inc token-num) token])

(defn transform [transform-fn line-num input-line]
  {:pre [(number? line-num)]}

  "transforms a single input line"

  (let [tokens (split input-line #"\s")
        token-index (range)]
    (map
      transform-fn
      token-index
      tokens
      (repeat line-num))))


