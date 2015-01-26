(ns clump.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn shape-wrapper [contents]
  (dom/svg #js {:viewBox "0 0 100 100"}
           contents))

(defn circle [class-name]
  (shape-wrapper (dom/circle
                   #js {:className class-name
                        :cx 50 :cy 50 :r 45})))

(defn square [class-name]
  (shape-wrapper
    (dom/rect
      #js {:className class-name
           :x 5 :y 5 :width 90 :height 90})))

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
            (circle "red striped")))))))

(om/root card
         nil
         {:target (.getElementById js/document "app")})
