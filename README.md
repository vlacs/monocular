# Monocular [![Build Status](https://travis-ci.org/vlacs/monocular.png?branch=master)](https://travis-ci.org/vlacs/monocular)

A Clojure library designed to make creating searches easy. The name
*Monocular* was chosen as it's the stereotypical tool seen used by
pirates searching the open sea for vessels to plunder.

![monocular](http://upload.wikimedia.org/wikipedia/commons/thumb/5/51/Emblemata_1624.jpg/320px-Emblemata_1624.jpg)

## Usage

*Monocular is currently in development, and the usage outlined here is
subject to change.*

The first step to using Monocular is defining how searches map to your
search functions. In this example we're creating a search to find
actors that have played the Doctor in BBC's Doctor Who (see the
complete code in test/monocular/examples/set-and-filters.clj). In this
example our data is stored in a set, and our search functions use
`filter` to narrow results. Here is the data-map for our Doctor
search:

```clj
(def doctor-data-map
  {:keywords       {:name               filter-name
                    :fullname           filter-name
                    :doctor             filter-doctor}
   :magic-keywords {:new-doctors        filter-new-doctors
                    :classic-doctors    filter-classic-doctors
                    :main-doctors       filter-main-doctors
                    :alt-doctors        filter-alt-doctors}
   :default filter-default})
```

Keyword searches take the form "keyword:value". Magic keywords are
used to perform predefined searches that don't require a value. The
function defined by default handles terms entered into the search that
aren't keyword or magic keyword searches. Keywords, magic keywords,
and default searches are described in more detail below, under
*Data-map*.

Keyword and default functions take a search term and a set, and return
a set:

```clj
(defn filter-name [s doctors]
  (filter #(.contains (str (:fname %1) " " (:lname %1)) s) doctors))
```

Magic keyword functions take a set and return a set:

```clj
(defn filter-new-doctors [doctors]
  (filter #(contains? (:tags %1) :new) doctors))
```

Once the search functions and data map have been defined we can create
the search. Our search will alway use the same data set (in our case
the set `doctor-recs`), so we can use defsearch:

```clj
(require '[monocular.core :as monocular])

(monocular/defsearch doctor-search doctor-data-map doctor-recs)
```

To get a set containing all classic doctors we could now do the
following, using the `classic-doctors` magic-keyword:

```clj
=> (doctor-search "classic-doctors")
({:fname "William" :lname "Hartnell" ...} ...)
```

If we needed the ability to search different sets we could define a
searcher instead of using defsearch:

```clj
(def doctor-searcher (monocular/searcher doctor-data-map))
```

To do the same search as before:

```clj
=> ((doctor-searcher "classic-doctors") doctor-recs)
({:fname "William" :lname "Hartnell" ...} ...)
```

### Data-map

Creating searchers in Monocular requires creating a map that relates
search terms to Datomic/SQL/your-special-filtering-function. Here is
an example of what such a map would look like for performing searches
on a set of customers:

```clj
{:keywords       {:name search-by-name-fn
                  :fullname search-by-name-fn
                  :state search-by-state-of-residence-fn}
 :magic-keywords {:over18 search-over-18-fn}
 :default        search-on-all-fn}
```

#### Keywords

Keywords define searches on a value. The map above defines three
keywords, "name", "fullname", and "state". Notice that "name" and
"fullname" call the same function, effectively creating an alias. In
this example to find customers named Jon a user could enter "name:Jon"
or "fullname:Jon" as a search. Monocular supports quotes, so if the
user knew the full name of the customer they were looking for was "Jon
Smith" they could enter:

```
name:"Jon Smith"
```

#### Magic Keywords

Magic keywords are bare keywords to perform special functions. In the
example we've defined the magic keyword "over18". To search for users
named Jon that are over 18 years of age, one would search "name:Jon
over18".

#### Default

Default defines as search function to be used when a bare word (that
isn't a magic keyword) is entered. In our example, such a function
could search on both state, name, or any other customer attribute. So
to search for customers named Jon, one could simple search "Jon".

### More Coming

When there is something more to use, this area will describe its usage.

## License

Copyright © 2014 VLACS http://vlacs.org

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
