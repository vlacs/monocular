(ns monocular.data-map-test
  (:require [monocular.data-map :refer :all]
            [instaparse.combinators :refer [nt hide hide-tag cat alt string ebnf]]
            [clojure.test :refer :all]))

(def data-map
  {:magic-keywords {:one-seg :some-fn-would-go-here
                    :gug :some-fn-would-go-here
                    :guggenheim :some-fn-would-go-here}
   :keywords {:comp :some-fn-would-go-here
              :competency :some-fn-would-go-here
              :tag :some-fn-would-go-here}
   :default :some-fn-would-go-here})

(def grammar
  (merge
    {:one-seg (hide (string "one-seg"))
     :gug (hide (string "gug"))
     :guggenheim (hide (string "guggenheim"))
     :comp (cat (hide (string "comp")) (cat (hide (string ":")) (nt :value)))
     :competency (cat (hide (string "competency")) (cat (hide (string ":")) (nt :value)))
     :tag (cat (hide (string "tag")) (cat (hide (string ":")) (nt :value)))
     :magic-keyword (hide-tag (alt (nt :guggenheim) (nt :one-seg) (nt :gug)))
     :keyword-value (hide-tag (alt (nt :competency) (nt :comp) (nt :tag)))}
    (ebnf "<term> = magic-keyword | !magic-keyword (keyword-value | default)")))

(def small-map {:keywords {:winning :some-fn-would-go-here}})

(def small-grammar
  (merge
    {:keyword-value (hide-tag (nt :winning))
     :winning (cat (hide (string "winning")) (cat (hide (string ":")) (nt :value)))}
    (ebnf "<term> = keyword-value | default")))

(deftest map->grammar-test
  (testing "map->grammar"
  ; todo: write some [more] tests here
    (is (= (map->grammar data-map) grammar))
    (is (= (map->grammar small-map) small-grammar))))
