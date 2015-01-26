(ns clump.rules)

(defn distinct-traits [cards trait]
  (->> cards
       (map trait)
       (remove nil?)
       (distinct)))

(defn clump? [traits cards]
  (let [trait-counts (map (comp count
                                (partial distinct-traits cards))
                          traits)]
    (when (every? #{1 (count cards)} trait-counts)
      cards)))
