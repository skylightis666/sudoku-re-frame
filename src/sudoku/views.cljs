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
  (let [values (mapv db (range 16))
        rows (partition 4 values)
        columns (apply map vector rows)
        squares (for [i (range 0 4 2)
                      j (range 0 4 2)]
                  (for [x (range 0 2)
                        y (range 0 2)]
                    (get values (+ (+ i x) (* 4 (+ j y))))))
        all-parts (concat rows columns squares)]
    (print all-parts)
    (and (every? #(= 10 (reduce + %)) all-parts)
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
  (let [cell-value (re-frame/subscribe [:cell idx])]
    (fn []
      [:button {:class   "cell"
                :on-click #(re-frame/dispatch [:select-cell idx])}
       (or @cell-value "&nbsp;")])))

(defn block-component [indices]
  [:div {:class "block"}
   (for [idx indices]
     ^{:key idx} [cell-component idx])])
  
(defn board-component []
  [:div {:class "board"}
   (for [i (range 0 16 4)]
     ^{:key i} [block-component (range i (+ i 4))])])

(defn set-value-button [v]
  (let [selected (re-frame/subscribe [:selected-cell])]
    (fn []
      [:button {:on-click #(re-frame/dispatch [:update-cell @selected v])} (str v)])))

(defn main-panel []
  (fn []
    [:div
     [board-component]
     (for [v (range 1 5)]
       ^{:key v} [set-value-button v])
     [:button {:on-click #(re-frame/dispatch [:complete-sudoku])} "Check Solution"]]))   