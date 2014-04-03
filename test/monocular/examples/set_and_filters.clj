(ns monocular.examples.set-and-filters
  ^{:doc "Example showing how to use Monocular with Clojure sets and filter."}
  (:require [monocular.core :as monocular]
            [clojure.set :refer [union]]))

(defrecord Doctor [fname lname doctor tags])
(def doctor-recs #{(Doctor. "Peter" "Capaldi" 12 #{:new})
                   (Doctor. "Toby" "Jones" "The Dream Lord" #{:new :alt})
                   (Doctor. "Matt" "Smith" 11 #{:new})
                   (Doctor. "David" "Tennant" 10 #{:new})
                   (Doctor. "Christopher" "Eccleston" 9 #{:new})
                   (Doctor. "John" "Hurt" "The War Doctor" #{:new :alt})
                   (Doctor. "Paul" "McGann" 8 #{:classic})
                   (Doctor. "Sylvester" "McCoy" 7 #{:classic})
                   (Doctor. "Geoffrey" "Hughes" "The Valeyard as Mr Popplewick" #{:classic :alt})
                   (Doctor. "Michael" "Jayston" "The Valeyard" #{:classic :alt})
                   (Doctor. "Colin" "Baker" 6 #{:classic})
                   (Doctor. "Peter" "Davison" 5 #{:classic})
                   (Doctor. "Tom" "Baker" 4 #{:classic})
                   (Doctor. "Jon" "Pertwee" 3 #{:classic})
                   (Doctor. "Patrick" "Troughton" 2 #{:classic})
                   (Doctor. "William" "Hartnell" 1 #{:classic})})

(defn filter-name [s doctors]
  (filter #(.contains (str (:fname %1) " " (:lname %1)) s) doctors))

(defn filter-doctor [s doctors]
  (filter #(or (= s (:doctor %1)) (.contains (str (:doctor %1)) s)) doctors))

(defn filter-default [s doctors]
  (union (filter-name s doctors)
         (filter-doctor s doctors)))

(defn filter-new-doctors [doctors]
  (filter #(contains? (:tags %1) :new) doctors))

(defn filter-classic-doctors [doctors]
  (filter #(contains? (:tags %1) :classic) doctors))

(defn main-doctor? [doctor]
  (not (contains? (:tags doctor) :alt)))

(defn filter-main-doctors [doctors]
  (filter main-doctor? doctors))

(defn filter-alt-doctors [doctors]
  (filter (complement main-doctor?) doctors))

(def doctor-data-map
  {:keywords       {:name               filter-name
                    :fullname           filter-name
                    :doctor             filter-doctor}
   :magic-keywords {:new-doctors        filter-new-doctors
                    :classic-doctors    filter-classic-doctors
                    :main-doctors       filter-main-doctors
                    :alt-doctors        filter-alt-doctors}
   :default filter-default})

;; for when you always use the same data set, lets you search like:
;; (doctor-search search-string)
(monocular/defsearch doctor-search doctor-data-map doctor-recs)

;; lets you search different data sets like:
;; => ((doctor-searcher search-string) data-set)
(def doctor-searcher (monocular/searcher doctor-data-map))

(comment
  ;; search for "Baker"
  (doctor-search "Baker")

  ;; search for all classic Doctors
  (doctor-search "classic-doctors")

  ;; search for all classic main Doctors
  (doctor-search "classic-doctors main-doctors")

  ;; get just Doctor 12
  (doctor-search "doctor:12")

  ;; get the Valeyard Doctor with "Hughes" in his name
  (doctor-search "doctor:Valeyard name:Hughes")

  ;; get William Hartnell
  (doctor-search "doctor:\"William Hartnell\"")   ;; note that the actual search string here is 'doctor:"William Hartnell"'

  )
