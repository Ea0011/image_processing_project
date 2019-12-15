# Detecting the Mouth Area using Binary Layer 3 and moments of the image.

1. Binary Layer 3 effectively filters pixels near ears and lips. A bit odd behaviour here is that it may filter some pixels near the dress under the chin or area below them chin. However, the important effect heree is that most pixels that are filtered are centered around ears and lips. Hence, if we calculate the centroid of the image after applying Binary Layer 3 we are going to get a point which is approximately near the center of lips or at the tip of the nose. This gives an opportunity to draw a bounding box using the centroid to get the are around the mouth of the original image.

2. To draw an approximate bounding box, we calculate the average distance of the filtered out pixels from the centroid and draw a box around the centroid with width and height equal to that average distance.

3. To alter the behaviour of this algorithm, one can apply a min filter after Binary Layer 3 to make pixels more prominent, this can lead to better bounding box.
