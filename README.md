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

## Terms

This is free and unencumbered software released into the public domain. It was originally written by Clinton Dreisbach.
