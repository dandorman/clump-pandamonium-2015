(ns clump.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clump.ui.utils :refer [class-names]]))

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

(defn card [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div
        #js {:className "card-container"}
        (dom/div
          #js {:className "card"}
          (dom/div
            #js {:className "face front"}
            (shape {:shape :circle
                    :color :red
                    :fill :striped})))))))

(om/root card
         nil
         {:target (.getElementById js/document "app")})
