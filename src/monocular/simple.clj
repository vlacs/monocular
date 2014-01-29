(ns monocular.simple
  (:require [instaparse.core :as insta]
            [instaparse.combinators :refer [enbf]]))    ;; we'll need more from this later when we start building gramamr

(comment

(def test-parser
  (insta/parser
    "search = term (<whitespace> term)*
     <term> = magic-keyword | !magic-keyword (keyword-value | default)
     default = value
     <value> = #'[^\"\\':\\s]+'
     whitespace = #'\\s+'

    (** generated things **)
     <magic-keyword> = one-segment-course | guggenheim
     <keyword-value> = comp | tag
     comp = (<'comp'> | <'competency'>) <':'> value
     tag = <'tag'> <':'> value
     one-segment-course = <'one-segment-course'>
     guggenheim = <'guggenheim'>
    "))

;; corresponding grammar-data-map

(def test-grammar-data-map
  {:magic-keywords {:one-seg {:alias ['one-segment-course']
                              :fn datomic-query-fn}
                    :gug {:alias ['guggenheim']
                          :fn another-query-fn}}
   :keywords {:comp {:alias ['comp'
                             'competency']
                     :fn yet-another-query-fn}
              :tag {:alias ['tag']
                    :fn i-ran-out-of-things-to-call-these-query-fn}}
   :default some-catch-all-fn})




;; misc

(def grammar
  (enbf
    "search = term (<whitespace> term)*                               (* searches are one or more terms, separated with whitespace *)
     term = key? value                                                (* terms are values or key:value pairs *)
     key = #'[\\w\\*\\+\\!\\-\\?\\.]*' <':'>                          (* keys are word (and some allowed symbol) characters ending with a colon *)
     <value> = quoted-value | raw-value                               (* values are raw or quoted... *)
     quoted-value = single-quoted-value | double-quoted-value         (* ... with single or double quotes *)
     <single-quoted-value> = <'\\''> #'[^\\']*' <'\\''>
     <double-quoted-value> = <'\"'> #'[^\"]*' <'\"'>
     raw-value = #'[^\"\\':\\s]+'
     whitespace = #'\\s+'"))

(def parser
  (insta/parser
    (merge
      grammar
      (generate-grammar grammar-data-map))))


(def query '[:find ?e :in $ % :where])






;; some scratch stuff while talking things through with Matt

  (def grammar-data-map
    {:key {:comp datomic-query-manipulation-fn
           :tag datomic-query-manipulation-fn}
     :term {:one-segment-courses [datomic datalogs to require the course to have only one segment]
            :guggenheim datomic-query-manipulation-fn
            :johnny-drop-tables (throw (Exception. "Punk."))
            ;; note gensym to help with datomic unification terms that won't collide. maybe.
            :default ["table.somefield LIKE '%<term>%' OR table.otherfield LIKE '%<term>%' etc. blah blah blah"]}
     }
    )

  (parser "name:Marketing 'Business' blah")

  (def data-mapped-grammar
    {:search "term (<whitespace> term)*                               (* searches are one or more terms, separated with whitespace *)" {:comp [:name :desc :favorite-ice-cream]}
     "term = key? value                                                (* terms are values or key:value pairs *)" {:}
     }
    "search = term (<whitespace> term)*                               (* searches are one or more terms, separated with whitespace *)
    term = key? value                                                (* terms are values or key:value pairs *)
    key = #'[\\w\\*\\+\\!\\-\\?\\.]*' <':'>                          (* keys are word (and some allowed symbol) characters ending with a colon *)
    <value> = quoted-value | raw-value                               (* values are raw or quoted... *)
    quoted-value = single-quoted-value | double-quoted-value         (* ... with single or double quotes *)
    <single-quoted-value> = <'\\''> #'[^\\']*' <'\\''>
    <double-quoted-value> = <'\"'> #'[^\"]*' <'\"'>
    raw-value = #'[^\"\\':\\s]+'
    whitespace = #'\\s+'"

    )
  (def parser (insta/parser (reduce str (keys data-mapped-grammar))))



  ;; for reference
(def parser
  (insta/parser
    "search = term (<whitespace> term)*                               (* searches are one or more terms, separated with whitespace *)
     term = key? value                                                (* terms are values or key:value pairs *)
     key = #'[\\w\\*\\+\\!\\-\\?\\.]*' <':'>                          (* keys are word (and some allowed symbol) characters ending with a colon *)
     <value> = quoted-value | raw-value                               (* values are raw or quoted... *)
     quoted-value = single-quoted-value | double-quoted-value         (* ... with single or double quotes *)
     <single-quoted-value> = <'\\''> #'[^\\']*' <'\\''>
     <double-quoted-value> = <'\"'> #'[^\"]*' <'\"'>
     raw-value = #'[^\"\\':\\s]+'
     whitespace = #'\\s+'"))


  )
