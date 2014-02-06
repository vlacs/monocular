(ns monocular.core
  (:require [monocular.data-map :refer [map->grammar]]
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

;; this will do more than just create an instaparser parser in the future
(defn searcher
  [data-map]
  (insta/parser (merge base-grammar (map->grammar data-map)) :start :search))

;; ..and this will do more than just pass things to instaparse.core/parse
(defn search
  [searcher string]
  (insta/parse searcher string))

; todo: rework these to match our plan
;
;(defn parse
;  ([input parser]
;   (let [result (parser input)]
;     (if (not (insta/failure? result))
;       result))))
;
;(defn search
;  "Search a Datomic db with a search string"
;  [db search-string & {:keys [parser transform query rules]
;                       :or {parser simple/parser
;                            transform simple/transform
;                            query simple/query}}]
;  (if-let [parse-tree (parse search-string parser)]
;    (d/q query db (into (insta/transform transform parse-tree) rules))))


