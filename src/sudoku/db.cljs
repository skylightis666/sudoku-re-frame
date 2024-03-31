(ns sudoku.db)

(def default-db
  (reduce (fn [m v] (assoc m v v)) {} (range 16)))
