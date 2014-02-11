# Monocular

A Clojure library designed to make creating searches easy. The name *Monocular*
was chosen as it's the stereotypical tool seen used by pirates searching the
open sea for vessels to plunder.

![monocular](http://upload.wikimedia.org/wikipedia/commons/thumb/5/51/Emblemata_1624.jpg/320px-Emblemata_1624.jpg)

## Usage

*Monocular is currently in development, and the usage outlined here is subject
to change.*

The first step to using Monocular is defining how searches map to your search
functions. In this example we're creating a search to find actors that have
played the Doctor in BBC's Doctor Who (see the complete code in
test/monocular/examples/set-and-filters.clj). Here is the data-map for our
Doctor search:

```clj
(def doctor-data-map
  {:keywords       {:name            {:alias ["fullname"] :fn filter-name}
                    :doctor          {:fn filter-doctor}}
   :magic-keywords {:new-doctors     {:fn filter-new-doctors}
                    :classic-doctors {:fn filter-classic-doctors}
                    :main-doctors    {:fn filter-main-doctors}
                    :alt-doctors     {:fn filter-alt-doctors}}
   :default filter-default})
```

Keyword searches take the form "keyword:value". Magic keywords are used to
perform predefined searches that don't require a value. The function defined by
default handles terms entered into the search that aren't keyword or magic
keyword searches. Keywords, magic keywords, and default searches are described
in more detail below, under *Data-map*. Keyword and default functions take a
search term and a set, and return a set:

```clj
(defn filter-name [s doctors]
  (filter #(.contains (str (:fname %1) " " (:lname %1)) s) doctors))
```

Magic keyword functions take a set and return a set:

```clj
(defn filter-new-doctors [doctors]
  (filter #(contains? (:tags %1) :new) doctors))
```

Once you've defined your search functions and created the data map you can
create the search. Our search will alway use the same data set (in our case
the set `doctor-recs`), so we can use defsearch:

```clj
(require '[monocular.core :as monocular])

(monocular/defsearch doctor-search doctor-data-map doctor-recs)
```

To get a set containing all classic doctors we could now do the following, using
the `classic-doctors` magic-keyword:

```clj
=> (doctor-search "classic-doctors")
({:fname "William" ...} ...)
```

If we had different data sets we needed to be able to search on we could define
a searcher:

```clj
(def doctor-searcher (monocular/searcher doctor-data-map))
```

To do the same search as before:

```clj
=> ((doctor-searcher "classic-doctors") doctor-recs)
({:fname "William" ...} ...)
```

### Data-map

Creating searchers in Monocular requires creating a map that relates search terms to
Datomic/SQL/your-special-filtering-function. Here is an example of what such a
map would look like for performing searches on a set of customers:

```clj
{:keywords       {:name   {:alias ['fullname']
                           :fn search-by-name-fn}
                  :state  {:fn search-by-state-of-residence-fn}}
 :magic-keywords {:over18 {:fn search-over-18-fn}}
 :default        search-on-all-fn}
```

#### Keywords

Keywords define searches on a value. The map above defines two keywords, "name"
and "state". Name is also searchable by the alias "fullname". In this example to
find customers named Jon a user could enter "name:Jon" or "fullname:Jon" as a
search. Monocular supports quotes, so if the user knew the full name of the
customer they were looking for was "Jon Smith" they could enter:

```
name:"Jon Smith"
```

#### Magic Keywords

Magic keywords are bare keywords to perform special functions. In the example
we've defined the magic keyword "over18". To search for users named Jon that are
over 18 years of age, one would search "name:Jon over18".

#### Default

Default defines as search function to be used when a bare word that isn't a
magic keyword is entered. In our example, such a function could search on both
state, name, or any other customer attribute. So to search for customers named
Jon, one could simple search "Jon".

### More Coming

When there is something more to use, this area will describe its usage.

## License

Copyright © 2014 VLACS http://vlacs.org

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
