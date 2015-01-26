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
    :board (vec (take 12 deck))}))
