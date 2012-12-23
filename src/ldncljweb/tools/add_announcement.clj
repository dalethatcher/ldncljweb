(ns ldncljweb.tools.add-announcement
  (:require [clojure.string :as cs])
  (:gen-class))

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

(defn parse-boundary-from-header [header]
  (let [ct (header :Content-Type)
        [_ boundary] (re-matches #".*boundary=(.*)" "multipart/alternative; boundary=1234")]
        boundary))

(defn seq-matches? [a b]
  (empty? (drop-while true? (map = a b))))

(defn split-at-boundary 
  ([boundary body] (split-at-boundary (seq (str "--" boundary)) (seq body) [] [] ))
  ([boundary remainder result acc]
    (cond
      (empty? remainder) result
      (seq-matches? boundary remainder) (recur boundary
                                              (drop (count boundary) remainder)
                                              (conj result (apply str acc))
                                              [])
      :else (recur boundary
                   (rest remainder)
                   result
                   (conj acc (first remainder))))))
  
(defn content-type [message]
  (let [[headers _] message
        raw-content-type (headers :Content-Type)]
    (first (cs/split raw-content-type #";"))))

(defn parse-body-into-types [boundary body]
  (let [parts (filter #(not (empty? %)) (split-at-boundary boundary body))
        parsed-parts (map parse-message parts)]
    (reduce #(conj %1 [(content-type %2) (second %2)]) {} parsed-parts)))

(defn parse-message-by-type [message]
  (let [[header body] (parse-message message)
        boundary (parse-boundary-from-header header)
        body-parts (parse-body-into-types boundary body)] 
  body-parts))

(defn -main [& args]
  (let [raw-message (slurp *in*)
        message (parse-message raw-message)]
    (println message)))