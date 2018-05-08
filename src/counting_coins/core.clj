;; author: elias julian marko garcia
;; version: 04.05.18
;;
;; summary: this program implements a multi-threaded solution to the coin
;; counting game

(ns counting-coins.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [java.util.concurrent.Executors])
  (:gen-class))


(def input_test10 (str (System/getProperty "user.dir") "/10.txt"))
(def input_test10k (str (System/getProperty "user.dir") "/10000.txt"))

(defn write-results
  "writes the `results` of the computation to a file, results.dat"
  [results]
  ;; make sure the list passed to write-results has a "\n" at the end of it.
  (spit "results.dat" (string/join " " results) :append true))


(defn extract-coins
  "applies regex to coin file `f` to remove any non-coin values, i.e. integers,
  splits them with a space, and then parses them into integer values"
  [f]
  (map #(Integer/parseInt %)
       (re-seq #"\d+"
               (slurp f))))

(defn get-even-sum
  "returns the sum of all even integers currently in `coins` list"
  [coins]
  (reduce +
          (take-nth 2 (rest coins))))

(defn get-odd-sum
  "returns the sum of all odd integers currently in `coins` list"
  [coins]
  (reduce +
          (take-nth 2 coins)))

(defn get-end-sums
  "returns the sum of the even and odd sums for the coin list if last token is
  taken"
  [coins]
  (+ (get-odd-sum (butlast coins))
     (get-even-sum (butlast coins))))

(defn get-start-sums
  "returns the sum of the even and odd sums for the coin list if first token
  is taken"
  [coins]
  (+ (get-odd-sum (drop 1 (coins)))
     (get-even-sum (drop 1 coins))))


(defn lets-play-a-game
  "plays the coin game on a street corner somewhere in london, circa 1870s"
  [p1 p2 coins turns]
  (cond
    (= turns 0) (do
                  (println "p1:" p1 "p2:" p2)
                  [p1 p2 "\n"])
    (even? turns) (if (>= (get-even-sum coins)
                           (get-odd-sum coins))
                     (recur (+ p1 (last coins))
                            p2
                            (butlast coins)
                            (dec (count coins)))
                     (recur (+ p1 (first coins))
                            p2
                            (drop 1 coins)
                            (dec (count coins))))
    (odd? turns)(if (>= (get-even-sum coins)
                          (get-odd-sum coins))
                    (recur p1
                           (+ p2 (first coins))
                           (drop 1 coins)
                           (dec (count coins)))
                    (recur p1
                           (+ p2 (last coins))
                           (butlast coins)
                           (dec (count coins))))))

(defn run-concurrently
  "run the coin game on `n` threads"
  [coins n]
   (let [thread-pool (java.util.concurrent.Executors/newFixedThreadPool n)]
     (try
       (doseq [future (.invokeAll thread-pool (lets-play-a-game 0 0 coins (count coins)))]
         (.get future))
       (catch Exception e
              (println (str "Error while multithreading: " e)))
    (finally
      (.shutdown thread-pool)))))

(defn -main
  [file n]
  (try
    (println (str file))
     (time
      (let [coins (extract-coins file)]
        (time (run-concurrently coins n))))
     (catch Exception e
       (println "counting-coins usage: lein run <FILENAME> <#THREADS>\n")
       (println "<FILENAME> is the full name of the textfile in the local directory.")
       (println "<#THREADS> is the number of threads to run the program on."))))
