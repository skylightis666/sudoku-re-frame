(ns sudoku.views
  (:require
   [re-frame.core :as re-frame]
   [sudoku.subs :as s]
   [sudoku.events :as e]))
   
(re-frame/reg-sub
  :board
  (fn [db _] db))
  
(re-frame/reg-sub
  :cell
  :<- [:board]
  (fn [board [_ idx]]
    (board idx)))

(re-frame/reg-event-db
  :update-cell
  (fn [db [_ idx value]]
    (assoc db idx value)))


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
    (and (every? #(= 10 (reduce + %)) all-parts)
         (every? #(apply distinct? %) all-parts))))

(re-frame/reg-event-db
  :complete-sudoku
  (fn [db]
    (let [solution-valid? (check-all-rows-cols-and-boxes db)] ; Assuming the presence of function check-all-rows-cols-and-boxes
      (if solution-valid?
        (js/alert "Sudoku completed correctly!")
        (js/alert "Lox"))
      db)))

(defn cell-component [idx]
  (let [cell-value (re-frame/subscribe [:cell idx])]
    (fn []
      [:input {:type    "number"
               :class   "cell"
               :value   (or @cell-value "")
               :on-change #(let [v (-> % .-target .-value)]
                             (re-frame/dispatch [:update-cell idx (js/parseInt v)]))}]) ))

(defn block-component [indices]
  [:div {:class "block"}
   (for [idx indices]
     ^{:key idx} [cell-component idx])])
  
(defn board-component []
  [:div {:class "board"}
   (for [i (range 0 16 4)]
     ^{:key i} [block-component (range i (+ i 4))])])

(defn main-panel []
  (fn []
    [:div
     [board-component]
     [:button {:on-click #(re-frame/dispatch [:complete-sudoku])} "Check Solution"] ] )) 