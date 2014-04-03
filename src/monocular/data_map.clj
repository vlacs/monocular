(ns monocular.data-map
  (:require [instaparse.combinators :refer [nt hide hide-tag cat alt string ebnf]]
            [clojure.algo.generic.functor :refer [fmap]]))

;; grammar

(defn apply-alt
  [grammar]
  (if (> (count grammar) 1)
      (apply alt grammar)
      (first grammar)))

(defn keyword->grammar
  "Generates instaparse grammar for single keyword/magic-keyword"
  ([keyword-name]
     (hide (string (name keyword-name))))
  ([keyword-name suffix]
     (cat (keyword->grammar keyword-name) suffix)))

(defn keywords->grammar
  ([keywords]
     (zipmap keywords (map keyword->grammar keywords)))
  ([keywords suffix]
     (zipmap keywords (map #(keyword->grammar %1 suffix) keywords))))

(defn keyword-rule [key-type keywords]
  (if (empty? keywords) {}
      {key-type (hide-tag (apply-alt (map #(nt %1) (keys keywords))))}))

(defn term-rule [have-magic-keywords have-keywords]
  (ebnf (str "<term> = "
             (cond (and have-magic-keywords have-keywords) "magic-keyword | !magic-keyword (keyword-value | default)"
                   have-magic-keywords "magic-keyword | !magic-keyword default"
                   have-keywords "keyword-value | default"
                   :else "default"))))

(defn map->grammar
  "Takes a monocular data map and returns instaparse grammar."
  [{:keys [magic-keywords keywords]}]
  (merge (term-rule (not (empty? magic-keywords)) (not (empty? keywords)))
         (keyword-rule :magic-keyword magic-keywords)
         (keyword-rule :keyword-value keywords)
         (keywords->grammar (keys magic-keywords))
         (keywords->grammar (keys keywords) (cat (hide (string ":")) (nt :value)))))

;; transforms

(defn keywords->transforms
  [keywords pre-transform-fn]
  (if (empty? keywords) {}
      (fmap pre-transform-fn keywords)))

(defn map->transforms
  "Takes a monocular data map and returns instaparse transforms. When the transforms are applied to the parse tree, keywords/default"
  [{:keys [magic-keywords keywords default]}]
  (merge {:default (partial partial default)}
         (keywords->transforms magic-keywords #(fn [] %1))
         (keywords->transforms keywords #(partial partial %1))))

(comment
  (map->grammar
   {:magic-keywords {:one-seg :some-fn-would-go-here
                     :gug :some-fn-would-go-here}
    :keywords {:comp :some-fn-would-go-here
               :tag :some-fn-would-go-here}
    :default :some-fn-would-go-here}))
