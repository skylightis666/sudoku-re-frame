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
    (print all-parts)
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
        x (Math/floor (/ idx 9)) ; row number
        y (Math/floor (mod idx 9)) ;column number
        right-border (if (or (= 2 y) (= 5 y) (= 8 y)) "block-border-right" "")
        bottom-border (if (or (= 2 x) (= 5 x) (= 8 x)) "block-border-bottom" "")]
    (fn []
      [:button {:class (str "cell " right-border " " bottom-border)
                :on-click #(re-frame/dispatch [:select-cell idx])}
       (or @cell-value " ")])))

(defn block-component [indices]
  [:div {:class "block"}
   (for [idx indices]
     ^{:key idx} [cell-component idx])])
  
(defn board-component []
  [:div {:class "board"}
   (for [i (range 0 81 9)]
     ^{:key i} [block-component (range i (+ i 9))])])

(defn set-value-button [v]
  (let [selected (re-frame/subscribe [:selected-cell])]
    (fn []
      [:button {:on-click #(re-frame/dispatch [:update-cell @selected v])} (str v)])))

(defn main-panel []
  (fn []
    [:div
     [board-component]
     (for [v (range 1 10)]
       ^{:key v} [set-value-button v])
     [:button {:on-click #(re-frame/dispatch [:complete-sudoku])} "Check Solution"]]))   