(ns monocular.core
  (:require [instaparse.core :as insta]
            [datomic.api :as d]))

(def search-parser
  (insta/parser
    "search = term (<whitespace> term)*                               (* searches are one or more terms, separated with whitespace *)
     term = key? value                                                (* terms are values or key:value pairs *)
     key = #'[\\w\\*\\+\\!\\-\\?\\.]*' <':'>                          (* keys are word (and some allowed symbol) characters ending with a colon *)
     value = quoted-value | raw-value                                 (* values are raw or quoted... *)
     <quoted-value> = single-quoted-value | double-quoted-value       (* ... with single or double quotes *)
     <single-quoted-value> = <'\\''> #'[^\\']*' <'\\''>
     <double-quoted-value> = <'\"'> #'[^\"]*' <'\"'>
     <raw-value> = #'[^\"\\':\\s]+'
     whitespace = #'\\s+'"))
