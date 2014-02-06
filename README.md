# Monocular

<img src="http://upload.wikimedia.org/wikipedia/commons/thumb/5/51/Emblemata_1624.jpg/320px-Emblemata_1624.jpg"
    alt="monocular" title="monocular" align="right" />

A Clojure library designed to make creating searches easy. The name *Monocular*
was chosen as it's the stereotypical tool seen used by pirates searching the
open sea for vessels to plunder.

## Usage

Currently Monocular doesn't do much. To create a searcher you can enter:

```clj
=> (def my-searcher (monocular.core/searcher my-data-map))
```

And to perform searches (currently returns an
[Instaparse](https://github.com/Engelberg/instaparse) parse tree) you can enter:

```clj
=> (monocular.core/search my-searcher search-string)
[:search [...]]
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
