(ns clump.game
  (:require [clump.utils :refer [map-combinations]]
            [clump.rules :refer [clump?]]))

(def traits {:shape  [:circle :square :triangle]
             :color  [:red :green :blue]
             :fill   [:empty :striped :solid]
             :number [1 2 3]})

(def deck (map-combinations traits))

(defn new-game
  ([]
   (new-game (shuffle deck)))
  ([deck]
   {:deck (vec (drop 12 deck))
    :board (vec (take 12 deck))
    :selected #{}}))

(defn toggle-card [game card]
  (let [selected (:selected game)]
    (assoc game :selected (if (selected card)
                            (disj selected card)
                            (conj selected card)))))

(defn card-selected [game card]
  (assoc (toggle-card game card)))
