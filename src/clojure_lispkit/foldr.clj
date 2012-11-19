(defn foldr
  "reduce collection from the end"
  [f coll]
  (reduce #(f %2 %1) (reverse coll))) ; hat tip to David Sletten
