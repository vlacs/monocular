(ns monocular.core
  (:require [instaparse.core :as insta]
            [datomic.api :as d]
            [monocular.simple :as simple]))

(defn parse
  ([input parser]
   (let [result (parser input)]
     (if (not (insta/failure? result))
       result))))

(defn search
  "Search a Datomic db with a search string"
  [db search-string & {:keys [parser transform query rules]
                       :or {parser simple/parser
                            transform simple/transform
                            query simple/query}}]
  (if-let [parse-tree (parse search-string parser)]
    (d/q query db (into (insta/transform transform parse-tree) rules))))
