(ns sudoku.views-test
  (:require
   [cljs.test :refer [deftest is]]
   [sudoku.views]
   [sudoku.events :as e]
   [re-frame.core :as r] 
   [day8.re-frame.test :as t]))

(deftest update-cell
  (t/run-test-sync
   (r/dispatch [::e/initialize-db])
   (let [current-db (r/subscribe [:board])
         expected 2]
     (r/dispatch [:update-cell 0 2])
     (is (= expected (@current-db 0))))))

(deftest select-cell
  (t/run-test-sync
   (r/dispatch [::e/initialize-db])
   (let [current-db (r/subscribe [:selected-cell])
         expected 5] 
     (r/dispatch [:select-cell 5])
     (is (= expected @current-db)))))