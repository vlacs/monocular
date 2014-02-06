(ns monocular.data-map
  (:require [instaparse.combinators :refer [nt hide hide-tag cat alt string ebnf]]
            [clojure.algo.generic.functor :refer [fmap]]))

;; utilities

(defn fmap-with-key
  "Similar to clojure.algo.generic.functor/fmap. Takes a map and applies f to
  each key value pair."
  [f m]
    (into (empty m) (for [[k v] m] [k (f k v)])))

;; grammar

(defn apply-alt
  [grammar]
  (if (> (count grammar) 1)
      (apply alt grammar)
      (first grammar)))

(defn keyword->grammar
  ([keyword-name keyword-data]
   (apply-alt (map #(hide (string %1)) (conj (:alias keyword-data) (name keyword-name)))))
  ([keyword-name keyword-data suffix]
   (cat (keyword->grammar keyword-name keyword-data) suffix)))

; todo: is there a better way to do this?
(defn keywords->grammar
  ([keywords]
   (fmap-with-key keyword->grammar keywords))
  ([keywords suffix]
   (fmap-with-key #(keyword->grammar %1 %2 suffix) keywords)))

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
  "Takes a monocular data map and returns the associated grammar"
  [{:keys [magic-keywords keywords]}]
  (merge (term-rule (not (empty? magic-keywords)) (not (empty? keywords)))
         (keyword-rule :magic-keyword magic-keywords)
         (keyword-rule :keyword-value keywords)
         (keywords->grammar magic-keywords)
         (keywords->grammar keywords (cat (hide (string ":")) (nt :value)))))

;; transforms

(defn keyword->transform [kw] (fn [value] (partial (:fn kw) value)))

(defn magic-keyword->transform [kw] (fn [] (:fn kw)))

(defn keywords->transforms
  [keywords pre-transform-fn]
  (if (empty? keywords) {}
      (fmap pre-transform-fn keywords)))

(defn map->transforms
  [{:keys [magic-keywords keywords default]}]
  (merge {:default (fn [value] (partial default value))}
         (keywords->transforms magic-keywords magic-keyword->transform)
         (keywords->transforms keywords keyword->transform)))
