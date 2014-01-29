(ns monocular.data-map
  (:require [instaparse.combinators :refer [nt ebnf hide cat]]))

(def base-grammar
  (ebnf
    "search = term (<whitespace> term)*
     <term> = magic-keyword | !magic-keyword (keyword-value | default)
     default = value
     <value> = #'[^\"\\':\\s]+'
     whitespace = #'\\s+'"))

(defn map->grammar
  "Takes a monocular data map and returns the associated grammar"
  [data-map]
  (merge (reduce cat (map nt (keys (merge (:magic-keywords data-map) (:keywords data-map)))))
         (magic-keywords->grammar (:magic-keywords data-map))
         (keywords->grammar (:keywords data-map))))
