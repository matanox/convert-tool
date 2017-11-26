(ns convert-tool.util
  (:require
    [clojure.test :refer :all]
    [clojure.pprint :refer [pprint]]
    [puget.printer :refer [cprint]]
    [clojure.inspector :as inspect :refer [inspect-tree]]
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
))


(defmacro is-function? [x]
  " just pulls in clojure.test's function? into scope "
  `(function? ~x))


(defn as-double [variable]
  " takes an argument of unknown primitive type, and transforms it to a float without crashing on any case.
    useful e.g. for data being read from text or csv files "
  (condp instance? variable
    String (Double/parseDouble variable)
    Long (double variable)
    (throw (Exception. (str "unexpected type: " (class variable))))))

(defn as-float [variable]
  " takes an argument of unknown primitive type, and transforms it to a float without crashing on any case.
    useful e.g. for data being read from text or csv files "
  (condp instance? variable
    String (Float/parseFloat variable)
    Long (float variable)
    (throw (Exception. (str "unexpected type: " (class variable))))))


(with-test
  (defn interval-points
    [start end number-of-intervals]
    {:pre [(number? start) (number? end) (number? number-of-intervals)]}

    " returns an ordered list of evenly-spaced interval points
      between the start and end numbers, the start and end included.

      compared to clojure.core's `range` this is inclusive, numerically stable, and always returns doubles"

    (let
      [step-size (/ (- end start) number-of-intervals )]
      (map
        double
        (range start (+ step-size end) step-size))))

  (do
    (assert (= '(0 0.5 1) (interval-points 0 1 2)))
    (assert (some #(= % 0.22) (interval-points 0 1 50)))))


(defn abs-distance
  [a b]
  (let [distance (- a b)]
       (max distance (- distance))))

(defn square [distance] (* distance distance))


(defn divide-or-undef [a b]
  " divides a by b, or returns :undef if b is zero "
  (if (== b 0) :undef ; type independant numeric equality check
    (float (/ a b))))


(defn divide-or-default [a b default]
  " divides a by b, or returns default value if b is zero "
  (if (== b 0) default ; type independant numeric equality check
    (float (/ a b))))


(defn has-nth? [col n]
  (let [max-index (- (count col) 1)]
    (>= max-index n)))


(defn capped! [coll]
  " for use when outputing collections as exception text
    alternatively `(binding [*print-length* 4] (print-str coll))` "
  (let [max-to-include 4
        maybe-truncated (take max-to-include coll)]
    (if (> (count coll) max-to-include)
      (print-str maybe-truncated "...")
      (print-str maybe-truncated))))


(defn get! [map key]
  " get the value of key in the map, or throw if the key is not found "
  (or (get map key) (throw (Exception. (str "key " key " not found in map " (capped! map))))))


(defn get-single [pred coll]
  " get collection element matching the given predicate,
    expecting only a single match to exist "
  (let [matches (filter pred coll)]
    (case (count matches)
      1 (first matches)
      0 (throw (Exception. (str "no match found in the collection: " (capped! matches))))
      (throw (Exception. (str "a unique match is expected but multiple matches are found in the collection:" (capped! matches)))))))


(defn map-key-equals [map key expected-value]
  " checks whether the value of the given key equals the expected value.
    if the given key does not exists, throws an exception. "
  (= (get! map key) expected-value))


(defn spit-create [& args]
  " like spit, but creates the full path to the file if it doesn't exist yet "
  (let [path (drop-last args)
        file (apply io/file path)]
    (io/make-parents file)
    (spit file (last args))))


(defmacro symbol-to-key-pair [value]
  `{(keyword (quote ~value)) ~value})


(with-test
  (defmacro to-map [& vars]
    " returns a map consisting of each supplied var turned into a key-value pair,
      where the key is the var name and the value is the var's value. useful for
      stuffing a bunch of vars into a map "
    `(merge
       ~@(map
           (fn [arg] {(keyword (name arg)) arg})
           vars)))

  (is (=
        (let
          [foo 1
           bar (* foo 2)]
          (to-map foo bar))
        {:foo 1 :bar 2})))


(defn third [x]
  " returns third element of sequence "
  (nth x 2))


(defn get-file-object
  [path-seq]
  {:pre [(seq? path-seq) (every? string? path-seq)]}
  " gets a java file object from a path sequence "
  (apply io/file path-seq))


(defn file-with-parents [path filename]
  {:pre [(list? path) (string? filename)]}
  " returns file object ready to use, creating its parents hierarchy if it does not exist yet "
  (let
    [full-path (concat path (list filename))
      file (apply io/file full-path)]
    (io/make-parents file)
    file))


(defn read-csv [base-path input-file-name]

  " loads a headers-row prefixed csv file.
    note that columns having an empty string header are silently dropped.

    TODO: enforce distinct header names to avoid undeterminism when there are duplicate header names "

  (let [file (io/file base-path input-file-name)
        input-file (read-string (slurp file))]

    (println (str "reading " (.getPath file)))

    (with-open [input-file (io/reader file)]
      (let
         [content (csv/read-csv input-file)
          headers
            (map keyword (first content))
          data
            (map
              (fn [content-row]
                (into (hash-map)
                  (remove ; discards columns having an empty-string header
                    #(= (key %) (keyword ""))
                    (zipmap headers content-row))))
              (rest content))

          headers
            (remove ; discards empty-string headers
              #(= % (keyword ""))
              headers)]

          (println (count data) "rows read")

          (to-map
             headers
             data)))))
