(ns halresource.resource
  (:require [inflections.core :refer [plural singular]]
            [cheshire.core :as json]
            [clojure.data.xml :as xml]))

(defrecord Resource [href links embedded properties])

(defn new-resource
  "Create a new empty resource, given a href for the resource."
  
  [href]
  (->Resource href [] [] {}))

(defn add-link
  "Add a link to the resource. Arguments are named parameters and
   should include the following:

   * :rel
   * :href

   Any other parameters will be attributes on the link."
  
  [resource & args]
  (let [link (apply hash-map args)]
    (update-in resource [:links] #((fnil conj []) % link))))

(defn add-resource
  "Add an embedded resource to the resource. You must specify a type
   for the embedded resource, which will be used to group them in
   HAL+JSON or as the rel attribute in HAL+XML."

  [resource type embedded]
  (update-in resource [:embedded] #((fnil conj []) % [(name type) embedded])))

(defn add-property 
  "Add one or more properties to the resource. These properties are
   listed as named parameters, like so:

   (add-property resource :size \"S\" :color \"green\")

   These will be merged with the current properties."

  [resource & args]
  (let [properties (apply hash-map args)]
    (update-in resource [:properties] #((fnil merge {}) % properties))))

(defn add-properties 
  "Add one or more properties to the resource from a map. These will
   be merged with the current properties."

  [resource properties]
  (update-in resource [:properties] #((fnil merge {}) % properties)))

(declare json-representation xml-representation)

(defmulti resource->representation
  "Given a resource, create the HAL representation of that
   resource. Valid representation-type values are :xml and :json."

  (fn [_ representation-type]
    representation-type))

(defmethod resource->representation :json [resource _]
  (let [representation (json-representation resource)]
    (json/generate-string representation)))

(defmethod resource->representation :xml [resource _]
  (-> (xml-representation resource)
      xml/sexp-as-element
      xml/emit-str))

(defn- json-representation [resource]
  (let [links (-> [{:rel "self" :href (:href resource)}]
                  (concat (:links resource)))
        embedded (into {}
                       (map (fn [[k resources]]
                              [(plural k)
                               (map (comp json-representation second)
                                    resources)])
                            (group-by first (:embedded resource))))
        representation (merge (:properties resource)
                              {:_links links})
        representation (if (empty? embedded)
                         representation
                         (merge representation {:_embedded embedded}))]
    representation))

(defn- xml-property [property value]
  (cond
   (map? value) [property (for [[k v] value]
                            (xml-property k v))]
   (coll? value) (if (= property (plural property))
                   [property (for [v value]
                               (xml-property (singular property) v))]
                   (for [v value]
                     (xml-property property v)))
   :else [property {} value]))

(defn- xml-representation [resource]
  (let [href (:href resource)
        rel (:rel resource)]
    [:resource (if rel
                 {:href href :rel rel}
                 {:href href})
     (for [link (:links resource)]
       [:link link])
     (for [[property value] (:properties resource)]
       (xml-property property value))
     (for [[rel resource] (:embedded resource)]
       (xml-representation (merge resource {:rel rel})))]))
