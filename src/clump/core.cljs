(ns clump.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

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
            (dom/svg
              #js {:viewBox "0 0 100 100"}
              (dom/circle
                #js {:className "red striped"
                     :cx 50 :cy 50 :r 45}))))))))

(om/root card
         nil
         {:target (.getElementById js/document "app")})
