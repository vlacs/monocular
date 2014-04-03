(ns monocular.core
  (:require [monocular.data-map :refer [map->grammar map->transforms]]
            [instaparse.core :as insta]
            [instaparse.combinators :refer [ebnf]]))

(def base-grammar
  (ebnf
    "search = term (<whitespace> term)*
     default = value
     <value> = quoted-value | raw-value
     <raw-value> = #'[^\":\\s]+'
     <quoted-value> = <'\"'> #'[^\"]*' <'\"'>
     whitespace = #'\\s+'"))

(defn parse
  "Parse a search without transforming it"
  [searcher string]
  (vec (rest (insta/parse (:parser searcher) string))))

(defn transform
  "Transform a parsed search into a function that takes a data set and returns a result"
  [searcher parse-tree]
  (->> parse-tree
       (insta/transform (:transforms searcher))
       (apply (comp))))

(defn search
  "Parse, transform, and search in one step"
  [searcher string data-set]
  ((transform searcher (parse searcher string)) data-set))

(defrecord Searcher [parser transforms]
  clojure.lang.IFn
  (invoke [searcher string data-set] (search searcher string data-set))
  (applyTo [searcher args] (apply search searcher args)))

(defn searcher
  [data-map]
  (Searcher. (insta/parser (merge base-grammar (map->grammar data-map)) :start :search)
             (map->transforms data-map)))
