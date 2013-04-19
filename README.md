# halresource

halresource is a Clojure library designed to build and represent resources in [Hypertext Application Format](http://stateless.co/hal_specification.html). It is extremely simple in nature and slightly opinionated.

## Usage

```clj
(require '[halresource.resource :as hal])

(def resource (hal/new-resource "http://example.org/people/1"))

(hal/resource->representation resource :json)
; {
;   "_links" : [ {
;     "rel" : "self",
;     "href" : "http://example.org/person/1"
;   } ]
; }

(hal/resource->representation resource :xml)
; <?xml version="1.0" encoding="UTF-8"?>
; <resource href="http://example.org/people/1"/>

(def resource (-> resource
                  (hal/add-link :rel "up"
                                :href "http://example.org/people")))
                                
(hal/resource->representation resource :json)
; {
;   "_links" : [ {
;     "rel" : "self",
;     "href" : "http://example.org/person/1"
;   }, {
;     "href" : "http://example.org/people",
;     "rel" : "up"
;   } ]
; }

(hal/resource->representation resource :xml)
; <?xml version="1.0" encoding="UTF-8"?>
; <resource href="http://example.org/people/1">
;   <link href="http://example.org/people" rel=":up"/>
; </resource>

(def resource (-> resource
                  (hal/add-resource :addresses
                    (hal/new-resource "http://example.org/people/1/addresses"))))

(hal/resource->representation resource :json)
; {
;   "_embedded" : {
;     "addresses" : [ {
;       "_links" : [ {
;         "rel" : "self",
;         "href" : "http://example.org/people/1/addresses"
;       } ]
;     } ]
;   },
;   "_links" : [ {
;     "rel" : "self",
;     "href" : "http://example.org/people/1"
;   }, {
;     "href" : "http://example.org/people",
;     "rel" : "up"
;   } ]
; }

(hal/resource->representation resource :xml)
; <?xml version="1.0" encoding="UTF-8"?>
; <resource href="http://example.org/people/1">
;   <link href="http://example.org/people" rel="up"/>
;   <resource href="http://example.org/people/1/addresses" rel="addresses"/>
; </resource>

(def resource (-> resource
                  (hal/add-properties {:name "Clinton Dreisbach"
                                       :city "Durham"
                                       :state "NC"})))

(hal/resource->representation resource :json)
; {
;   "_embedded" : {
;     "addresses" : [ {
;       "_links" : [ {
;         "rel" : "self",
;         "href" : "http://example.org/people/1/addresses"
;       } ]
;     } ]
;   },
;   "_links" : [ {
;     "rel" : "self",
;     "href" : "http://example.org/people/1"
;   }, {
;     "href" : "http://example.org/people",
;     "rel" : "up"
;   } ],
;   "city" : "Durham",
;   "name" : "Clinton Dreisbach",
;   "state" : "NC"
; }

(hal/resource->representation resource :xml)
; <?xml version="1.0" encoding="UTF-8"?>
; <resource href="http://example.org/people/1">
;   <link href="http://example.org/people" rel="up"/>
;   <city>Durham</city>
;   <name>Clinton Dreisbach</name>
;   <state>NC</state>
;   <resource href="http://example.org/people/1/addresses" rel="addresses"/>
; </resource>
```

## Nested properties

Nested properties present a unique problem, as maps and vectors are easily represented in HAL+JSON, but not so easily represented in HAL+XML. We take an opinionated approach here.

* Maps are represented as sub-elements of the property using tags and content.
* Vectors are represented as either one property element with a plural name (for example, "addresses") and sub-elements with a singular version of that name ("address"), or as multiple property elements with a singular version of that name. The way this is determined is by the name of the property. If it is a plural name, we use a parent property element. If it is a singular name, we use multiple property elements. An example:

```clj
(def resource (-> (new-resource "http://example.org/people/1")
                  (add-property :names ["Pete" "Peter" "Pecan" "P-Fizz"])
                  (add-property :speciality ["Clojure"
                                             "Breakdancing"
                                             "Viking Lore"])))

(resource->representation resource :xml)

; <?xml version="1.0" encoding="UTF-8"?>
; <resource href="http://example.org/people/1">
;   <speciality>Clojure</speciality>
;   <speciality>Breakdancing</speciality>
;   <speciality>Viking Lore</speciality>
;   <names>
;     <name>Pete</name>
;     <name>Peter</name>
;     <name>Pecan</name>
;     <name>P-Fizz</name>
;   </names>
; </resource>
```

## Terms

This is free and unencumbered software released into the public domain. It was originally written by Clinton Dreisbach.
