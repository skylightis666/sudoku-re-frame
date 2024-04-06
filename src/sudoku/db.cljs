(ns sudoku.db)

(defn cell-coords [idx]
  (let
   [x (mod idx 9)
    y (quot idx 9)]
    [x y]))

(defn related-cell-indices [x y]
  (let
   [box-x (quot x 3)
    box-y (quot y 3)]
    (->> (for [i (range 81)
               :let [[cell-x cell-y] (cell-coords i)]
               :when (or (= cell-x x) (= cell-y y)
                         (and (= (quot cell-x 3) box-x)
                              (= (quot cell-y 3) box-y)))]
           i))))

(defn valid-numbers [board idx]
  (let
   [[x y] (cell-coords idx)
    related-indices (related-cell-indices x y)
    related-numbers (map #(board %) related-indices)
    possible-numbers (range 1 10)]
    (remove #(contains? (set related-numbers) %) possible-numbers)))

(defn solve-sudoku-helper [board]
  (if-let [idx (first (keep-indexed #(when (nil? %2) %1) board))]
    (let [valid-nums (valid-numbers board idx)]
      (if (empty? valid-nums)
        nil
        (lazy-seq (mapcat (fn [n] (solve-sudoku-helper (assoc board idx n))) valid-nums))))
    [board]))

(defn generate-sudoku []
  (let
   [empty-board (vec (repeat 81 nil))
    solutions (solve-sudoku-helper empty-board)]
    (first solutions)))
  

(def default-db
  {:selected-cell [0 0]
   :board (generate-sudoku)})
  
