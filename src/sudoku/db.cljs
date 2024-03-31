(ns sudoku.db)

(def default-db
  {:selected-cell [0 0]
   :board (reduce (fn [m v] (assoc m v v)) {} (range 16))})
