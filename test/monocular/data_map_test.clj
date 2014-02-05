(ns monocular.data-map-test
  (:require [monocular.data-map :refer :all]
            [instaparse.combinators :refer [nt hide hide-tag cat alt string ebnf]]
            [clojure.test :refer :all]))

(def data-map
  {:magic-keywords {:one-seg {:alias ["one-segment-course"]
                              :fn :some-fn-would-go-here }
                    :gug {:alias ["guggenheim"]
                          :fn :some-fn-would-go-here}}
   :keywords {:comp {:alias ["competency"]
                     :fn :some-fn-would-go-here}
              :tag {:fn :some-fn-would-go-here}}
   :default :some-fn-would-go-here})

(def grammar
  (merge
    {:one-seg (alt (hide (string "one-segment-course")) (hide (string "one-seg")))
     :gug (alt (hide (string "guggenheim")) (hide (string "gug")))
     :comp (cat (alt (hide (string "competency")) (hide (string "comp"))) (cat (hide (string ":")) (nt :value)))
     :tag (cat (hide (string "tag")) (cat (hide (string ":")) (nt :value)))
     :magic-keyword (hide-tag (alt (nt :one-seg) (nt :gug)))
     :keyword-value (hide-tag (alt (nt :comp) (nt :tag)))}
    (ebnf "<term> = magic-keyword | !magic-keyword (keyword-value | default)")))

(def small-map {:keywords {:winning {:fn :some-fn-would-go-here}}})

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
