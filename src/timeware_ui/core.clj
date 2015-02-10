(ns timeware-ui.core
  (:gen-class :main true)
  (:require [clj-http.client :as client])
  (:require [carica.core :refer [configurer resources]])
  (:use seesaw.core seesaw.table)
  (use [clojure.string :only (join split)]))

(defn get-data []
  (get (client/get
    (join "/" [
      (carica.core/config :url) "history" (carica.core/config :username)
     ]) {:accept :json :as :json :headers {"X-Auth-Pass" (carica.core/config :password)}}) :body))

(defn make-table []
  (table :id :table :model [
    :columns [
        { :key :info :text "Info" }
        { :key :date :text "Date" }
        { :key :schedule :text "Pattern" }
        { :key :bookings :text "Bookings" }
        { :key :absence :text "Absence" }
        { :key :total :text "Total" }
        { :key :opening :text "Opening" }
        { :key :accum :text "Accum." }
        { :key :target :text "Target" }
        { :key :closing :text "Closing" }
      ]
    :rows (get-data)]))

(defn make-frame []
  (frame :title "Timeware" :width 500 :height 400 :content
     (border-panel
       :center (scrollable (make-table))
       :south  (label :id :sel :text "Selection: "))))

(defn -main [& args]
  (let [f (show! (make-frame))
    t (select f [:#table])]
    (listen t :selection
      (fn [e]
        (config! (select f [:#sel])
          :text (str "Selection: "
            (value-at t (selection t))))))
    f))
