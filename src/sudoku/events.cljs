(ns sudoku.events
  (:require
   [re-frame.core :as re-frame]
   [sudoku.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))
