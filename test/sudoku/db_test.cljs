(ns sudoku.db-test
  (:require
   [cljs.test :refer-macros [deftest is testing]] 
   [sudoku.events :as e]
   [re-frame.core :as r]
   [day8.re-frame.test :as t]))

(deftest test-sudoku-default-db
   (t/run-test-sync
    (r/dispatch [::e/initialize-db])
    (testing "Test the default-db of Sudoku"
      (let [board (r/subscribe [:board])
            pre-filled-cells (r/subscribe [:pre-filled-cells])]
        (is (= (count @board) 81)
            "Default Sudoku board should contain 81 cells.")

        (is (= (count @pre-filled-cells) 81)
            "pre-filled-cells should contain 81 cells.")

        (is (= (count (filter #(= "" %) @board))
               (count (filter identity @pre-filled-cells)))
            "Count of unfilled cells should be equal in board and pre-filled-cells")))))


(defn- unique-elements? [coll]
  (let [filtered-coll (filter #(not= "" %) coll)]
    (= (count filtered-coll) (count (set filtered-coll)))))

(defn- row [board n]
  (subvec board (* n 9) (+ 1 (* n 9))))

(defn- column [board n]
  (mapv #(get board (+ n (* 9 %))) (range 9)))

(defn- square [board n]
  (let [x (* 3 (mod n 3))
        y (* 3 (int (/ n 3)))
        indices (for [i (range x (+ x 3))
                      j (range y (+ y 3))]
                  (+ i (* 9 j)))]
    (mapv board indices)))

(deftest test-init-board
  (t/run-test-sync
    (r/dispatch [::e/initialize-db])
    (let [board @(r/subscribe [:board])]
      (testing "Test the initial Sudoku board"
        ;; Test the rows
        (dotimes [i 9]
          (is (unique-elements? (row board i))
              (str "Sudoku row " i " does not contain unique numbers")))
        ;; Test the columns
        (dotimes [i 9]
          (is (unique-elements? (column board i))
              (str "Sudoku column " i " does not contain unique numbers")))
        ;; Test the squares
        (dotimes [i 9]
          (is (unique-elements? (square board i))
              (str "Sudoku square " i " does not contain unique numbers")))))))