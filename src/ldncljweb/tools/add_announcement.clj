(ns ldncljweb.tools.add-announcement
  (:require [clojure.string :as cs]))

(defn listify [v]
  (if (list? v) v [v]))

(defn conj-multiple [headers key-value]
  (if (empty? key-value) 
    headers
    (let [[raw-key value] key-value
          key (keyword raw-key)]
      (conj headers [key
                     (if (contains? headers key)
                       (conj (listify (headers key)) value)
                       value)]))))  

(defn parse-header [raw-header]
  (let [lines (cs/split-lines raw-header)
        key-values (map #(rest (re-find #"([0-9A-Za-z_-]+): (.*)" %)) lines)
        headers (reduce conj-multiple {} key-values)]
    headers))

(defn parse-message [message]
  (let [[raw-header body] (cs/split message #"\n\n" 2)]
    [(parse-header raw-header) body]))

(defn seq-matches? [a b]
  (empty? (drop-while true? (map = a b))))

(defn split-at-boundry 
  ([boundry body] (split-at-boundry (seq (str "--" boundry)) (seq body) [] [] ))
  ([boundry remainder result acc]
    (cond
      (empty? remainder) result
      (seq-matches? boundry remainder) (recur boundry
                                              (drop (count boundry) remainder)
                                              (conj result (apply str acc))
                                              [])
      :else (recur boundry
                   (rest remainder)
                   result
                   (conj acc (first remainder))))))
  
(defn content-type [message]
  (let [[headers _] message
        raw-content-type (headers :Content-Type)]
    (first (cs/split raw-content-type #";"))))

(defn parse-body-into-types [boundry body]
  (let [parts (filter #(not (empty? %)) (split-at-boundry boundry body))
        parsed-parts (map parse-message parts)]
    (reduce #(conj %1 [(content-type %2) (second %2)]) {} parsed-parts)
    ))