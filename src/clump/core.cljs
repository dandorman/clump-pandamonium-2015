(ns clump.core
  (:require-macros [cljs.core.async.macros :as m :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [chan put! <!]]
            [clump.game :as game]
            [clump.ui.utils :refer [alert class-names]]))

(def game-state (atom {}))

(def game-history (atom []))
(add-watch game-state :history
           (fn [_ _ _ new-state]
             (when-not (= (last @game-history) new-state)
               (swap! game-history conj new-state))))

(defn new-game! []
  (swap! game-history empty)
  (swap! game-state #(merge % (game/new-game)))
  game-state)

(defn undo! []
  (when (> (count @game-history) 1)
    (swap! game-history pop)
    (reset! game-state (last @game-history))))

(defn hint! [data]
  (if-let [clump (game/hint @data)]
    (om/transact! data :board (fn [_] (map #(if ((set clump) %) (assoc % :highlight true) %) (:board @data))))
    (alert "No clumps?")))

(defn shape-wrapper [contents]
  (dom/svg #js {:viewBox "0 0 100 100"}
           contents))

(defmulti shape :shape)

(defmethod shape :circle [{:keys [color fill]}]
  (shape-wrapper
    (dom/circle
      #js {:className (class-names [color fill])
           :cx 50 :cy 50 :r 45})))

(defmethod shape :square [{:keys [color fill]}]
  (shape-wrapper
    (dom/rect
      #js {:className (class-names [color fill])
           :x 5 :y 5 :width 90 :height 90})))

(defmethod shape :triangle [{:keys [color fill]}]
  (shape-wrapper
    (dom/polygon
      #js {:className (class-names [color fill])
           :points "10 85, 50 16, 90 85"})))

(defn card [{:keys [traits selected]} owner opts]
  (reify
    om/IRender
    (render [this]
      (dom/div
        #js {:className "card-container"
             :onClick #(put! (:card-selected opts) traits)}
        (dom/div
          #js {:className (class-names {"card" true
                                        "selected" (selected traits)
                                        "highlight" (:highlight traits)})}
          (apply dom/div
                 #js {:className "face front"}
                 (repeatedly (:number traits)
                             (partial shape traits))))))))

(defn board [data owner opts]
  (reify
    om/IWillMount
    (will-mount [this]
      (go (loop []
            (let [card (<! (:card-selected opts))]
              (om/update! data
                          (game/card-selected @data card)))
            (recur))))
    om/IRender
    (render [this]
      (apply dom/div
             #js {:className "board"}
             (om/build-all
               card
               (map #(hash-map :traits % :selected (:selected data))
                    (:board data))
               {:opts opts})))))

(defn game-ui [data owner opts]
  (reify
    om/IRender
    (render [this]
      (dom/div
        nil
        (dom/h1 nil (str "Score: " (:score data)))
        (om/build board data {:opts opts})
        (dom/div nil
                 (dom/button #js {:onClick #(hint! data)} "Hint")
                 (dom/button #js {:onClick #(undo!)} "Undo"))))))

(om/root game-ui
         (new-game!)
         {:target (.getElementById js/document "app")
          :opts {:card-selected (chan)}})
