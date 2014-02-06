(ns monocular.core
  (:require [monocular.data-map :refer [map->grammar map->transforms]]
            [instaparse.core :as insta]
            [instaparse.combinators :refer [ebnf]]
            [datomic.api :as d]))

(def base-grammar
  (ebnf
    "search = term (<whitespace> term)*
     default = value
     <value> = quoted-value | raw-value
     <raw-value> = #'[^\":\\s]+'
     <quoted-value> = <'\"'> #'[^\"]*' <'\"'>
     whitespace = #'\\s+'"))

(def base-transforms
  {:search (comp vec list)})

(defn searcher
  [data-map]
  {:parser (insta/parser (merge base-grammar (map->grammar data-map)) :start :search)
   :transforms (merge base-transforms (map->transforms data-map))})

(defn search
  [searcher string]
  (->> string
      (insta/parse (:parser searcher))
      (insta/transform (:transforms searcher))))
