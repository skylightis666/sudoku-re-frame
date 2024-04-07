(ns sudoku.views
  (:require
   [re-frame.core :as re-frame]
   [sudoku.subs :as s]
   [sudoku.events :as e]))
   
(re-frame/reg-sub
  :board
  (fn [db _] (:board db)))
  
(re-frame/reg-sub
  :cell
  :<- [:board]
  (fn [board [_ idx]]
    (board idx)))

(re-frame/reg-sub
 :pre-filled-cells
 (fn [db _] (:pre-filled-cells db)))

(re-frame/reg-sub
 :cell-selectable?
 :<- [:pre-filled-cells]
 (fn [board [_ idx]]
   (nth board idx)))

(re-frame/reg-sub
  :selected-cell
  (fn [db _] (:selected-cell db)))

(re-frame/reg-event-db
  :update-cell
  (fn [db [_ idx value]]
    (assoc-in db [:board idx] value)))


(defn check-all-rows-cols-and-boxes [db]
  (let [values (mapv db (range 81))
        rows (partition 9 values)
        columns (apply map vector rows)
        squares (for [i (range 0 9 3)
                      j (range 0 9 3)]
                  (for [x (range 0 3)
                        y (range 0 3)]
                    (get values (+ (* 9 (+ i x)) (+ j y)))))
        all-parts (concat rows columns squares)]
    (and (every? #(= 45 (reduce + %)) all-parts)
         (every? #(apply distinct? %) all-parts))))

(re-frame/reg-event-db
  :complete-sudoku
  (fn [db]
    (let [solution-valid? (check-all-rows-cols-and-boxes (:board db))] ; Assuming the presence of function check-all-rows-cols-and-boxes
      (if solution-valid?
        (js/alert "Sudoku completed correctly!")
        (js/alert "Lox"))
      db)))

(re-frame/reg-event-db
  :select-cell
  (fn [db [_ idx]]
    (assoc db :selected-cell idx)))

(defn cell-component [idx]
  (let [cell-value (re-frame/subscribe [:cell idx])
        cell-selectable? (re-frame/subscribe [:cell-selectable? idx])
        selected-cell (re-frame/subscribe [:selected-cell])]
    (fn []
      [:button {:style (merge {:width "100%"
                               :height "100%"
                               :border "1px solid #999"
                               :display "flex"
                               :justifyContent "center"
                               :alignItems "center"
                               :backgroundColor (if (= @selected-cell idx) 
                                                  "#fdd835" 
                                                  (if @cell-selectable?
                                                   "#c3cdea"
                                                    "#f0f0f0"))})
                :on-click #(if @cell-selectable? 
                             (re-frame/dispatch [:select-cell idx]))}
       (or @cell-value " ")])))

(defn block-component [idx]
  [:div {:style {:display "grid"
                 :gridTemplateColumns "repeat(3, 1fr)"
                 :gridTemplateRows "repeat(3, 1fr)"
                 :border "1px solid black"
                 :boxSizing "border-box"
                 :gaps "0"}}
   (for [i (range 9)]
     ^{:key i} [cell-component (+ (* 9 idx) i)])])

(defn sudoku-grid []
  [:div {:style {:display "grid"
                 :gridTemplateColumns "repeat(3, 1fr)"
                 :gridTemplateRows "repeat(3, 1fr)"
                 :gaps "0"}}
   (for [i (range 9)]
     ^{:key i} [block-component i])])
  
(defn set-value-button [v]
  (let [selected (re-frame/subscribe [:selected-cell])]
    (fn []
      [:button {:on-click #(re-frame/dispatch [:update-cell @selected v])} (str v)])))

(defn main-panel []
  (fn []
    [:div {:style {:margin-top "250px"
                   :display "grid"
                   :gridTemplateColumns "auto auto"
                   :gap "10px"
                   :scale "250%"
                   :justifyContent "center"}}
     [:div {:style {:gridColumn 1
                    :justifySelf "center"
                    :maxWidth "600px"}}
      [sudoku-grid]] 
     [:div {:style {:gridColumn 2
                    :maxWidth "200px"
                    :display "grid"
                    :gridTemplateColumns "repeat(3, 1fr)" ;; three columns
                    :gridTemplateRows "repeat(3, 1fr)"    ;; three rows
                    :gap "5px"}}                           ;; add gaps between buttons
      (doall
       (for [v (range 1 10)]
         ^{:key v} [set-value-button v]))]
     [:button {:on-click #(re-frame/dispatch [:complete-sudoku])} "Check Solution"]])) 
                       