(ns sudoku.db)

(defn shift-row
  [row shift]
  (into (subvec row shift) (subvec row 0 shift)))

(shift-row (vec (shuffle (range 1 10))) 3)

(defn gen-full-sudoku []
  (let [row1 (vec (shuffle (range 1 10)))               ;; generating first row
        row2 (shift-row row1 3)                         ;; generating second row
        row3 (shift-row row2 3)                         ;; generating third row
        row4 (shift-row row3 1)                         ;; generating fourth row
        row5 (shift-row row4 3)                         ;; generating fifth row
        row6 (shift-row row5 3)                         ;; generating sixth row
        row7 (shift-row row6 1)                         ;; generating seventh row
        row8 (shift-row row7 3)                         ;; generating eighth row
        row9 (shift-row row8 3)]                         ;; generating ninth row
    [row1 row2 row3 row4 row5 row6 row7 row8 row9]))

(->> (gen-full-sudoku)
     flatten
     vec)
;; (map reverse (generate-sudoku))
  
(defn adjust [v]
  (mapv (fn [x] (if (< (rand) 0.3) "" x)) v))

(def default-db
  (let [board (-> (gen-full-sudoku)
                  flatten
                  vec
                  adjust)] 
    {:selected-cell    [0 0]
     :board            board
     :pre-filled-cells (mapv #(= "" %) board)}))
  
