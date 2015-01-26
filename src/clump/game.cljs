(ns clump.game
  (:require [clump.utils :refer [combinations map-combinations]]
            [clump.rules :refer [clump?]]))

(def traits {:shape  [:circle :square :triangle]
             :color  [:red :green :blue]
             :fill   [:empty :striped :solid]
             :number [1 2 3]})

(def blank {:shape :blank, :color :blank, :fill :blank, :number 1})

(def deck (map-combinations traits))

(defn clumps [cards]
  (filter (partial clump? (keys traits))
          (combinations (filter #(not= :blank (:shape %)) cards)
                        3)))

(defn new-game
  ([]
   (new-game (shuffle deck)))
  ([deck]
   {:deck (vec (drop 12 deck))
    :board (vec (take 12 deck))
    :selected #{}
    :score 0}))

(defn toggle-card [game card]
  (let [selected (:selected game)]
    (assoc game :selected (if (selected card)
                            (disj selected card)
                            (conj selected card)))))

(defn board-position [board card]
  (count (take-while #(not= card %) board)))

(defn draw [game]
  (cond
    (seq (drop 12 (:board game)))
    (assoc game :draw (vec (drop 12 (:board game))))

    (seq (take 3 (:deck game)))
    (assoc game :draw (vec (take 3 (:deck game))) :deck (vec (drop 3 (:deck game))))

    :else
    (assoc game :draw (vec (repeatedly 3 (constantly blank))))))

(defn replace-clump [game clump]
  (let [game (draw game)
        old->new (zipmap (sort-by (partial board-position (:board game)) clump)
                         (sort-by #(clump %) (:draw game)))]
    (-> game
        (dissoc :draw)
        (assoc :selected #{}
               :board (vec (take 12 (map #(get old->new % %) (:board game))))))))

(defn card-selected [game card]
  (let [game (toggle-card game card)
        selected (:selected game)]
    (if (= 3 (count selected))
      (let [score (:score game)]
        (if (clump? (keys traits) selected)
          (-> game
              (replace-clump selected)
              (assoc :score (inc score)))
          (assoc game :score (dec score) :selected #{})))
      game)))

(defn hint [game]
  (first (clumps (:board game))))
