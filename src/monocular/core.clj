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

;; We may want to have the option to change this. For our original
;; idea of the transform functions taking a Datomic query and
;; returning a Datomic query, this is what we want, but if you're
;; doing SQL, you may want your transform functions to return strings
;; that are put together afterward.
(def base-transforms
  {:search comp})

(defn search
  [searcher string]
  (->> string
      (insta/parse (:parser searcher))
      (insta/transform (:transforms searcher))))

(defrecord Searcher [parser transforms]
  clojure.lang.IFn
  (invoke [searcher string] (search searcher string))
  (applyTo [searcher args] (apply search searcher args)))

(defn searcher
  [data-map]
  (Searcher. (insta/parser (merge base-grammar (map->grammar data-map)) :start :search)
             (merge base-transforms (map->transforms data-map))))

(defmacro defsearch
  [name data-map data-set]
  `(let [searchersym# (searcher ~data-map)]
     (defn ~name [search#] ((searchersym# search#) ~data-set))))
